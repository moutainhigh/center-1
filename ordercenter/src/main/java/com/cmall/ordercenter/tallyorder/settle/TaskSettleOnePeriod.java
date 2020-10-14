package com.cmall.ordercenter.tallyorder.settle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * @deprecated
 * 对一个结算周期进行结算
 * @author zht
 *
 */
public class TaskSettleOnePeriod implements Runnable {
	/**
	 * 结算类型
	 * 4497477900040001  常规结算
	 * 4497477900040002  跨境保税
	 * 4497477900040003  跨境直邮
	 * 4497477900040004  平台入驻
	 */
	private String settleType;
	
	/**
	 * 帐期类型
	 * 4497477900030001  月结
	 * 4497477900030002  半月结
	 */
	private String accountType;
	
	private Map<String, Object> settlePeriod;
	private static final int ThreadSizeInPool = 20;
	private ExecutorService service = Executors.newFixedThreadPool(ThreadSizeInPool + 2);
	
	public TaskSettleOnePeriod(Map<String, Object> settlePeriod) {
		this.settlePeriod = settlePeriod;
		this.accountType = (String) settlePeriod.get("account_type");
		this.settleType = (String) settlePeriod.get("settle_type");
	}
	
	@Override
	public void run() {
		String saleStartDate = settlePeriod.get("sale_begin_date").toString();  	// 结算开始时间
		String saleEndDate = settlePeriod.get("sale_end_date").toString();      	// 结算结束时间
		String returnStartDate = settlePeriod.get("return_begin_date").toString();  	// 退货开始时间
		String returnEndDate = settlePeriod.get("return_end_date").toString();      	// 退货结束时间				
		
		//查询结算周期内有订单支付成功的供应商
		List<String> successSellerList = getSuccessSellerList(saleStartDate, saleEndDate, accountType);
		List<List<String>> itemSellerList = split(successSellerList, successSellerList.size() / (ThreadSizeInPool / 2) );
		for(List<String> itemSellers : itemSellerList) {
//			SettleSkuDealed tss = new SettleSkuDealed(itemSellers, accountType, settlePeriod);
//			service.execute(tss);
		}
		//查询结算周期内有退货单的供应商
		List<String> returnSellerList = getReturnSellerList(returnStartDate, returnEndDate, accountType);
		itemSellerList = split(returnSellerList,  returnSellerList.size() / (ThreadSizeInPool / 2) );
		for(List<String> itemSellers : itemSellerList) {
//			SettleSkuReturned tss = new SettleSkuReturned(itemSellers, accountType, settlePeriod);
//			service.execute(tss);
		}
		
		service.shutdown();
        while(true) {
            if(service.isTerminated()){
                break;
            }
            try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        getSkuDetail();
        //本结算周期的财务汇总数据
        finanlBillAmount();
        System.out.println("商户结算单生成完毕！");
	}
	
	/**
	 * 将一个集合拆分成count大小的多个小集合
	 * 
	 * @param originList 原集合
	 * @param count 每个小集合的大小,最后一个集合元素个数可能小于count
	 * @return
	 */
	private <T> List<List<T>> split(List<T> originList, int count) {
		List<List<T>> result = new ArrayList<List<T>>();
		if (originList == null || originList.size() == 0)
			return result;
		
		int size = originList.size();
		if (size <= count || count == 0) {
			// 数据量不足count指定的大小
			result.add(originList);
		} else {
			int pre = size / count;
			int last = size % count;
			// 前面pre个集合，每个大小都是count个元素
			for (int i = 0; i < count; i++) {
				List<T> itemList = new ArrayList<T>();
				for (int j = 0; j < pre; j++) {
					itemList.add(originList.get(i * pre + j));
				}
				result.add(itemList);
			}
			// last的进行处理
			if (last > 0) {
				List<T> itemList = new ArrayList<T>();
				for (int i = 0; i < last; i++) {
					itemList.add(originList.get(pre * count + i));
				}
				result.add(itemList);
			}
		}
		return result;
	}

	/**
	 * 取得月结或半月结对应的开始日期和结束日期内有成交订单的商户编号
	 * @param start_time 与结算类型对应的开始日期
	 * @param end_time 与结算类型对应的结束日期
	 * @param accountType 月结 4497477900030001  半月结 4497477900030002
	 * @return
	 * @throws Exception
	 */
	private List<String> getSuccessSellerList(String start_time, String end_time, String accountType) {
		List<String> sellerList = new ArrayList<String>();
		try {
			MDataMap map = new MDataMap();
			map.put("create_time_from", start_time);
			map.put("create_time_end", end_time);
			//交易成功
			map.put("now_status", "4497153900010005");  
			//在线支付
			map.put("pay_type", "449716200001"); 
			
			//只结算普通商户
			String settleTypeWhere = " AND si.uc_seller_type='4497478100050001' ";
			
			if(accountType.equals("4497477900030001")) {
				//月结
				map.put("account_clear_type", "4497478100030003");
				//原商户结算类型可能为空
				settleTypeWhere += "AND (si.account_clear_type=:account_clear_type OR si.account_clear_type is null OR si.account_clear_type ='')";
			} else if(accountType.equals("4497477900030002")) {
				//半月结
				map.put("account_clear_type", "4497478100030004");
				settleTypeWhere += "AND si.account_clear_type=:account_clear_type";
			}
			
			//sql中增加了商户条件in ('SI2003','SI3003')
			String sql = "SELECT " +
					"small_seller_code " +
					"FROM " +
						"usercenter.uc_seller_info_extend si " +
					"WHERE " +
						"si.small_seller_code IN (" +
							"SELECT DISTINCT " +
								"b.small_seller_code AS small_seller_code " +
							"FROM " +
								"(" +
									"SELECT " +
										"code  " +
									"FROM " +
										"logcenter.lc_orderstatus " +
									"WHERE " +
										"now_status=:now_status " +
									"AND create_time>=:create_time_from " +
									"AND create_time<=:create_time_end " +
								") a, ordercenter.oc_orderinfo b " +
							"WHERE a.code = b.order_code " +
							"AND b.seller_code IN ('SI2003', 'SI3003') " +
						//	"AND b.small_seller_code LIKE 'SF031%' " +
							"AND b.pay_type=:pay_type " +
						") " + settleTypeWhere;
			
			List<Map<String, Object>> list = DbUp.upTable("lc_orderstatus").dataSqlList(sql, map);
			if (null != list && !list.isEmpty()) {
				for(Map<String, Object> seller : list) {
					String smallSellerCode = (String) seller.get("small_seller_code");
					if(StringUtils.isNotEmpty(smallSellerCode)) {
						sellerList.add((String) seller.get("small_seller_code"));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sellerList;
	}
	
	
	/**
	 * 自然月有效退货订单号
	 * @param smallSellerCode 商户编号
	 * @param returnStart 退货开始时间
	 * @param returnEnd 退货结束时间
	 * @param saleStart 销售开始时间
	 * @param saleEnd 销售结束时间
	 * @return
	 * @throws Exception
	 */
	private List<String> getReturnSellerList(String returnStart, String returnEnd, String accountType) {
		List<String> sellerList = new ArrayList<String>();
		try {
			MDataMap map = new MDataMap();
			map.put("create_time_from", returnStart);
			map.put("create_time_end", returnEnd);
			//通过审核(收货入库)
			map.put("status", "4497153900050001");
			
			//只结算普通商户
			String settleTypeWhere = " AND si.uc_seller_type='4497478100050001' ";
			if(accountType.equals("4497477900030001")) {
				//月结
				map.put("account_clear_type", "4497478100030003");
				//原商户结算类型可能为空
				settleTypeWhere += "AND (si.account_clear_type=:account_clear_type OR si.account_clear_type is null OR si.account_clear_type ='')";
			} else if(accountType.equals("4497477900030002")) {
				//半月结
				map.put("account_clear_type", "4497478100030004");
				settleTypeWhere += "AND si.account_clear_type=:account_clear_type";
			}
			
			String sql = "SELECT " +
					 "small_seller_code " +
					 "FROM " +
					 "usercenter.uc_seller_info_extend si " +
					 "WHERE " +
						"si.small_seller_code IN (" +
							"SELECT DISTINCT " +
								"b.small_seller_code AS small_seller_code " +
							"FROM " +
								"(" +
									"SELECT " +
										"return_no " +
									"FROM " +
										"logcenter.lc_return_goods_status " + 
									"WHERE " +
										"status=:status " +
										"AND create_time>=:create_time_from " +
										"AND create_time<=:create_time_end" +
								") a, ordercenter.oc_return_goods b " +
							"WHERE " +
								"a.return_no = b.return_code " +
//								"AND b.small_seller_code LIKE 'SF031%'" +
						") " + settleTypeWhere;
			
			List<Map<String, Object>> list = DbUp.upTable("oc_return_goods").dataSqlList(sql, map);
			if (null != list && !list.isEmpty()) {
				for(Map<String, Object> seller : list) {
					String smallSellerCode = (String) seller.get("small_seller_code");
					if(StringUtils.isNotEmpty(smallSellerCode)) {
						sellerList.add((String) seller.get("small_seller_code"));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sellerList;
	}
	
	/**
	 * 财务总结算
	 * @param period 结算周期
	 */
	public void finanlBillAmount() {
		String saleStart = settlePeriod.get("sale_begin_date").toString();					// 结算开始时间
		String saleEnd = settlePeriod.get("sale_end_date").toString();		    			// 结算结束时间
		String returnStart = settlePeriod.get("return_begin_date").toString();				// 退货开始时间
		String returnEnd = settlePeriod.get("return_end_date").toString();					// 退货结束时间
		String settledate = settlePeriod.get("account_date").toString().replace("-", "");	// 结算时间
		if(accountType.equals("4497477900030002")) {
			// 半月结
			settledate = settledate + "_HALF";
		}
		
		MDataMap map = new MDataMap();
		map.put("start_time", saleStart);
		map.put("end_time", saleEnd);
		List<Map<String, Object>> list=DbUp.upTable("oc_bill_final_export").dataSqlList("select small_seller_code as small_seller_code,sum(settle_amount) as settle_amount,"
				+ "sum(period_retention_money) as period_money,sum(add_amount) as related_charges from oc_bill_final_export "
				+ "where start_time=:start_time and end_time=:end_time",map);
		if (null != list && !list.isEmpty()) {
			for (Map<String, Object> mapinfo : list) {
				double settle_amount = Double.valueOf(mapinfo.get("settle_amount")==null?"0.00":mapinfo.get("settle_amount").toString());
				double current_period_money = Double.valueOf(mapinfo.get("period_money")==null?"0.00":mapinfo.get("period_money").toString());
				double related_charges = Double.valueOf(mapinfo.get("related_charges")==null?"0.00":mapinfo.get("related_charges").toString());
				double actual_pay_amount =settle_amount-related_charges;
				String small_seller_code = StringUtils.isEmpty((String) mapinfo.get("small_seller_code")) ? "" : mapinfo.get("small_seller_code").toString();
				if(StringUtils.isNotEmpty(small_seller_code)) {
					DbUp.upTable("oc_bill_finance_amount").insert("settle_code", settledate, "settle_amount",
							String.valueOf(settle_amount),"current_period_money",String.valueOf(current_period_money),
							"related_charges", String.valueOf(related_charges),
							"settle_pay_moeny", String.valueOf(actual_pay_amount),"start_time",saleStart,"end_time",saleEnd,
							"settle_period",saleStart.substring(0, 10)+"至"+saleEnd.substring(0, 10),"settle_status","1",
							"tuistart",returnStart,"tuiend",returnEnd,"small_seller_code",small_seller_code);
				}
			}
		}
	}
	
	/**
	 * 汇总各商户的本月结算明细
	 */
	private void getSkuDetail() {
		String saleStartDate = settlePeriod.get("sale_begin_date").toString();  		// 结算开始时间
		String saleEndDate = settlePeriod.get("sale_end_date").toString();      		// 结算结束时间
		String returnStartDate = settlePeriod.get("return_begin_date").toString();  	// 退货开始时间
		String returnEndDate = settlePeriod.get("return_end_date").toString();      	// 退货结束时间		
		String settledate = settlePeriod.get("account_date").toString();      			// 结算日期
		
		  StringBuffer sb=new StringBuffer();
		  //增加了passage通路字段
		    sb.append("select passage,product_code,product_name,sku_code,sku_name,cost_price,sell_price,product_contract_sign,product_alter,");
	        sb.append("small_seller_code,small_seller_name,branch_name,branch_account,supplier_level,supplier_rate,");
	        sb.append("sum(success_num) as success_num, sum(success_amount) as success_amount,sum(return_num) as return_num,");
	        sb.append("sum(return_amount) as return_amount,max_retention_money,rate,add_amount,money_proportion_rate");
	        //sb.append(",deduct_retention_money,sum(period_retention_money) as period_retention_money,money_proportion_rate");
	        // sb.append("sale_money,postage,manage_money,others,add_amount,rate,input_tax_subtotal, total,other_pay_reason");
	        sb.append(" from oc_bill_final_export_tmp where start_time= '" + saleStartDate + "' and end_time='" + saleEndDate + "'");
	        sb.append(" group by passage,small_seller_code,product_code,sku_code,cost_price");
	        sb.append(" ORDER BY passage,small_seller_code,product_code,sku_code,cost_price ASC");
	        //供应商编号+商品编号+SKU编号+成本
	        List<Map<String, Object>> list = DbUp.upTable("oc_bill_final_export_tmp").dataSqlList(sb.toString(), null);
	        //求
	        String product_code="";
	        String sku_code="";
	        if(list.size()>0){
	        	for (Map<String, Object> mapinfo :list) {
	        		String passage=mapinfo.get("passage").toString();
	        		//获取被复制商品的sku编号和商品编号
	        		product_code=mapinfo.get("product_code").toString();
	    			Map<String, String> productMap = DbUp.upTable("pc_productinfo").oneWhere("product_code_copy","", "product_code=:product_code", "product_code",product_code);
	    			if(productMap!=null&&!productMap.isEmpty()&&!productMap.equals("")){
	    				product_code=productMap.get("product_code_copy").toString();
	    				if(product_code==null||product_code.equals("")){
	    					product_code=mapinfo.get("product_code").toString();
	    				}
	    			}
	    			String product_name=mapinfo.get("product_name").toString();
	    			sku_code=mapinfo.get("sku_code").toString();
	    			Map<String, String> skuMap = DbUp.upTable("pc_skuinfo").oneWhere("sku_code_old","", "sku_code=:sku_code", "sku_code",sku_code);
	    			if(skuMap!=null&&!skuMap.isEmpty()&&!skuMap.equals("")){
	    				sku_code=skuMap.get("sku_code_old")==null?"":skuMap.get("sku_code_old").toString();
	    				if(sku_code==null||sku_code.equals("")){
	    					sku_code=mapinfo.get("sku_code").toString();
	    				}
	    			}
	    			String sku_name=mapinfo.get("sku_name").toString();
	    			double cost_price=Double.valueOf(mapinfo.get("cost_price").toString());
	    			String sell_price=mapinfo.get("sell_price").toString();
	    			String product_contract_sign=mapinfo.get("product_contract_sign").toString();//商品合同签署
	    			String product_alter=mapinfo.get("product_alter").toString();//商品调编
	    			String small_seller_code=mapinfo.get("small_seller_code").toString();
	    			String small_seller_name=mapinfo.get("small_seller_name").toString();
	    			String branch_name=mapinfo.get("branch_name").toString();
	    			String branch_account=mapinfo.get("branch_account").toString();
	    			String supplier_level=mapinfo.get("supplier_level").toString();
	    			String supplier_rate=mapinfo.get("supplier_rate").toString();
	    			double success_num=Double.valueOf(mapinfo.get("success_num").toString());
	    			double success_amount=success_num*cost_price;
	    			double return_num=Double.valueOf(mapinfo.get("return_num").toString());
	    			double return_amount=return_num*cost_price;
	    			double settle_num=success_num-return_num;
	    			double settle_amount=settle_num*cost_price;
	    			double max_retention_money=Double.valueOf(mapinfo.get("max_retention_money").toString());
	    			double rate=Double.valueOf(mapinfo.get("rate").toString());
	    			double add_amount=Double.valueOf(mapinfo.get("add_amount")==null?"0.00":mapinfo.get("add_amount").toString());
	    			String input_tax_subtotal=String.valueOf(settle_amount/(1+rate)*rate);
	    			MDataMap map2 = new MDataMap();
	    			map2.put("product_code", product_code);
	    			map2.put("small_seller_code", small_seller_code);
	    			map2.put("sku_code", sku_code);
	    			map2.put("cost_price", String.valueOf(cost_price));
	    			//已扣质保金质保金
	    			//double old_deduct_retention_money=0;
	    			double old_period_retention_money=0;
	    			double deduct_retention_money=0;
	    			double period_retention_money_sum=0;
	    			
	    			//此sql有一个坑select deduct_retention_money...应变为select min(deduct_retention_money)....,不加min有时deduct_retention_money为0.0有时为某一期的数据
	    			//会导致该期质保金会被加,双份。
	    			//因为后面sum(period_retention_money)已累加,所以deduct_retention_money=old_deduct_retention_money+old_period_retention_money
	    			//变为deduct_retention_money=old_period_retention_money
	    			
	    			String sql="select deduct_retention_money,sum(period_retention_money) as period_retention_money from oc_bill_seller_retention_money where"
	    					+ " small_seller_code=:small_seller_code";
	    			Map<String,Object> mapbao=DbUp.upTable("oc_bill_seller_retention_money").dataSqlOne(sql, map2);
	    			if(mapbao!=null) {
	    				//old_deduct_retention_money=Double.valueOf(mapbao.get("deduct_retention_money")==null?"0.00":mapbao.get("deduct_retention_money").toString());
	    				old_period_retention_money=Double.valueOf(mapbao.get("period_retention_money")==null?"0.00":mapbao.get("period_retention_money").toString());
	    				//deduct_retention_money=old_deduct_retention_money+old_period_retention_money;
	    				deduct_retention_money=old_period_retention_money;
	    			}
	    			//结算质保金
	    			//当月某商户所有sku产生的质保金之和
	    			MDataMap map3 = new MDataMap();
	    			map3.put("start_time", saleStartDate);
	    			map3.put("small_seller_code", small_seller_code);
	    			String sql1="select sum(period_retention_money) as period_retention_money from oc_bill_final_export where "+ "small_seller_code=:small_seller_code and start_time=:start_time";
	    			Map<String,Object> mapbao1=DbUp.upTable("oc_bill_final_export").dataSqlOne(sql1, map3);
	    			if(mapbao1!=null){
	    				period_retention_money_sum=Double.valueOf(mapbao1.get("period_retention_money")==null?"0.00":mapbao1.get("period_retention_money").toString());
	    			}
	    			
	    			//if (剩余质保金-结算金额*质保金比例）>0？结算金额*质保金比例：剩余质保金
	    					//剩余质保金 =最大质保-已扣质保-∑本期质保
	    			double period_retention_money=0;
	    			double money_proportion_rate=Double.valueOf(mapinfo.get("money_proportion_rate")==null?"0.00":mapinfo.get("money_proportion_rate").toString());//质保金比例
	    			double remain_proportion_money=max_retention_money-deduct_retention_money-period_retention_money_sum;
	    			if(settle_amount<0){
	    				//本期没有结算
	    				period_retention_money=0;
	    			}else if(remain_proportion_money-settle_amount*money_proportion_rate>0){
	    				//还未扣完
	    				period_retention_money=settle_amount*money_proportion_rate;
	    			}else {
	    				//剩于要扣的质保金小于本期应扣质保金,则本期应扣质保金等于剩余质保金
	    				period_retention_money=remain_proportion_money;
	    			} 
	    			
//	    			else {
//	    				//质保金扣满后,以后每期remain_proportion_money计算后会为负
//	    				//走这个分支,从此期以后每期质保金都为0
//	    				period_retention_money = 0;
//	    			}
	    			double total=settle_amount-add_amount;
	    			DbUp.upTable("oc_bill_final_export").insert("product_settle_type","常规","passage",
	    					passage,"product_code",product_code,"product_name",product_name,"sku_code",sku_code,"sku_name",sku_name,
							"cost_price",String.valueOf(cost_price),"sell_price",String.valueOf(sell_price),
							"small_seller_code",small_seller_code,"small_seller_name",small_seller_name,
							"branch_name",branch_name,"branch_account",branch_account,"success_num",String.valueOf(success_num),
							"success_amount",String.valueOf(success_amount),"return_num",String.valueOf(return_num),
							"return_amount",String.valueOf(return_amount),"settle_num",String.valueOf(settle_num),
							"settle_amount",String.valueOf(settle_amount),"max_retention_money",String.valueOf(max_retention_money),
							"rate",String.valueOf(rate),"money_proportion_rate",String.valueOf(money_proportion_rate),
							"deduct_retention_money",String.valueOf(deduct_retention_money),
							"period_retention_money",String.valueOf(period_retention_money),"rate",String.valueOf(rate),
							"input_tax_subtotal",input_tax_subtotal,"total",String.valueOf(total),"start_time",saleStartDate,"end_time",saleEndDate);
			}
		}
	    if(list.size()>0) {
	       	retentionMoney(saleStartDate, saleEndDate);
	    }
	}
	
	public void retentionMoney(String saleStartDate, String saleEndDate) {
		MDataMap map = new MDataMap();
		map.put("start_time", saleStartDate);
		map.put("end_time", saleEndDate);
//		map.put("small_seller_code", small_seller_code);
		String sql="select small_seller_code,max_retention_money,deduct_retention_money,sum(period_retention_money) as period_retention_money,"
				+ " start_time from oc_bill_final_export where start_time=:start_time and end_time=:end_time group by small_seller_code ";
		List<Map<String, Object>> list=DbUp.upTable("oc_bill_final_export").dataSqlList(sql, map);
		for(Map<String, Object> mapinfo:list){
			String small_seller_code=mapinfo.get("small_seller_code").toString();
			double max_retention_money=Double.valueOf(mapinfo.get("max_retention_money").toString());
			double deduct_retention_money=Double.valueOf(mapinfo.get("deduct_retention_money").toString());
			double period_retention_money=Double.valueOf(mapinfo.get("period_retention_money").toString());
			DbUp.upTable("oc_bill_seller_retention_money").insert("small_seller_code",small_seller_code, "max_retention_money",
					String.valueOf(max_retention_money),"deduct_retention_money",String.valueOf(deduct_retention_money),
					"period_retention_money", String.valueOf(period_retention_money),"settle_time",saleStartDate);
		}
	}
	
	/**
	 * @deprecated
	 * 备用
	 * @deprecated
	 * @param smallSellerCode
	 * @param saleStart
	 * @param saleEnd
	 * @param returnStart
	 * @param returnEnd
	 * @param settledate
	 */
	private void getSkuDetail(String smallSellerCode, String saleStart, String saleEnd, String returnStart, String returnEnd, String settledate) {
		  StringBuffer sb=new StringBuffer();
		  //增加了passage通路字段
		    sb.append("select passage,product_code,product_name,sku_code,sku_name,cost_price,sell_price,product_contract_sign,product_alter,");
	        sb.append("small_seller_code,small_seller_name,branch_name,branch_account,supplier_level,supplier_rate,");
	        sb.append("sum(success_num) as success_num, sum(success_amount) as success_amount,sum(return_num) as return_num,");
	        sb.append("sum(return_amount) as return_amount,max_retention_money,rate,add_amount,money_proportion_rate");
	        //sb.append(",deduct_retention_money,sum(period_retention_money) as period_retention_money,money_proportion_rate");
	        // sb.append("sale_money,postage,manage_money,others,add_amount,rate,input_tax_subtotal, total,other_pay_reason");
	        sb.append(" from oc_bill_final_export_tmp where small_seller_code='" + smallSellerCode + "' and start_time= '" + saleStart + "' and end_time='" + saleEnd + "'");
	        sb.append(" group by passage,product_code,sku_code,cost_price");
	        sb.append(" ORDER BY passage,product_code,sku_code,cost_price ASC");
	        //供应商编号+商品编号+SKU编号+成本
	        List<Map<String, Object>> list = DbUp.upTable("oc_bill_final_export_tmp").dataSqlList(sb.toString(), null);
	        //求
	        String product_code="";
	        String sku_code="";
	        if(list.size()>0){
	        	for (Map<String, Object> mapinfo :list) {
	        		String passage=mapinfo.get("passage").toString();
	        		//获取被复制商品的sku编号和商品编号
	        		product_code=mapinfo.get("product_code").toString();
	    			Map<String, String> productMap = DbUp.upTable("pc_productinfo").oneWhere("product_code_copy","", "product_code=:product_code", "product_code",product_code);
	    			if(productMap!=null&&!productMap.isEmpty()&&!productMap.equals("")){
	    				product_code=productMap.get("product_code_copy").toString();
	    				if(product_code==null||product_code.equals("")){
	    					product_code=mapinfo.get("product_code").toString();
	    				}
	    			}
	    			String product_name=mapinfo.get("product_name").toString();
	    			sku_code=mapinfo.get("sku_code").toString();
	    			Map<String, String> skuMap = DbUp.upTable("pc_skuinfo").oneWhere("sku_code_old","", "sku_code=:sku_code", "sku_code",sku_code);
	    			if(skuMap!=null&&!skuMap.isEmpty()&&!skuMap.equals("")){
	    				sku_code=skuMap.get("sku_code_old")==null?"":skuMap.get("sku_code_old").toString();
	    				if(sku_code==null||sku_code.equals("")){
	    					sku_code=mapinfo.get("sku_code").toString();
	    				}
	    			}
	    			String sku_name=mapinfo.get("sku_name").toString();
	    			double cost_price=Double.valueOf(mapinfo.get("cost_price").toString());
	    			String sell_price=mapinfo.get("sell_price").toString();
	    			String product_contract_sign=mapinfo.get("product_contract_sign").toString();//商品合同签署
	    			String product_alter=mapinfo.get("product_alter").toString();//商品调编
	    			String small_seller_code=mapinfo.get("small_seller_code").toString();
	    			String small_seller_name=mapinfo.get("small_seller_name").toString();
	    			String branch_name=mapinfo.get("branch_name").toString();
	    			String branch_account=mapinfo.get("branch_account").toString();
	    			String supplier_level=mapinfo.get("supplier_level").toString();
	    			String supplier_rate=mapinfo.get("supplier_rate").toString();
	    			double success_num=Double.valueOf(mapinfo.get("success_num").toString());
	    			double success_amount=success_num*cost_price;
	    			double return_num=Double.valueOf(mapinfo.get("return_num").toString());
	    			double return_amount=return_num*cost_price;
	    			double settle_num=success_num-return_num;
	    			double settle_amount=settle_num*cost_price;
	    			double max_retention_money=Double.valueOf(mapinfo.get("max_retention_money").toString());
	    			double rate=Double.valueOf(mapinfo.get("rate").toString());
	    			double add_amount=Double.valueOf(mapinfo.get("add_amount")==null?"0.00":mapinfo.get("add_amount").toString());
	    			String input_tax_subtotal=String.valueOf(settle_amount/(1+rate)*rate);
	    			MDataMap map2 = new MDataMap();
	    			map2.put("product_code", product_code);
	    			map2.put("small_seller_code", small_seller_code);
	    			map2.put("sku_code", sku_code);
	    			map2.put("cost_price", String.valueOf(cost_price));
	    			//已扣质保金质保金
	    			double old_deduct_retention_money=0;
	    			double old_period_retention_money=0;
	    			double deduct_retention_money=0;
	    			double period_retention_money_sum=0;
	    			String sql="select deduct_retention_money,sum(period_retention_money) as period_retention_money from oc_bill_seller_retention_money where"
	    					+ " small_seller_code=:small_seller_code";
	    			Map<String,Object> mapbao=DbUp.upTable("oc_bill_seller_retention_money").dataSqlOne(sql, map2);
	    			if(mapbao!=null){
	    				old_deduct_retention_money=Double.valueOf(mapbao.get("deduct_retention_money")==null?"0.00":mapbao.get("deduct_retention_money").toString());
	    				old_period_retention_money=Double.valueOf(mapbao.get("period_retention_money")==null?"0.00":mapbao.get("period_retention_money").toString());
	    				deduct_retention_money=old_deduct_retention_money+old_period_retention_money;
	    			}
	    			//结算质保金
	    			MDataMap map3 = new MDataMap();
	    			map3.put("start_time", saleStart);
	    			map3.put("small_seller_code", small_seller_code);
	    			String sql1="select sum(period_retention_money) as period_retention_money from oc_bill_final_export where "+ "small_seller_code=:small_seller_code and start_time=:start_time";
	    			Map<String,Object> mapbao1=DbUp.upTable("oc_bill_final_export").dataSqlOne(sql1, map3);
	    			if(mapbao1!=null){
	    				period_retention_money_sum=Double.valueOf(mapbao1.get("period_retention_money")==null?"0.00":mapbao1.get("period_retention_money").toString());
	    			}
	    			
	    			//if (剩余质保金-结算金额*质保金比例）>0？结算金额*质保金比例：剩余质保金
	    					//剩余质保金 =最大质保-已扣质保-∑本期质保
	    			double period_retention_money=0;
	    			double money_proportion_rate=Double.valueOf(mapinfo.get("money_proportion_rate")==null?"0.00":mapinfo.get("money_proportion_rate").toString());//质保金比例
	    			double remain_proportion_money=max_retention_money-deduct_retention_money-period_retention_money_sum;
	    			if(settle_amount<0){
	    				period_retention_money=0;
	    			}else if(remain_proportion_money-settle_amount*money_proportion_rate>0){
	    				period_retention_money=settle_amount*money_proportion_rate;
	    			}else{
	    				period_retention_money=remain_proportion_money;
	    			}
	    			double total=settle_amount-add_amount;
	    			DbUp.upTable("oc_bill_final_export").insert("product_settle_type","常规","passage",
	    					passage,"product_code",product_code,"product_name",product_name,"sku_code",sku_code,"sku_name",sku_name,
							"cost_price",String.valueOf(cost_price),"sell_price",String.valueOf(sell_price),
							"small_seller_code",small_seller_code,"small_seller_name",small_seller_name,
							"branch_name",branch_name,"branch_account",branch_account,"success_num",String.valueOf(success_num),
							"success_amount",String.valueOf(success_amount),"return_num",String.valueOf(return_num),
							"return_amount",String.valueOf(return_amount),"settle_num",String.valueOf(settle_num),
							"settle_amount",String.valueOf(settle_amount),"max_retention_money",String.valueOf(max_retention_money),
							"rate",String.valueOf(rate),"money_proportion_rate",String.valueOf(money_proportion_rate),
							"deduct_retention_money",String.valueOf(deduct_retention_money),
							"period_retention_money",String.valueOf(period_retention_money),"rate",String.valueOf(rate),
							"input_tax_subtotal",input_tax_subtotal,"total",String.valueOf(total),"start_time",saleStart,"end_time",saleEnd);
			}
		}
	    if(list.size()>0) {
	       	retentionMoney(smallSellerCode, saleStart, saleEnd);
	    }
	}
	
	/**
	 * @deprecated
	 * 备用
	 * @param small_seller_code
	 * @param saleStart
	 * @param saleEnd
	 */
	public void retentionMoney(String small_seller_code, String saleStart, String saleEnd) {
		MDataMap map = new MDataMap();
		map.put("start_time", saleStart);
		map.put("end_time", saleEnd);
		map.put("small_seller_code", small_seller_code);
		String sql="select small_seller_code,max_retention_money,deduct_retention_money,sum(period_retention_money) as period_retention_money,"
				+ " start_time from oc_bill_final_export where start_time=:start_time and end_time=:end_time and small_seller_code=:small_seller_code group by small_seller_code ";
		List<Map<String, Object>> list=DbUp.upTable("oc_bill_final_export").dataSqlList(sql, map);
		for(Map<String, Object> mapinfo:list){
//			String small_seller_code=mapinfo.get("small_seller_code").toString();
			double max_retention_money=Double.valueOf(mapinfo.get("max_retention_money").toString());
			double deduct_retention_money=Double.valueOf(mapinfo.get("deduct_retention_money").toString());
			double period_retention_money=Double.valueOf(mapinfo.get("period_retention_money").toString());
			DbUp.upTable("oc_bill_seller_retention_money").insert("small_seller_code",small_seller_code, "max_retention_money",
					String.valueOf(max_retention_money),"deduct_retention_money",String.valueOf(deduct_retention_money),
					"period_retention_money", String.valueOf(period_retention_money),"settle_time",saleStart);
		}
	}
}
