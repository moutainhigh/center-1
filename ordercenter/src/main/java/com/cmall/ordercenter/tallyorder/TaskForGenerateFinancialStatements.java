package com.cmall.ordercenter.tallyorder;


import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;

import com.cmall.ordercenter.tallyorder.settleperiod.OcAccountPeriodService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 定时任务-商户结算单
 * @author zmm
 *
 */
public class TaskForGenerateFinancialStatements extends RootJob{

	public void doExecute(JobExecutionContext context) {
		// 获取结算时间
		OcAccountPeriodService ops = new OcAccountPeriodService();
		MDataMap map = ops.getSettlePeriod();
		if (map != null) {
			String start = map.get("sale_begin_date").toString();// 结算开始时间
			String end = map.get("sale_end_date").toString();// 结算结束时间
			String tuiStart = map.get("return_begin_date").toString();// 退货开始时间
			String tuiEnd = map.get("return_end_date").toString();// 退货结束时间
			String settledate = map.get("account_date").toString();// 结算时间
			
			try {
				getPaySuccessCodes(start, end, tuiStart, tuiEnd);
				getReturnCodes(tuiStart, tuiEnd, start, end);
				getSkuDetail(start, end, tuiStart, tuiEnd,settledate);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 自然月有效订单号
	 * @param start_time
	 * @param end_time
	 * @param tuiStart
	 * @param tuiEnd
	 * @param response
	 * @return
	 * @throws Exception
	 */
	private MDataMap getPaySuccessCodes(String start_time, String end_time,String tui_start, String tui_end) throws Exception {
		MDataMap map = new MDataMap();
		try {
			map.put("create_time_from", start_time);
			map.put("create_time_end", end_time);
			map.put("status", "4497153900010005");
			map.put("pay_type", "449716200001");
			
			//sql中增加了商户条件in ('SI2003','SI3003')
//			List<Map<String, Object>> list = DbUp.upTable("lc_orderstatus")//SF031
//					.dataSqlList("select a.code,a.create_time from (SELECT code, create_time FROM logcenter.lc_orderstatus WHERE now_status = '4497153900010005' GROUP BY code) a,ordercenter.oc_orderinfo b "
//									+ "where a.code=b.order_code and b.seller_code in ('SI2003','SI3003') and b.small_seller_code like 'SF031%' and b.pay_type=:pay_type "
//									+ "and a.create_time>=:create_time_from and a.create_time<=:create_time_end",map);
			
			String sql ="select a.code,a.create_time from (SELECT code, create_time FROM logcenter.lc_orderstatus WHERE now_status = '4497153900010005' GROUP BY code) a,ordercenter.oc_orderinfo b, usercenter.uc_seller_info_extend c "
					+ "where a.code=b.order_code and b.small_seller_code= c.small_seller_code and b.seller_code in ('SI2003','SI3003') and b.small_seller_code like 'SF031%' and b.pay_type=:pay_type "
					+ "and a.create_time>=:create_time_from and a.create_time<=:create_time_end";
			List<Map<String, Object>> list = DbUp.upTable("lc_orderstatus").dataSqlList(sql,map);
			
			map.clear();
			if (!list.isEmpty()) {
				Iterator<Map<String, Object>> iterator = list.iterator();
				while (iterator.hasNext()) {
					Map<String, Object> m = iterator.next();
					map.put(m.get("code").toString(), m.get("create_time").toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		getSkuInfoDetail(map, start_time,end_time,tui_start,tui_end);
		return map;
	}
	
	
	/**
	 * 生成财务结算单
	 * @param map
	 * @param start_time 开始结算时间
	 * @param end_time
	 * @param tuiStart  开始退货时间
	 * @param tuiEnd
	 * @throws Exception
	 */
	private void getSkuInfoDetail(MDataMap map,String start_time, String end_time,String tui_start,String tui_end) throws Exception {
		// 根据有效的订单编号查询sku 信息
 		String ordersql = JoinSql.getJoinOrderCode(map);
 		//System.out.println("ordersql------"+ordersql);
		String skuSql = "select order_code,product_code,sku_code,sku_name,sku_price,sku_num,cost_price from oc_orderdetail where order_code " + ordersql;
 		List<Map<String, Object>> list = DbUp.upTable("oc_orderdetail").dataSqlList(skuSql, null);
		//System.out.println("list------" + list.size());
		for (Map<String, Object> map2 : list) {
			String order_code = map2.get("order_code").toString();  
			String product_code = map2.get("product_code").toString();
			//判断此商品结算类型是否为常规商品 4497471600110001常规
			Map<String, String> settle_type=DbUp.upTable("pc_productinfo_ext").oneWhere("settlement_type,product_code","","product_code=:product_code","product_code",product_code);
			if(settle_type!=null){
			String settlement_type=settle_type.get("settlement_type").toString();
			if(settlement_type.equals("4497471600110001")){
			String sku_code = map2.get("sku_code").toString();
			String sku_name=map2.get("sku_name").toString();
			//double sku_price = Double.valueOf(map2.get("sku_price").toString());
			double success_sku_num = Double.valueOf(map2.get("sku_num").toString());
			double cost_price = Double.valueOf(map2.get("cost_price").toString());
			//sku售价
			Map<String, String> skumap=DbUp.upTable("pc_skuinfo").oneWhere("sell_price,sku_code","","sku_code=:sku_code","sku_code",sku_code);
			double sell_price = 0;
			if(skumap!=null){
				sell_price=Double.valueOf(skumap.get("sell_price").toString());
			}
			//商品信息
			Map<String, String> productMap = DbUp.upTable("pc_productinfo")
					.oneWhere("product_code,product_name,tax_rate,small_seller_code,seller_code","", "product_code=:product_code", "product_code",product_code);
			String product_name="";
			double tax_rate=0;
			String small_seller_code="";
			//增加了商户的编码(seller_code)作为通路 例如SI2003,SI3003
			String seller_code="";
			if(productMap!=null){
				product_name = productMap.get("product_name").toString();
				tax_rate = Double.valueOf(productMap.get("tax_rate").toString()==null||productMap.get("tax_rate").toString()==""?"0.00":productMap.get("tax_rate").toString());
				small_seller_code = productMap.get("small_seller_code").toString();
				seller_code = productMap.get("seller_code").toString();
			}
			//获取商户的信息
			Map<String, String> sellerInfoMap=DbUp.upTable("uc_seller_info_extend").oneWhere("seller_company_name,branch_name,bank_account,quality_retention_money,money_proportion","","small_seller_code=:small_seller_code","small_seller_code",small_seller_code);
			String seller_company_name="";
			String branch_name="";
			String bank_account="";
			double max_quality_retention_money=0;
			double money_proportion_rate=0;
			if(sellerInfoMap!=null){
				 seller_company_name = sellerInfoMap.get("seller_company_name")==null?"":sellerInfoMap.get("seller_company_name").toString();
				 branch_name = sellerInfoMap.get("branch_name").toString();//开户行
				 bank_account = sellerInfoMap.get("bank_account").toString();//帐号
				 max_quality_retention_money = Double.valueOf( (sellerInfoMap.get("quality_retention_money")==null||sellerInfoMap.get("quality_retention_money")==""?"0.00":sellerInfoMap.get("quality_retention_money").toString()));//质保金
				 money_proportion_rate = Double.valueOf( (sellerInfoMap.get("money_proportion")==null||sellerInfoMap.get("money_proportion")==""?"0.00":sellerInfoMap.get("money_proportion").toString()));//质保金比例
			}
			//把数据入库订单结算表SF03100294此商户不包含在结算内
			if(!small_seller_code.equals("SF03100294")&&!small_seller_code.equals("SF03100327")&&!small_seller_code.equals("SF03100329")){
			DbUp.upTable("oc_bill_final_export_tmp").insert("order_code",order_code,"product_settle_type","常规","passage",
					seller_code,"product_code",product_code,"product_name",product_name,"sku_code",sku_code,"sku_name",sku_name,
					"cost_price",String.valueOf(cost_price),"sell_price",String.valueOf(sell_price),
					"small_seller_code",small_seller_code,"small_seller_name",seller_company_name,
					"branch_name",branch_name,"branch_account",bank_account,"success_num",String.valueOf(success_sku_num),
					"success_amount",String.valueOf(success_sku_num*cost_price),"return_num",String.valueOf("0"),
					"return_amount",String.valueOf("0.00"),"settle_num",String.valueOf(success_sku_num),
					"settle_amount",String.valueOf(success_sku_num*cost_price),"max_retention_money",String.valueOf(max_quality_retention_money),
					"rate",String.valueOf(tax_rate),"money_proportion_rate",String.valueOf(money_proportion_rate),"start_time",start_time,"end_time",end_time,"tui_start",tui_start,"tui_end",tui_end);
		          //System.out.println("-------------insert oc_bill_final_export success!!----------------------");
				}
			}
		} 
	 }
}

	
	/**
	 * 自然月有效订单号
	 * @param start_time
	 * @param end_time
	 * @param tuiStart
	 * @param tuiEnd
	 * @param response
	 * @return
	 * @throws Exception
	 */
	private MDataMap getReturnCodes(String tuiStart,String tuiEnd,String start_time, String end_time) throws Exception {
		MDataMap map = new MDataMap();
		try {
			map.put("create_time_from", tuiStart);
			map.put("create_time_end", tuiEnd);
			map.put("status", "4497153900050001");

			String sql ="select a.create_time,b.order_code "
					+ "from logcenter.lc_return_goods_status a,ordercenter.oc_return_goods b, usercenter.uc_seller_info_extend c "
					+ "where a.return_no = b.return_code and b.small_seller_code = c.small_seller_code and "
					+ "a.status=:status and a.create_time>=:create_time_from and a.create_time<=:create_time_end ";
			List<Map<String, Object>> list = DbUp.upTable("oc_return_goods").dataSqlList(sql,map);
			
//			List<Map<String, Object>> list = DbUp.upTable("oc_return_goods")
//					.dataSqlList("select a.create_time,b.order_code from logcenter.lc_return_goods_status a,ordercenter.oc_return_goods b where a.return_no = b.return_code and a.status=:status and a.create_time>=:create_time_from and a.create_time<=:create_time_end and b.small_seller_code like 'SF031%'",map);
			map.clear();
			if (list.size()>0) {
				Iterator<Map<String, Object>> iterator = list.iterator();
				while (iterator.hasNext()) {
					Map<String, Object> m = iterator.next();
					map.put(m.get("order_code").toString(), m.get("create_time").toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(map!=null){
			getReturntSkuInfoDetail(map,tuiStart, tuiEnd,start_time,end_time);
		}
		return map;
	}
	
	private void getReturntSkuInfoDetail(MDataMap map,String tuiStart,String tuiEnd,String start_time,String end_time){
		String product_code="";
		String sku_name="";
		double cost_price=0;
		// 根据有效的订单编号查询sku 信息
		 		String ordersql = JoinSql.getJoinOrderCode(map);
		 		//原始退货sql
				//String skuSql = "select order_code,product_code,sku_code,sku_name,sku_price,sku_num,cost_price from oc_orderdetail where order_code " + ordersql;
		 		//部分退换货sql
		 		String skuSql="select a.return_code,a.order_code as order_code,b.sku_code as sku_code, sum(b.count) as count ,b.return_code from ordercenter.oc_return_goods a,"
		 				+ "ordercenter.oc_return_goods_detail b  where a.order_code  "+ordersql+" "
		 				+ "and a.return_code=b.return_code and a.status='4497153900050001' group by a.order_code,b.return_code,b.sku_code;";
		 		List<Map<String, Object>> list = DbUp.upTable("oc_return_goods_detail").dataSqlList(skuSql, null);
				for (Map<String, Object> returnMap : list) {
					String order_code = returnMap.get("order_code").toString(); 
					String sku_code = returnMap.get("sku_code").toString();
				    String skudetail = "select product_code,sku_name,sku_price,cost_price from oc_orderdetail where order_code = '"+order_code+"' and sku_code= '"+sku_code+"'";
				    List<Map<String, Object>> skulist = DbUp.upTable("oc_orderdetail").dataSqlList(skudetail, null);
				    for (Map<String, Object> skuMap : skulist) {
				    	product_code=skuMap.get("product_code").toString();
				    	sku_name=skuMap.get("sku_name").toString();
				    	cost_price = Double.valueOf(skuMap.get("cost_price").toString());
					}
					//判断此商品结算类型是否为常规商品 4497471600110001常规
					Map<String, String> settle_type=DbUp.upTable("pc_productinfo_ext").oneWhere("settlement_type,product_code","","product_code=:product_code","product_code",product_code);
					if(settle_type!=null){
					String settlement_type=settle_type.get("settlement_type").toString();
					if(settlement_type.equals("4497471600110001")){
					double return_sku_num = Double.valueOf(returnMap.get("count").toString());//每个订单所包含的退货单上sku_code的退货数量
					//sku售价
					Map<String, String> skumap=DbUp.upTable("pc_skuinfo").oneWhere("sell_price,sku_code","","sku_code=:sku_code","sku_code",sku_code);
					double sell_price = 0;
					if(skumap!=null){
						sell_price=Double.valueOf(skumap.get("sell_price").toString());
					}
					//商品信息
					Map<String, String> productMap = DbUp.upTable("pc_productinfo")
							.oneWhere("product_code,product_name,tax_rate,small_seller_code,seller_code","", "product_code=:product_code", "product_code",product_code);
					String product_name="";
					double tax_rate=0;
					String small_seller_code="";
					//增加了商户的编码(seller_code)作为通路 例如SI2003,SI3003
					String seller_code="";
					if(productMap!=null){
						product_name = productMap.get("product_name").toString();
						tax_rate = Double.valueOf(productMap.get("tax_rate").toString());
						small_seller_code = productMap.get("small_seller_code").toString();
						seller_code = productMap.get("seller_code").toString();
					}
					//获取商户的信息
					Map<String, String> sellerInfoMap=DbUp.upTable("uc_seller_info_extend").oneWhere("seller_company_name,branch_name,bank_account,quality_retention_money,money_proportion","","small_seller_code=:small_seller_code","small_seller_code",small_seller_code);
					String seller_company_name="";
					String branch_name="";
					String bank_account="";
					double max_quality_retention_money=0;
					double money_proportion_rate=0;
					if(sellerInfoMap!=null){
						 seller_company_name = sellerInfoMap.get("seller_company_name")==null?"":sellerInfoMap.get("seller_company_name").toString();
						 branch_name = sellerInfoMap.get("branch_name").toString();//开户行
						 bank_account = sellerInfoMap.get("bank_account").toString();//帐号
						 max_quality_retention_money = Double.valueOf( (sellerInfoMap.get("quality_retention_money")==null||sellerInfoMap.get("quality_retention_money")==""?"0.00":sellerInfoMap.get("quality_retention_money").toString()));//质保金
						 money_proportion_rate = Double.valueOf( (sellerInfoMap.get("money_proportion")==null||sellerInfoMap.get("money_proportion")==""?"0.00":sellerInfoMap.get("money_proportion").toString()));//质保金比例
					}
					//把数据入库订单结算表
					double return_amount=return_sku_num*cost_price;
					double settle_amount=return_sku_num*cost_price;
					//此商户不包含在结算内
					if(!small_seller_code.equals("SF03100294")&&!small_seller_code.equals("SF03100327")&&!small_seller_code.equals("SF03100329")){
					DbUp.upTable("oc_bill_final_export_tmp").insert("order_code",order_code,"product_settle_type","常规","passage",
							seller_code,"product_code",product_code,"product_name",product_name,"sku_code",sku_code,"sku_name",sku_name,
							"cost_price",String.valueOf(cost_price),"sell_price",String.valueOf(sell_price),
							"small_seller_code",small_seller_code,"small_seller_name",seller_company_name,
							"branch_name",branch_name,"branch_account",bank_account,"success_num",String.valueOf("0.00"),
							"success_amount",String.valueOf("0.00"),"return_num",String.valueOf(return_sku_num),
							"return_amount",String.valueOf(return_amount),"settle_num",String.valueOf(return_sku_num),
							"settle_amount",String.valueOf(settle_amount),"max_retention_money",String.valueOf(max_quality_retention_money),
							"rate",String.valueOf(tax_rate),"money_proportion_rate",String.valueOf(money_proportion_rate),"tui_start",tuiStart,"tui_end",tuiEnd,"start_time",start_time,"end_time",end_time);
					}
				}
			}
		}
	}
	
	private void getSkuDetail(String start_time,String end_time,String tuiStart, String tuiEnd,String settledate){
		  StringBuffer sb=new StringBuffer();
		  //增加了passage通路字段
		    sb.append("select passage,product_code,product_name,sku_code,sku_name,cost_price,sell_price,product_contract_sign,product_alter,");
	        sb.append("small_seller_code,small_seller_name,branch_name,branch_account,supplier_level,supplier_rate,");
	        sb.append("sum(success_num) as success_num, sum(success_amount) as success_amount,sum(return_num) as return_num,");
	        sb.append("sum(return_amount) as return_amount,max_retention_money,rate,add_amount,money_proportion_rate");
	        //sb.append(",deduct_retention_money,sum(period_retention_money) as period_retention_money,money_proportion_rate");
	        // sb.append("sale_money,postage,manage_money,others,add_amount,rate,input_tax_subtotal, total,other_pay_reason");
	        sb.append(" from oc_bill_final_export_tmp where start_time= '"+start_time+"' and end_time='"+end_time+"'");
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
	    			map3.put("start_time", start_time);
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
							"input_tax_subtotal",input_tax_subtotal,"total",String.valueOf(total),"start_time",start_time,"end_time",end_time);
			}
		}
	        if(list.size()>0){
	        	finanlBillAmount(start_time,end_time,tuiStart, tuiEnd,settledate);
	        	retentionMoney(start_time,end_time);
	        }
	}
	
	/**
	 * 财务总结算
	 * @param start_time
	 * @param end_time
	 * @param tui_end
	 */
	public void finanlBillAmount(String start_time, String end_time,String tui_start , String tui_end,String settledate){
		MDataMap map = new MDataMap();
		map.put("start_time", start_time);
		map.put("end_time", end_time);
		List<Map<String, Object>> list=DbUp.upTable("oc_bill_final_export").dataSqlList("select small_seller_code,sum(settle_amount) as settle_amount,"
				+ "sum(period_retention_money) as period_money,sum(add_amount) as related_charges from oc_bill_final_export "
				+ "where start_time=:start_time and end_time=:end_time ",map);
		if (list.size() > 0) {
			for (Map<String, Object> mapinfo : list) {
				double settle_amount = Double.valueOf(mapinfo.get("settle_amount")==null?"0.00":mapinfo.get("settle_amount").toString());
				double current_period_money = Double.valueOf(mapinfo.get("period_money")==null?"0.00":mapinfo.get("period_money").toString());
				double related_charges = Double.valueOf(mapinfo.get("related_charges")==null?"0.00":mapinfo.get("related_charges").toString());
				double actual_pay_amount =settle_amount-related_charges;
				String small_seller_code = mapinfo.get("small_seller_code").toString();
				DbUp.upTable("oc_bill_finance_amount").insert("settle_code",settledate.replace("-", ""), "settle_amount",
						String.valueOf(settle_amount),"current_period_money",String.valueOf(current_period_money),
						"related_charges", String.valueOf(related_charges),
						"settle_pay_moeny", String.valueOf(actual_pay_amount),"start_time",start_time,"end_time",end_time,
						"settle_period",start_time.substring(0, 10)+"至"+end_time.substring(0, 10),"settle_status","1",
						"tuistart",tui_start,"tuiend",tui_end,"small_seller_code",small_seller_code);
			}
		}
	}
	
	public void retentionMoney(String start_time, String end_time) {
		MDataMap map = new MDataMap();
		map.put("start_time", start_time);
		map.put("end_time", end_time);
		String sql="select small_seller_code,max_retention_money,deduct_retention_money,sum(period_retention_money) as period_retention_money,"
				+ " start_time from oc_bill_final_export where start_time=:start_time and end_time=:end_time group by small_seller_code ";
		//System.out.println(sql);
		List<Map<String, Object>> list=DbUp.upTable("oc_bill_final_export").dataSqlList(sql, map);
		for(Map<String, Object> mapinfo:list){
			String small_seller_code=mapinfo.get("small_seller_code").toString();
			double max_retention_money=Double.valueOf(mapinfo.get("max_retention_money").toString());
			double deduct_retention_money=Double.valueOf(mapinfo.get("deduct_retention_money").toString());
			double period_retention_money=Double.valueOf(mapinfo.get("period_retention_money").toString());
			DbUp.upTable("oc_bill_seller_retention_money").insert("small_seller_code",small_seller_code, "max_retention_money",
					String.valueOf(max_retention_money),"deduct_retention_money",String.valueOf(deduct_retention_money),
					"period_retention_money", String.valueOf(period_retention_money),"settle_time",start_time);
		}
	}

	
	//only  for  test
	public static void main(String[] args) throws Exception {
//		TaskForGenerateFinancialStatements ff=new TaskForGenerateFinancialStatements();
//		///ff.getPaySuccessCodes("2015-09-02 00:00:01", "2015-09-30 23:59:59","2015-09-21 00:00:00", "2015-10-21 00:00:00");
//		//System.out.println(4);
//	    ff.getReturnCodes("2015-07-29 11:40:45", "2015-10-10 18:07:29","2015-09-02 00:00:01", "2015-09-30 23:59:59");
//	   // System.out.println(5);
//		//ff.getSkuDetail("2015-09-02 00:00:01", "2015-09-30 23:59:59","2015-09-21 00:00:00", "2015-10-21 00:00:00","2015-11-11");
//		//ff.getPaySuccessCodes("2015-09-30 00:00:01", "2015-09-30 23:59:59","2015-09-21 00:00:00", "2015-10-21 00:00:00");
//		System.out.println("------------------------------------");
//	    //ff.getReturnCodes("2015-09-21 00:00:00", "2015-10-21 00:00:00","2015-09-02 00:00:01", "2015-09-30 23:59:59");
//	    System.out.println(5);
//	    System.out.println("-------------------------0oooo");
//	//	ff.getSkuDetail("2015-09-30 00:00:01", "2015-09-30 23:59:59","2015-09-21 00:00:00", "2015-10-21 00:00:00","2015-11-06");
//		//ff.doExecute(null);
//		System.out.println("---------ok-----------");
		
		// new TaskForGenerateFinancialStatements().getReturnCodes(, );
		// new TaskForGenerateFinancialStatements().getPaySuccessCodes("2016-03-01 00:00:00", "2016-03-31 23:59:59", "2016-03-08 00:00:00", "2016-04-07 23:59:59");
		// new TaskForGenerateFinancialStatements().doExecute(null);
		
//		new TaskForGenerateFinancialStatements().getPaySuccessCodes("2016-04-01 00:00:00", "2016-04-30 23:59:59", "2016-04-08 00:00:00", "2016-05-07 23:59:59");
//		new TaskForGenerateFinancialStatements().getReturnCodes("2016-04-08 00:00:00", "2016-05-07 23:59:59", "2016-04-01 00:00:00", "2016-04-30 23:59:59");
//		new TaskForGenerateFinancialStatements().getSkuDetail("2016-04-01 00:00:00", "2016-04-31 23:59:59", "2016-04-08 00:00:00", "2016-05-07 23:59:59", "20160508");
		TaskForGenerateFinancialStatements task = new TaskForGenerateFinancialStatements();
		task.doExecute(null);
		System.out.println("......................success");
	}

}
