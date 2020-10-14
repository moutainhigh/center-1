package com.cmall.ordercenter.tallyorder.settle;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;

import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.tallyorder.JoinSql;
import com.cmall.ordercenter.util.DateFormatUtil;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.MailSupport;
import com.srnpr.zapcom.topdo.TopUp;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.rootweb.RootJob;
import com.srnpr.zapweb.webclass.WarnCount;

/**
 * 生成自定义结算周期商户的结算单
 * @author lgx
 *
 */
public class TaskCustomizeFinancialStatement extends RootJob {
	
	// 自定义日期字符串(01~15)
	private String bill_day;
	// 账单节点
	private String bill_point;
	// 商户类型
	private String uc_seller_type;
	// 商户编号
	private String smallSellerCode;
	// 结算类型
	private String settleType;
	// 结算单编号固定字母(平台PT/常规CG)
	private String billChar;
	
	private String saleStart;
	private String saleEnd;
	private String returnStart;
	private String returnEnd;
	
	
	@Override
	public void doExecute(JobExecutionContext context) {
		// 结算类型只有常规结算和平台入驻(只有这两种商户支持自定义结算周期)
		List<String> settleTypeList = new ArrayList<>();
		settleTypeList.add("4497477900040001");
		settleTypeList.add("4497477900040004");
		
		for (String type : settleTypeList) {
			settleType = type;
			// 获取所有自定义结算周期的商户
			String sql = "SELECT s.small_seller_code, sie.uc_seller_type, sie.bill_point, sie.bill_day FROM uc_sellerinfo s " + 
					"LEFT JOIN uc_seller_info_extend sie ON s.small_seller_code = sie.small_seller_code " + 
					" WHERE sie.account_clear_type = '4497478100030006' AND sie.seller_company_name NOT LIKE '%测试%' ";
			
			if(type.equals("4497477900040001")) {
				sql += " AND sie.uc_seller_type in ('4497478100050001','4497478100050005') ";
				billChar = "CG";
			}else if(type.equals("4497477900040004")) {
				sql += " AND sie.uc_seller_type = '4497478100050004' ";				
				billChar = "PT";
			}
			List<Map<String, Object>> sellerList = DbUp.upTable("uc_sellerinfo").dataSqlList(sql, new MDataMap());
			// 循环处理每个商户的订单和退货单
			if(sellerList != null && sellerList.size() > 0) {			
				for (Map<String, Object> map : sellerList) {
					smallSellerCode = MapUtils.getString(map, "small_seller_code");
					bill_point = MapUtils.getString(map, "bill_point");
					bill_day = MapUtils.getString(map, "bill_day");
					uc_seller_type =  MapUtils.getString(map, "uc_seller_type");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");                
					Calendar c = Calendar.getInstance();           
					c.add(Calendar.DATE, - Integer.parseInt(bill_day));           
					Date time = c.getTime();         
					String preDay = sdf.format(time);
					saleStart = preDay + " 00:00:00"; // 结算开始时间
					saleEnd = preDay + " 23:59:59"; // 结算结束时间
					returnStart = preDay + " 00:00:00"; // 退货开始时间
					returnEnd = preDay + " 23:59:59"; // 退货结束时间
					
					if (StringUtils.isNotEmpty(smallSellerCode)) {
						try {
							// 抓取商户自定义账期成交订单的sku信息
							grabSkuDealed();
							// 查询商户自定义账期有效退货订单
							grabSkuReturned();
						} catch (Exception e) {
							e.printStackTrace();
							// 结算出现问题,发送邮件通知
							sendMail();
							sendWx();
						}
					}
				}
			}
			List<Map<String, Object>> dayList = DbUp.upTable("oc_bill_period_source").dataSqlList("SELECT period_code FROM oc_bill_period_source WHERE period_code <= '15' ORDER BY period_code ASC", new MDataMap());
			for (Map<String, Object> map : dayList) {
				bill_day = MapUtils.getString(map, "period_code");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");                
				Calendar c = Calendar.getInstance();           
				c.add(Calendar.DATE, - Integer.parseInt(bill_day));           
				Date time = c.getTime();         
				String preDay = sdf.format(time);
				saleStart = preDay + " 00:00:00"; // 结算开始时间
				saleEnd = preDay + " 23:59:59"; // 结算结束时间
				returnStart = preDay + " 00:00:00"; // 退货开始时间
				returnEnd = preDay + " 23:59:59"; // 退货结束时间
				
				// 全部商户处理完之后,统一按固定账期(01~15)结算
				// 汇总当前结算类型内所有商户的不同SKU的交易成功结算信息和退货结算信息,存入SKU结算正式表
				summarizeSkuSettleInfo();
				// 生成本结算类型结算帐期的财务汇总数据
				genFinanlBill();
			}
			
			//生成结算单对应的发票单据信息
			if(type.equals("4497477900040001")) {
				genFinanlBillTicket1();
			}else if(type.equals("4497477900040004")) {
				genFinanlBillTicket4();
			}
		}
		
		System.out.println("自定义商户结算单生成完毕！");
		
	}
	
	
	/**
	 * 抓取自定义结算商户当前帐期成交订单的sku信息
	 * 
	 * @param smallSellerCode
	 *            商户编号
	 * @param start_time
	 *            结算开始时间
	 * @param end_time
	 *            结算结束时间
	 * @param tuiStart
	 *            退货开始时间
	 * @param tuiEnd
	 *            退货结束时间
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public MDataMap grabSkuDealed() throws Exception {
		MDataMap map = new MDataMap();
		
		String now_status = "";
		if("4497478100070001".equals(bill_point)) {
			// 订单完成
			now_status = "4497153900010005";
		}else if("4497478100070002".equals(bill_point)) {
			// 订单发货
			now_status = "4497153900010003";
		}
	    
	    try {
	    	if (!isTestSeller(smallSellerCode)) {
	    		map.put("small_seller_code", smallSellerCode);
	    		map.put("create_time_from", saleStart);
	    		map.put("create_time_end", saleEnd);
	    		map.put("now_status", now_status);
	    		
	    		// 结算支付类型,可能还要新增类型
	    		String orderSql = "SELECT " + "a.code, a.create_time " + "FROM " + "(" + "SELECT code, create_time "
	    				+ "FROM logcenter.lc_orderstatus " + "WHERE now_status=:now_status AND zid > 114806056 " + "GROUP BY code "
	    				+ "HAVING create_time>=:create_time_from " + "AND create_time<=:create_time_end "
	    				+ ") a, ordercenter.oc_orderinfo b " + "WHERE a.code = b.order_code "
	    				+ "AND b.small_seller_code=:small_seller_code " + "AND b.seller_code IN ('SI2003', 'SI3003') " +
	    				"AND b.pay_type in (" + getPayType() + ") ";
	    		
	    		List<Map<String, Object>> successList = DbUp.upTable("lc_orderstatus").dataSqlList(orderSql, map);
	    		map.clear();
	    		if (null != successList && !successList.isEmpty()) {
	    			for (Map<String, Object> success : successList) {
	    				map.put(success.get("code").toString(), success.get("create_time").toString());
	    			}
	    		}
	    	}
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	sendMail();
	    	sendWx();
	    }
	    if (null != map && !map.isEmpty()) {
	    	genSkuDealedSettleInfo(map, saleStart, saleEnd, returnStart, returnEnd);
	    }
	    
		return map;
	}

	/**
	 * 生成自定义结算商户当前帐期成交订单的sku结算信息
	 * 
	 * @param map
	 * @param start_time
	 *            开始结算时间
	 * @param end_time
	 * @param tuiStart
	 *            开始退货时间
	 * @param tuiEnd
	 * @throws Exception
	 */
	private void genSkuDealedSettleInfo(MDataMap map, String saleStart, String saleEnd, String returnStart,
			String returnEnd) throws Exception {
		// 排除已经结算过的订单
		map = dealBillOrder(map);
		
		// 根据有效的订单编号查询sku 信息
		String ordersql = JoinSql.getJoinOrderCode(map);
		String skuSql = "select order_code,product_code,sku_code,sku_name,sku_price,sku_num,cost_price,show_price from oc_orderdetail where order_code "
				+ ordersql;
		List<Map<String, Object>> list = DbUp.upTable("oc_orderdetail").dataSqlList(skuSql, null);

		for (Map<String, Object> map2 : list) {
			String order_code = map2.get("order_code").toString();
			String product_code = map2.get("product_code").toString();
			Map<String, String> pcExtMap = DbUp.upTable("pc_productinfo_ext").oneWhere(
					"settlement_type,product_code,purchase_type", "", "product_code=:product_code", "product_code",
					product_code);
			if (pcExtMap != null) {
				// 判断此商品结算类型是否为常规结算并且采购类型为代销/服务费结算并且采购类型为代收代付
				// 4497471600110001:常规结算 4497471600160001:代销
				// 4497471600110003:服务费结算 4497471600160003:代收代付
				String settlement_type = StringUtils.isNotEmpty(pcExtMap.get("settlement_type"))
						? pcExtMap.get("settlement_type").toString() : "";
				String purchase_type = StringUtils.isNotEmpty(pcExtMap.get("purchase_type"))
						? pcExtMap.get("purchase_type") : "";
						
				if ((("4497478100050001".equals(uc_seller_type) || "4497478100050005".equals(uc_seller_type)) && settlement_type.equals("4497471600110001") && purchase_type.equals("4497471600160001")) 
						|| (("4497478100050004".equals(uc_seller_type) || "4497478100050003".equals(uc_seller_type) || "4497478100050002".equals(uc_seller_type)) && settlement_type.equals("4497471600110003") && purchase_type.equals("4497471600160003"))) {
					
					String sku_code = map2.get("sku_code").toString();
					
					// 查询结算单中该sku订单是否生成,如果已经生成了就跳过
					MDataMap orderBill = DbUp.upTable("oc_bill_final_export_tmp").one("settle_code", getSettleCode(),"order_code",order_code,"return_num","0","product_code",product_code,"sku_code",sku_code);
					if(orderBill != null) {
						
					}else {
						String sku_name = map2.get("sku_name").toString();
						// double sku_price =
						// Double.valueOf(map2.get("sku_price").toString());
						double success_sku_num = Double.valueOf(map2.get("sku_num").toString());
						double cost_price = Double.valueOf(map2.get("cost_price").toString());
						
						// sku售价
						double sell_price = 0;
						if("4497478100050001".equals(uc_seller_type) || "4497478100050005".equals(uc_seller_type)){
							Map<String, String> skumap = DbUp.upTable("pc_skuinfo").oneWhere("sell_price,sku_code", "",
									"sku_code=:sku_code", "sku_code", sku_code);
							if (skumap != null) {
								sell_price = Double.valueOf(skumap.get("sell_price").toString());
							}
						}else if("4497478100050004".equals(uc_seller_type) || "4497478100050003".equals(uc_seller_type) || "4497478100050002".equals(uc_seller_type)) {
							if (StringUtils.isNotBlank(map2.get("show_price").toString())) {
								sell_price = Double.valueOf(map2.get("show_price").toString());
							}
						}
						// 商品信息
						Map<String, String> productMap = DbUp.upTable("pc_productinfo").oneWhere(
								"product_code,product_name,tax_rate,small_seller_code,seller_code", "",
								"product_code=:product_code", "product_code", product_code);
						String product_name = "";
						double tax_rate = 0.00;
						String small_seller_code = "";
						// 增加了商户的编码(seller_code)作为通路 例如SI2003,SI3003
						String seller_code = "";
						if (productMap != null) {
							product_name = productMap.get("product_name").toString();
							if("4497478100050001".equals(uc_seller_type) || "4497478100050005".equals(uc_seller_type)){
								tax_rate = Double.valueOf(productMap.get("tax_rate").toString() == null
										|| productMap.get("tax_rate").toString() == "" ? "0.00"
												: productMap.get("tax_rate").toString());
							}
							small_seller_code = productMap.get("small_seller_code").toString();
							seller_code = productMap.get("seller_code").toString();
						}
						// 获取商户的信息
						// 新增查询字段money_collection_way（质保金收取方式） 2016-10-28 zhy
						Map<String, String> sellerInfoMap = DbUp.upTable("uc_seller_info_extend").oneWhere(
								"seller_company_name,branch_name,bank_account,quality_retention_money,money_proportion,money_collection_way",
								"", "small_seller_code=:small_seller_code", "small_seller_code", small_seller_code);
						String seller_company_name = "";
						String branch_name = "";
						String bank_account = "";
						double max_quality_retention_money = 0;
						double money_proportion_rate = 0;
						String money_collection_way = "账扣";// 质保金收取方式，默认是账扣
						if (sellerInfoMap != null) {
							seller_company_name = sellerInfoMap.get("seller_company_name") == null ? ""
									: sellerInfoMap.get("seller_company_name").toString();
							branch_name = sellerInfoMap.get("branch_name").toString();// 开户行
							bank_account = sellerInfoMap.get("bank_account").toString();// 帐号
							max_quality_retention_money = Double
									.valueOf((sellerInfoMap.get("quality_retention_money") == null
									|| sellerInfoMap.get("quality_retention_money") == "" ? "0.00"
											: sellerInfoMap.get("quality_retention_money").toString()));// 质保金
							money_proportion_rate = Double.valueOf((sellerInfoMap.get("money_proportion") == null
									|| sellerInfoMap.get("money_proportion") == "" ? "0.00"
											: sellerInfoMap.get("money_proportion").toString()));// 质保金比例
							// 查询质保金收取方式编码对应中文名称 2016-10-28 zhy
							if (StringUtils.equals("4497477900050001",
									sellerInfoMap.get("money_collection_way").toString())) {
								money_collection_way = "预付";
							}
						}
						
						double success_amount = 0.00;
						double settle_amount = 0.00;
						if("4497478100050001".equals(uc_seller_type) || "4497478100050005".equals(uc_seller_type)){
							success_amount = success_sku_num * cost_price;
							settle_amount = success_sku_num * cost_price;
						}else if("4497478100050004".equals(uc_seller_type) || "4497478100050003".equals(uc_seller_type) || "4497478100050002".equals(uc_seller_type)) {
							success_amount = success_sku_num * sell_price;
							settle_amount = success_sku_num * sell_price;
						}
						
						if (small_seller_code.equals("SF03100294") || small_seller_code.equals("SF03100327")
								|| small_seller_code.equals("SF03100329") || small_seller_code.equals("SF03KJT")
								|| small_seller_code.equals("SF03MLG") || small_seller_code.equals("SF03100393")
								|| small_seller_code.equals("SF03100466") || small_seller_code.equals("SF03100443")
								|| small_seller_code.equals("SF03100541") || small_seller_code.equals("SF03100542")) {
							continue;
						}
						
						String product_settle_type = "";
						if("4497478100050001".equals(uc_seller_type) || "4497478100050005".equals(uc_seller_type)){
							product_settle_type = "常规结算";
						}else if("4497478100050004".equals(uc_seller_type)) {
							product_settle_type = "平台入驻结算";
						}else if("4497478100050003".equals(uc_seller_type)) {
							product_settle_type = "跨境直邮结算";
						}else if("4497478100050002".equals(uc_seller_type)) {
							product_settle_type = "跨境商户结算";
						}
						
						DbUp.upTable("oc_bill_final_export_tmp").insert("settle_code", getSettleCode(), "order_code",
								order_code, "product_settle_type", product_settle_type, "passage", seller_code, "product_code",
								product_code, "product_name", product_name, "sku_code", sku_code, "sku_name", sku_name,
								"cost_price", String.valueOf(cost_price), "sell_price", String.valueOf(sell_price),
								"small_seller_code", small_seller_code, "small_seller_name", seller_company_name,
								"branch_name", branch_name, "branch_account", bank_account, "success_num",
								String.valueOf(success_sku_num), "success_amount", 
								String.valueOf(success_amount), "return_num", String.valueOf("0"),
								"return_amount", String.valueOf("0.00"), "settle_num", String.valueOf(success_sku_num),
								"settle_amount", String.valueOf(settle_amount), "max_retention_money",
								String.valueOf(max_quality_retention_money), "rate", String.valueOf(tax_rate),
								"money_proportion_rate", String.valueOf(money_proportion_rate), "start_time", saleStart,
								"end_time", saleEnd, "tui_start", returnStart, "tui_end", returnEnd,
								// 新增添加质保金收取方式字段 2016-10-28 zhy
								"money_collection_way", money_collection_way);
						
					}
				}
			}
		}
	}

	/**
	 * 自定义结算周期有效退货订单号
	 * 
	 * @param smallSellerCode
	 *            商户编号
	 * @param returnStart
	 *            退货开始时间
	 * @param returnEnd
	 *            退货结束时间
	 * @param saleStart
	 *            销售开始时间
	 * @param saleEnd
	 *            销售结束时间
	 * @return
	 * @throws Exception
	 */
	public MDataMap grabSkuReturned() throws Exception {
		
		MDataMap map = new MDataMap();
	    
	    try {
	    	if (!isTestSeller(smallSellerCode)) {
	    		map.put("small_seller_code", smallSellerCode);
	    		map.put("create_time_from", returnStart);
	    		map.put("create_time_end", returnEnd);
	    		// 通过审核(收货入库)
	    		map.put("status", "4497153900050001");
	    		// 新添加查询字段return_code
	    		String sql = "SELECT a.create_time,	b.order_code,b.return_code "
	    				+ "FROM logcenter.lc_return_goods_status a, ordercenter.oc_return_goods b "
	    				+ "WHERE a.return_no = b.return_code "
	    				+ "AND a.status=:status AND b.small_seller_code=:small_seller_code "
	    				+ "AND a.create_time>=:create_time_from " + "AND a.create_time<=:create_time_end";
	    		
	    		List<Map<String, Object>> returnList = DbUp.upTable("oc_return_goods").dataSqlList(sql, map);
	    		map.clear();
	    		if (null != returnList && returnList.size() > 0) {
	    			for (Map<String, Object> returnItem : returnList) {
	    				map.put(returnItem.get("return_code").toString(), returnItem.get("order_code").toString());
	    			}
	    		}
	    	}
	    	
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	sendMail();
	    	sendWx();
	    }
	    if (null != map && !map.isEmpty()) {
	    	genSkuReturnedSettleInfo(map, returnStart, returnEnd, saleStart, saleEnd);
	    }
	
		return map;
	}

	private void genSkuReturnedSettleInfo(MDataMap map, String returnStart, String returnEnd, String saleStart, String saleEnd) {
		//排除已经结算过的退货单
		map = dealBillReturn(map);
		
		String product_code = "";
		String sku_name = "";
		double cost_price = 0;
		// 根据有效的订单编号查询sku 信息
		String ordersql = JoinSql.getJoinOrderCodeForReturn(map);
		// 获取return_code查询集合
		String returnCodeSql = JoinSql.getJoinReturnCode(map);
		// 部分退换货sql
		String skuSql = "SELECT a.return_code, a.order_code AS order_code, b.sku_code AS sku_code, sum(b.count) AS count, b.return_code "
				+ "FROM ordercenter.oc_return_goods a, ordercenter.oc_return_goods_detail b " + "WHERE	a.order_code "
				+ ordersql + " and a.return_code=b.return_code and a.status='4497153900050001' "
				// 添加查询条件2017-02-08 zhy
				+ "and a.return_code " + returnCodeSql + " group by a.order_code, b.return_code, b.sku_code";
		List<Map<String, Object>> list = DbUp.upTable("oc_return_goods_detail").dataSqlList(skuSql, null);
		for (Map<String, Object> returnMap : list) {
			String order_code = returnMap.get("order_code").toString();
			String sku_code = returnMap.get("sku_code").toString();
			String skudetail = "select product_code,sku_name,sku_price,cost_price from oc_orderdetail where order_code = '"
					+ order_code + "' and sku_code= '" + sku_code + "'";
			List<Map<String, Object>> skulist = DbUp.upTable("oc_orderdetail").dataSqlList(skudetail, null);
			for (Map<String, Object> skuMap : skulist) {
				product_code = skuMap.get("product_code").toString();
				sku_name = skuMap.get("sku_name").toString();
				cost_price = Double.valueOf(skuMap.get("cost_price").toString());
			}
			
			// 查询结算单中该sku退货单是否生成,如果已经生成了就跳过
			MDataMap returnBill = DbUp.upTable("oc_bill_final_export_tmp").one("settle_code", getSettleCode(),"order_code",order_code,"success_num","0","product_code",product_code,"sku_code",sku_code);
			if(returnBill != null) {
				
			}else {
				
				Map<String, String> pcExtMap = DbUp.upTable("pc_productinfo_ext").oneWhere(
						"settlement_type,product_code,purchase_type", "", "product_code=:product_code", "product_code",
						product_code);
				if (pcExtMap != null) {
					// 判断此商品结算类型是否为常规结算并且采购类型为代销/服务费结算并且采购类型为代收代付
					// 4497471600110001:常规结算 4497471600160001:代销
					// 4497471600110003:服务费结算 4497471600160003:代收代付
					String settlement_type = StringUtils.isNotEmpty(pcExtMap.get("settlement_type"))
							? pcExtMap.get("settlement_type").toString() : "";
				String purchase_type = StringUtils.isNotEmpty(pcExtMap.get("purchase_type"))
						? pcExtMap.get("purchase_type").toString() : "";
					if ((("4497478100050001".equals(uc_seller_type) || "4497478100050005".equals(uc_seller_type)) && settlement_type.equals("4497471600110001") && purchase_type.equals("4497471600160001")) 
							|| (("4497478100050004".equals(uc_seller_type) || "4497478100050003".equals(uc_seller_type) || "4497478100050002".equals(uc_seller_type)) && settlement_type.equals("4497471600110003") && purchase_type.equals("4497471600160003"))) {
						
						double return_sku_num = Double.valueOf(returnMap.get("count").toString());// 每个订单所包含的退货单上sku_code的退货数量
						// sku售价
						double sell_price = 0;
						if("4497478100050001".equals(uc_seller_type) || "4497478100050005".equals(uc_seller_type)){
							Map<String, String> skumap = DbUp.upTable("pc_skuinfo").oneWhere("sell_price,sku_code", "",
									"sku_code=:sku_code", "sku_code", sku_code);
							if (skumap != null) {
								sell_price = Double.valueOf(skumap.get("sell_price").toString());
							}
						}else if("4497478100050004".equals(uc_seller_type) || "4497478100050003".equals(uc_seller_type) || "4497478100050002".equals(uc_seller_type)) {
							MDataMap skuShowPrice = DbUp.upTable("oc_orderdetail").oneWhere("show_price", "",
									"order_code=:order_code and sku_code=:sku_code", "order_code", order_code,"sku_code",sku_code);
							if (skuShowPrice != null) {
								sell_price = Double.valueOf(skuShowPrice.get("show_price"));
							}
						}
						// 商品信息
						Map<String, String> productMap = DbUp.upTable("pc_productinfo").oneWhere(
								"product_code,product_name,tax_rate,small_seller_code,seller_code", "",
								"product_code=:product_code", "product_code", product_code);
						String product_name = "";
						double tax_rate = 0.00;
						String small_seller_code = "";
						// 增加了商户的编码(seller_code)作为通路 例如SI2003,SI3003
						String seller_code = "";
						if (productMap != null) {
							product_name = productMap.get("product_name").toString();
							if("4497478100050001".equals(uc_seller_type) || "4497478100050005".equals(uc_seller_type)){							
								tax_rate = Double.valueOf(productMap.get("tax_rate").toString());
							}
							small_seller_code = productMap.get("small_seller_code").toString();
							seller_code = productMap.get("seller_code").toString();
						}
						// 获取商户的信息
						// 新增查询字段money_collection_way（质保金收取方式） 2016-10-28 zhy
						Map<String, String> sellerInfoMap = DbUp.upTable("uc_seller_info_extend").oneWhere(
								"seller_company_name,branch_name,bank_account,quality_retention_money,money_proportion,money_collection_way",
								"", "small_seller_code=:small_seller_code", "small_seller_code", small_seller_code);
						String seller_company_name = "";
						String branch_name = "";
						String bank_account = "";
						double max_quality_retention_money = 0;
						double money_proportion_rate = 0;
						String money_collection_way = "账扣";// 质保金收取方式，默认为账扣
						if (sellerInfoMap != null) {
							seller_company_name = sellerInfoMap.get("seller_company_name") == null ? ""
									: sellerInfoMap.get("seller_company_name").toString();
							branch_name = sellerInfoMap.get("branch_name").toString();// 开户行
							bank_account = sellerInfoMap.get("bank_account").toString();// 帐号
							max_quality_retention_money = Double
									.valueOf((sellerInfoMap.get("quality_retention_money") == null
									|| sellerInfoMap.get("quality_retention_money") == "" ? "0.00"
											: sellerInfoMap.get("quality_retention_money").toString()));// 质保金
							money_proportion_rate = Double.valueOf((sellerInfoMap.get("money_proportion") == null
									|| sellerInfoMap.get("money_proportion") == "" ? "0.00"
											: sellerInfoMap.get("money_proportion").toString()));// 质保金比例
							// 查询质保金收取方式编码对应中文名称 2016-10-28 zhy
							if (StringUtils.equals("4497477900050001",
									sellerInfoMap.get("money_collection_way").toString())) {
								money_collection_way = "预付";
							}
						}
						// 把数据入库订单结算表
						double return_amount = 0.00;
						double settle_amount = 0.00;
						if("4497478100050001".equals(uc_seller_type) || "4497478100050005".equals(uc_seller_type)){
							return_amount = return_sku_num * cost_price;
							settle_amount = return_sku_num * cost_price;
						}else if("4497478100050004".equals(uc_seller_type) || "4497478100050003".equals(uc_seller_type) || "4497478100050002".equals(uc_seller_type)) {
							return_amount = return_sku_num * sell_price;
							settle_amount = return_sku_num * sell_price;
						}
						
						if (small_seller_code.equals("SF03100294") || small_seller_code.equals("SF03100327")
								|| small_seller_code.equals("SF03100329") || small_seller_code.equals("SF03KJT")
								|| small_seller_code.equals("SF03MLG") || small_seller_code.equals("SF03100393")
								|| small_seller_code.equals("SF03100466") || small_seller_code.equals("SF03100443")
								|| small_seller_code.equals("SF03100541") || small_seller_code.equals("SF03100542")) {
							continue;
						}
						
						String product_settle_type = "";
						if("4497478100050001".equals(uc_seller_type) || "4497478100050005".equals(uc_seller_type)){
							product_settle_type = "常规结算";
						}else if("4497478100050004".equals(uc_seller_type)) {
							product_settle_type = "平台入驻结算";
						}else if("4497478100050003".equals(uc_seller_type)) {
							product_settle_type = "跨境直邮结算";
						}else if("4497478100050002".equals(uc_seller_type)) {
							product_settle_type = "跨境商户结算";
						}
						
						DbUp.upTable("oc_bill_final_export_tmp").insert("settle_code", getSettleCode(), "order_code",
								order_code, "product_settle_type", product_settle_type, "passage", seller_code, "product_code",
								product_code, "product_name", product_name, "sku_code", sku_code, "sku_name", sku_name,
								"cost_price", String.valueOf(cost_price), "sell_price", String.valueOf(sell_price),
								"small_seller_code", small_seller_code, "small_seller_name", seller_company_name,
								"branch_name", branch_name, "branch_account", bank_account, "success_num",
								String.valueOf("0.00"), "success_amount", String.valueOf("0.00"), "return_num",
								String.valueOf(return_sku_num), "return_amount", String.valueOf(return_amount),
								"settle_num", String.valueOf(return_sku_num), "settle_amount", String.valueOf(settle_amount), 
								"tui_start", returnStart, "tui_end", returnEnd, "start_time", saleStart, "end_time", saleEnd, 
								"max_retention_money", String.valueOf(max_quality_retention_money),  "money_proportion_rate", 
								String.valueOf(money_proportion_rate),  "money_collection_way", money_collection_way, 
								"rate", String.valueOf(tax_rate));
					}
				}
			}
			
		}
	}


	/**
	 * 汇总本结算类型下各商户的SKU结算明细,结算数=成功交易数-退货数
	 */
	public void summarizeSkuSettleInfo() {
		String saleStartDate = saleStart; // 结算开始时间
		String saleEndDate = saleEnd; // 结算结束时间
		String returnStartDate = returnStart; // 退货开始时间
		String returnEndDate = returnEnd; // 退货结束时间
		StringBuffer sb = new StringBuffer();
		// 增加了passage通路字段
		sb.append(
				"select passage,product_code,product_name,sku_code,sku_name,cost_price,sell_price,product_contract_sign,product_alter,");
		// 新添加查询字段money_collection_way（质保金收取方式）2016-10-28 zhy
		sb.append("money_collection_way,");
		sb.append("small_seller_code,small_seller_name,branch_name,branch_account,supplier_level,supplier_rate,");
		sb.append(
				"sum(success_num) as success_num, sum(success_amount) as success_amount,sum(return_num) as return_num,");
		sb.append("sum(return_amount) as return_amount,max_retention_money,rate,add_amount,money_proportion_rate");
		
		sb.append(" from oc_bill_final_export_tmp where start_time='" + saleStartDate + "' and end_time='" + saleEndDate
				+ "'");
		sb.append(" and tui_start='" + returnStartDate + "' and tui_end='" + returnEndDate + "'");
		sb.append(" and settle_code= '" + getSettleCode() + "'");
		sb.append(" group by passage,small_seller_code,product_code,sku_code,cost_price");
		sb.append(" ORDER BY passage,small_seller_code,product_code,sku_code,cost_price ASC");
		// 供应商编号+商品编号+SKU编号+成本
		List<Map<String, Object>> list = DbUp.upTable("oc_bill_final_export_tmp").dataSqlList(sb.toString(), null);
		// 求
		String product_code = "";
		String sku_code = "";
		if (list.size() > 0) {
			for (Map<String, Object> mapinfo : list) {
				String passage = mapinfo.get("passage").toString();
				// 获取被复制商品的sku编号和商品编号
				product_code = mapinfo.get("product_code").toString();
				String product_name = mapinfo.get("product_name").toString();
				sku_code = mapinfo.get("sku_code").toString();
				String sku_name = mapinfo.get("sku_name").toString();
				// 查询结算单中该sku是否结算,如果已经生成了就删除重新生成
				MDataMap bill_final_export = DbUp.upTable("oc_bill_final_export").one("settle_code", getSettleCode(),"product_code",product_code,"sku_code",sku_code);
				if(bill_final_export != null) {
					// 如果已经生成了就删除重新生成
					DbUp.upTable("oc_bill_final_export").dataExec("delete from ordercenter.oc_bill_final_export where settle_code='"+getSettleCode()+"' and product_code='"+product_code+"' and sku_code='"+sku_code+"'", new MDataMap());
				}
					
				double cost_price = Double.valueOf(mapinfo.get("cost_price").toString());
				double sell_price = Double.valueOf(mapinfo.get("sell_price").toString());
				String small_seller_code = mapinfo.get("small_seller_code").toString();
				String small_seller_name = mapinfo.get("small_seller_name").toString();
				String branch_name = mapinfo.get("branch_name").toString();
				String branch_account = mapinfo.get("branch_account").toString();
				
				String sSql = "SELECT uc_seller_type FROM uc_seller_info_extend WHERE small_seller_code = '"+small_seller_code+"'";
				Map<String, Object> seller_info_extend = DbUp.upTable("uc_seller_info_extend").dataSqlOne(sSql, new MDataMap());
				String uc_seller_type = MapUtils.getString(seller_info_extend, "uc_seller_type");
				
				double success_num = Double.valueOf(mapinfo.get("success_num").toString());
				double success_amount = 0.00;
				// 退货数量和金额
				double return_num = Double.valueOf(mapinfo.get("return_num").toString());
				double return_amount = 0.00;
				// 结算数量和金额,结算数等于成功数减退货数
				double settle_num = success_num - return_num;
				double settle_amount = 0.00;
				if("4497478100050001".equals(uc_seller_type) || "4497478100050005".equals(uc_seller_type)){							
					success_amount = success_num * cost_price;
					return_amount = return_num * cost_price;
					settle_amount = settle_num * cost_price;
				}else if("4497478100050004".equals(uc_seller_type) || "4497478100050003".equals(uc_seller_type) || "4497478100050002".equals(uc_seller_type)) {
					success_amount = success_num * sell_price;
					return_amount = return_num * sell_price;
					settle_amount = settle_num * sell_price;
				}
				
				// 计算服务费
				double service_fee = 0.00;
				if("4497478100050004".equals(uc_seller_type) || "4497478100050003".equals(uc_seller_type) || "4497478100050002".equals(uc_seller_type)) {
					service_fee = settle_num * (sell_price - cost_price);
				}
				double rate = 0.00;
				if("4497478100050001".equals(uc_seller_type) || "4497478100050002".equals(uc_seller_type) || "4497478100050003".equals(uc_seller_type) || "4497478100050005".equals(uc_seller_type)) {
					rate = Double.valueOf(mapinfo.get("rate").toString());
				}
				
				double max_retention_money = Double.valueOf(mapinfo.get("max_retention_money").toString());
				double add_amount = Double
						.valueOf(mapinfo.get("add_amount") == null ? "0.00" : mapinfo.get("add_amount").toString());
				String input_tax_subtotal = "0.00";
				if("4497478100050001".equals(uc_seller_type) || "4497478100050005".equals(uc_seller_type)){							
					input_tax_subtotal = String.valueOf(settle_amount / (1 + rate) * rate);
				}
				
				// 已扣质保金质保金
				// double old_deduct_retention_money=0;
				double old_period_retention_money = 0;
				double deduct_retention_money = 0;
				double period_retention_money_sum = 0;
				
				/**
				 * 平台商品代收代付结算单（账扣方式保证金），按照保证金账扣比例，在平台结算单上增加保证金扣除项。
				 * 预付：最大保证金按系统取数，本期保证金：0元。
				 * 账扣：本期代收货款大于0，按照扣除比例扣除保证金，直到扣除金额达到最大保证金金额；本期代收货款小于等于0，则本期保证金为0
				 * 。保证金不做逆向处理。抬头形式如下（标黄色为新增）<br>
				 * 2016-10-31 zhy<br>
				 */
				// ##############start##################
				double period_retention_money = 0;// 本期质保金
				double money_proportion_rate = 0;// 质保金比例
				// 质保金收取方式
				String money_collection_way = mapinfo.get("money_collection_way") != null
						? mapinfo.get("money_collection_way").toString() : "账扣";
				if (StringUtils.equals(money_collection_way, "账扣")) {
					// 此sql有一个坑select deduct_retention_money...应变为select
					// min(deduct_retention_money)....,不加min有时deduct_retention_money为0.0有时为某一期的数据
					// 会导致该期质保金会被加,双份。
					// 因为后面sum(period_retention_money)已累加,所以deduct_retention_money=old_deduct_retention_money+old_period_retention_money
					// 变为deduct_retention_money=old_period_retention_money
					
					MDataMap map2 = new MDataMap();
					map2.put("product_code", product_code);
					map2.put("small_seller_code", small_seller_code);
					map2.put("sku_code", sku_code);
					map2.put("cost_price", String.valueOf(cost_price));
					String sql = "select deduct_retention_money,sum(period_retention_money) as period_retention_money from oc_bill_seller_retention_money where"
							+ " small_seller_code=:small_seller_code";
					Map<String, Object> mapbao = DbUp.upTable("oc_bill_seller_retention_money").dataSqlOne(sql, map2);
					if (mapbao != null) {
						// old_deduct_retention_money=Double.valueOf(mapbao.get("deduct_retention_money")==null?"0.00":mapbao.get("deduct_retention_money").toString());
						old_period_retention_money = Double.valueOf(mapbao.get("period_retention_money") == null
								? "0.00" : mapbao.get("period_retention_money").toString());
						// deduct_retention_money=old_deduct_retention_money+old_period_retention_money;
						deduct_retention_money = old_period_retention_money;
					}
					// 结算质保金
					// 当月某商户所有sku产生的质保金之和
					MDataMap map3 = new MDataMap();
					map3.put("settle_code", getSettleCode());
					map3.put("small_seller_code", small_seller_code);
					String sql1 = "select sum(period_retention_money) as period_retention_money from oc_bill_final_export where "
							+ "small_seller_code=:small_seller_code and settle_code=:settle_code";
					Map<String, Object> mapbao1 = DbUp.upTable("oc_bill_final_export").dataSqlOne(sql1, map3);
					if (mapbao1 != null) {
						period_retention_money_sum = Double.valueOf(mapbao1.get("period_retention_money") == null
								? "0.00" : mapbao1.get("period_retention_money").toString());
					}
					
					// if (剩余质保金-结算金额*质保金比例）>0？结算金额*质保金比例：剩余质保金
					// 剩余质保金 =最大质保-已扣质保-∑本期质保
					money_proportion_rate = Double.valueOf(mapinfo.get("money_proportion_rate") == null ? "0.00"
							: mapinfo.get("money_proportion_rate").toString());// 质保金比例
					double remain_proportion_money = max_retention_money - deduct_retention_money
							- period_retention_money_sum;
					if (remain_proportion_money > 0) {
						if (settle_amount < 0) {
							// 本期没有结算
							period_retention_money = 0;
						} else if (remain_proportion_money - settle_amount * money_proportion_rate > 0) {
							// 还未扣完
							period_retention_money = settle_amount * money_proportion_rate;
						} else {
							// 剩于要扣的质保金小于本期应扣质保金,则本期应扣质保金等于剩余质保金
							period_retention_money = remain_proportion_money;
						}
					} else {
						period_retention_money = 0;
					}
				}
				// ##############end####################
				
				double total = settle_amount - add_amount;
				
				String product_settle_type = "";
				if("4497478100050001".equals(uc_seller_type) || "4497478100050005".equals(uc_seller_type)){
					product_settle_type = "常规结算";
				}else if("4497478100050004".equals(uc_seller_type)) {
					product_settle_type = "平台入驻结算";
				}else if("4497478100050003".equals(uc_seller_type)) {
					product_settle_type = "跨境直邮结算";
				}else if("4497478100050002".equals(uc_seller_type)) {
					product_settle_type = "跨境商户结算";
				}
				
				DbUp.upTable("oc_bill_final_export").insert("settle_code", getSettleCode(), "product_settle_type",
						product_settle_type, "passage", passage, "product_code", product_code, "product_name", product_name,
						"sku_code", sku_code, "sku_name", sku_name, "cost_price", String.valueOf(cost_price),
						"sell_price", String.valueOf(sell_price), "small_seller_code", small_seller_code,
						"small_seller_name", small_seller_name, "branch_name", branch_name, "branch_account",
						branch_account, "success_num", String.valueOf(success_num), "success_amount",
						String.valueOf(success_amount), "return_num", String.valueOf(return_num), "return_amount",
						String.valueOf(return_amount), "settle_num", String.valueOf(settle_num), "settle_amount",
						String.valueOf(settle_amount), "service_fee", String.valueOf(service_fee), 
						"max_retention_money", String.valueOf(max_retention_money),
						"rate", String.valueOf(rate), "money_proportion_rate", String.valueOf(money_proportion_rate),
						"deduct_retention_money", String.valueOf(deduct_retention_money), "period_retention_money",
						String.valueOf(period_retention_money), "rate", String.valueOf(rate), "input_tax_subtotal",
						input_tax_subtotal, "total", String.valueOf(total), "start_time", saleStartDate, "end_time",
						saleEndDate, "tui_start", returnStartDate, "tui_end", returnEndDate,
						// 新增添加质保金收取方式字段 2016-10-28 zhy
						"money_collection_way", mapinfo.get("money_collection_way").toString());
			
			}
		}
		if (list.size() > 0) {
			retentionMoney(saleStartDate, saleEndDate);
		}
	}

	/**
	 * 汇总本结算类型下各商户sku质保金
	 * 
	 * @param saleStartDate
	 * @param saleEndDate
	 */
	private void retentionMoney(String saleStartDate, String saleEndDate) {
		MDataMap map = new MDataMap();
		map.put("start_time", saleStartDate);
		map.put("end_time", saleEndDate);
		String settle_code = getSettleCode();
		map.put("settle_code", settle_code);
		// map.put("small_seller_code", small_seller_code);
		/*String sql = "select small_seller_code, max_retention_money, deduct_retention_money, sum(period_retention_money) as period_retention_money, start_time "
				+ " from oc_bill_final_export where start_time=:start_time and end_time=:end_time and settle_code=:settle_code AND product_settle_type!='平台入驻结算' "
				+ " group by small_seller_code ";
		sql += " UNION ALL ";
		sql += " select small_seller_code,max_retention_money,deduct_retention_money,sum(period_retention_money) as period_retention_money,start_time "
				+ " from oc_bill_final_export where start_time=:start_time and end_time=:end_time and settle_code=:settle_code AND product_settle_type='平台入驻结算' "
				+ " and money_collection_way='账扣' "
				+ " group by small_seller_code ";*/
		String sql = "";
		if(settleType.equals("4497477900040001")) {
			sql = "select small_seller_code,max_retention_money,deduct_retention_money,sum(period_retention_money) as period_retention_money,"
					+ " start_time from oc_bill_final_export where start_time=:start_time and end_time=:end_time and settle_code=:settle_code"
					+ " group by small_seller_code ";
		}else if(settleType.equals("4497477900040004")) {			
			sql = "select small_seller_code,max_retention_money,deduct_retention_money,sum(period_retention_money) as period_retention_money,"
					+ " start_time from oc_bill_final_export where start_time=:start_time and end_time=:end_time and settle_code=:settle_code "
					+ " and money_collection_way='账扣' " + " group by small_seller_code ";
		}
		
		List<Map<String, Object>> list = DbUp.upTable("oc_bill_final_export").dataSqlList(sql, map);
		for (Map<String, Object> mapinfo : list) {
			String small_seller_code = mapinfo.get("small_seller_code").toString();
			// 查询结算单是否汇总,如果已经汇总了就删除重新汇总
			MDataMap retention_money = DbUp.upTable("oc_bill_seller_retention_money").one("settle_code", getSettleCode(),"small_seller_code",small_seller_code);
			if(retention_money != null) {
				// 如果已经汇总了就删除重新汇总
				DbUp.upTable("oc_bill_seller_retention_money").dataExec("delete from ordercenter.oc_bill_seller_retention_money where settle_code='"+getSettleCode()+"' and small_seller_code='"+small_seller_code+"'", new MDataMap());
			}
				
			double max_retention_money = Double.valueOf(mapinfo.get("max_retention_money").toString());
			double deduct_retention_money = Double.valueOf(mapinfo.get("deduct_retention_money").toString());
			double period_retention_money = Double.valueOf(mapinfo.get("period_retention_money").toString());
			DbUp.upTable("oc_bill_seller_retention_money").insert("small_seller_code", small_seller_code,
					"max_retention_money", String.valueOf(max_retention_money), "deduct_retention_money",
					String.valueOf(deduct_retention_money), "period_retention_money",
					String.valueOf(period_retention_money), "settle_time", saleStartDate, "settle_code",
					getSettleCode());
			
		}
	}

	/**
	 * 生成本结算帐期内财务汇总记录
	 */
	protected void genFinanlBill() {
		MDataMap map = new MDataMap();
		map.put("start_time", saleStart);
		map.put("end_time", saleEnd);
		map.put("tui_start", returnStart);
		map.put("tui_end", returnEnd);
		map.put("settle_code", getSettleCode());

		List<Map<String, Object>> list = DbUp.upTable("oc_bill_final_export")
				.dataSqlList(
						"select small_seller_code as small_seller_code,"
								+ "sum(settle_amount) as settle_amount, sum(service_fee) as service_fee, sum(add_amount) as related_charges, "
								+ "sum(period_retention_money) as period_money from oc_bill_final_export "
								+ "where start_time=:start_time and end_time=:end_time and tui_start=:tui_start and tui_end=:tui_end and settle_code=:settle_code",
						map);

		if (null != list && !list.isEmpty()) {
			for (Map<String, Object> mapinfo : list) {
				double settle_amount = Double.valueOf(
						mapinfo.get("settle_amount") == null ? "0.00" : mapinfo.get("settle_amount").toString());
				double service_fee = Double
						.valueOf(mapinfo.get("service_fee") == null ? "0.00" : mapinfo.get("service_fee").toString());
				double current_period_money = Double
						.valueOf(mapinfo.get("period_money") == null ? "0.00" : mapinfo.get("period_money").toString());
				double related_charges = Double.valueOf(
						mapinfo.get("related_charges") == null ? "0.00" : mapinfo.get("related_charges").toString());
				double actual_pay_amount = settle_amount - related_charges - service_fee;
				String small_seller_code = StringUtils.isEmpty((String) mapinfo.get("small_seller_code")) ? ""
						: mapinfo.get("small_seller_code").toString();
				if (StringUtils.isNotEmpty(small_seller_code)) {
					// 查询结算单是否汇总,如果已经汇总了就删除重新汇总
					MDataMap finance_amount = DbUp.upTable("oc_bill_finance_amount").one("settle_code", getSettleCode());
					if(finance_amount != null) {						
						// 如果已经汇总了就删除重新汇总
						DbUp.upTable("oc_bill_finance_amount").dataExec("delete from ordercenter.oc_bill_finance_amount where settle_code='"+getSettleCode()+"'", new MDataMap());
					}
					DbUp.upTable("oc_bill_finance_amount").insert("settle_code", getSettleCode(), "settle_type",
							settleType, "account_type", "4497477900030003", "settle_amount", String.valueOf(settle_amount),
							"current_period_money", String.valueOf(current_period_money), "related_charges",
							String.valueOf(related_charges), "settle_pay_moeny", String.valueOf(actual_pay_amount),
							"service_fee",String.valueOf(service_fee),
							"settle_period", saleStart.substring(0, 10) + "至" + saleEnd.substring(0, 10),
							"settle_status", "1", "start_time", saleStart, "end_time", saleEnd, "tuistart", returnStart,
							"tuiend", returnEnd, "small_seller_code", small_seller_code);
				
				}
			}
		}
	}

	/**
	 * 常规商户发票
	 */
	public void genFinanlBillTicket1() {
		//546同步结算单发票
		String sql = "select a.* from oc_bill_merchant_new a where a.settle_code not in (select b.account_amount from oc_documents_info b) ";
		List<Map<String, Object>> resultList = DbUp.upTable("oc_bill_merchant_new").dataSqlList(sql, null);
		
		for (Map<String, Object> map : resultList) {
			//去除缤纷商户类型
			Map<String, Object> resultMap = DbUp.upTable("v_base_uc_sellerinfo").dataSqlOne("select * from v_base_uc_sellerinfo where uc_seller_type='4497478100050001' and small_seller_code=:small_seller_code", new MDataMap("small_seller_code",map.get("merchant_code").toString()));
			Map<String, Object> newResultMap = DbUp.upTable("uc_seller_invoice_info").dataSqlOne("select * from uc_seller_invoice_info where small_seller_code=:small_seller_code",new MDataMap("small_seller_code",map.get("merchant_code").toString()));
			if(resultMap!=null) {
				MDataMap paramMap = new MDataMap();
				paramMap.put("uid",map.get("uid").toString());
				paramMap.put("document_code", WebHelper.upCode("FJKF"));
				paramMap.put("account_amount", map.get("settle_code").toString());
				paramMap.put("small_seller_code", map.get("merchant_code").toString());
				paramMap.put("small_seller_name", map.get("merchant_name").toString());
				paramMap.put("uc_seller_type", resultMap.get("uc_seller_type").toString());
				paramMap.put("account_clear_type", resultMap.get("account_clear_type").toString());
				paramMap.put("taxpayer_certificate_select", newResultMap==null?"":newResultMap.get("taxpayer_certificate_select").toString());
				paramMap.put("add_fee", map.get("add_deduction").toString());
				//是否开具:开具1 放弃0
				paramMap.put("is_issue","1");
				//发票性质:专票：zp  普票：pp
				//paramMap.put("document_nature", "zp");
				//发票类型:电子发票：dz 纸质发票：zz
				paramMap.put("document_type", "dz");
				//提交状态:未提交0 已提交1
				paramMap.put("submit_flag", "0");
				//发票状态:未开0  已开1
				paramMap.put("document_state", "0");
				//开票时间
				paramMap.put("bill_time", "");
				//运单号
				paramMap.put("waybill_num","");
				//金额类型 0:服务费金额 1:附加费金额,
				paramMap.put("fee_type", "1");
				//提交流程状态:商管待提交：44975003001 财务待提交：44975003002 商管待维护：44975003003 提交成功：44975003004
				paramMap.put("submit_flow", "44975003001");
				paramMap.put("update_time", DateUtil.getSysDateTimeString());
				DbUp.upTable("oc_documents_info").dataInsert(paramMap);
			}
		}
	}
	
	/**
	 * 平台入驻发票
	 */
	public void genFinanlBillTicket4() {
		//546同步结算单发票
		String sql = "select a.* from oc_bill_merchant_new_spec a where a.settle_code not in (select b.account_amount from oc_documents_info b) and settle_type='4497477900040004' ";
		List<Map<String, Object>> resultList = DbUp.upTable("oc_bill_merchant_new_spec").dataSqlList(sql, null);
		
		for (Map<String, Object> map : resultList) {
			Map<String, Object> resultMap = DbUp.upTable("v_base_uc_sellerinfo").dataSqlOne("select * from v_base_uc_sellerinfo where small_seller_code=:small_seller_code", new MDataMap("small_seller_code",map.get("merchant_code").toString()));
			Map<String, Object> newResultMap = DbUp.upTable("uc_seller_invoice_info").dataSqlOne("select * from uc_seller_invoice_info where small_seller_code=:small_seller_code",new MDataMap("small_seller_code",map.get("merchant_code").toString()));
			MDataMap paramMap = new MDataMap();
			paramMap.put("uid",map.get("uid").toString());
			paramMap.put("document_code", WebHelper.upCode("FWF"));
			paramMap.put("account_amount", map.get("settle_code").toString());
			paramMap.put("small_seller_code", map.get("merchant_code").toString());
			paramMap.put("small_seller_name", map.get("merchant_name").toString());
			paramMap.put("uc_seller_type", resultMap.get("uc_seller_type").toString());
			paramMap.put("account_clear_type", resultMap.get("account_clear_type").toString());
			paramMap.put("taxpayer_certificate_select", newResultMap==null?"":newResultMap.get("taxpayer_certificate_select").toString());
			paramMap.put("add_fee", "0.00");
			paramMap.put("service_fee", map.get("service_fee").toString());
			//是否开具:开具1 放弃0
			paramMap.put("is_issue","1");
			//发票性质:专票：zp  普票：pp
			paramMap.put("document_nature", "zp");
			//发票类型:电子发票：dz 纸质发票：zz
			paramMap.put("document_type", "dz");
			//提交状态:未提交0 已提交1
			paramMap.put("submit_flag", "0");
			//发票状态:未开0  已开1
			paramMap.put("document_state", "0");
			//开票时间
			paramMap.put("bill_time", "");
			//运单号
			paramMap.put("waybill_num","");
			//金额类型 0:服务费金额 1:附加费金额,
			paramMap.put("fee_type", "0");
			//提交流程状态:商管待提交：44975003001 财务待提交：44975003002 商管待维护：44975003003 提交成功：44975003004
			paramMap.put("submit_flow", "44975003001");
			paramMap.put("update_time", DateUtil.getSysDateTimeString());
			
			//同步服务费
			 DbUp.upTable("oc_documents_info").dataInsert(paramMap);
			 
			//同步附加费
			 paramMap.put("uid", UUID.randomUUID().toString().replace("-", ""));
			 paramMap.put("document_code", WebHelper.upCode("FJKF"));
			 paramMap.put("add_fee", map.get("add_deduction").toString());
			 paramMap.put("service_fee", "0.00");
			 paramMap.put("fee_type", "1");
			 DbUp.upTable("oc_documents_info").dataInsert(paramMap);
			 
		}
	}
	
	/**
	 * 结算单编号.年月日+固定字母(平台PT/常规CG)+自定义结算天数
	 * @param day 自定义结算天数(01~15)
	 * @return
	 */
	protected String getSettleCode() {
		return DateHelper.upDate(new Date(), "yyyyMMdd") + billChar + bill_day;
	}
	
	/**
	 * 
	 * 方法: isTestSeller <br>
	 * 描述: 判断当前商户是否为测试商户 <br>
	 * @param small_seller_code
	 * @return
	 */
	protected boolean isTestSeller(String small_seller_code) {
		boolean flag = false;
		String sellers = TopUp.upConfig("ordercenter.test_seller");
		if (StringUtils.isNotBlank(sellers)) {
			List<String> testSellerList = Arrays.asList(sellers.split(","));
			if (testSellerList != null && testSellerList.size() > 0) {
				if (testSellerList.contains(small_seller_code)) {
					return true;
				}
			}
		}
		return flag;
	}
	
	/**
	 * 
	 * 方法: getPayType <br>
	 * 描述: 获取结算的支付类型 <br>
	 * @return
	 */
	public String getPayType() {
		StringBuffer sb = new StringBuffer();
		List<Map<String, Object>> array = DbUp.upTable("oc_import_define").dataSqlList(
				"select pay_type from ordercenter.oc_import_define where flag_able='449746250001'", new MDataMap());
		if (array != null && array.size() > 0) {
			for (Map<String, Object> map : array) {
				sb.append("'").append(map.get("pay_type").toString()).append("'").append(",");
			}
		}
		// 百度外卖导入订单
		sb.append("'449716200005'").append(",");
		// 电视宝商城
		sb.append("'449716200006'").append(",");
		// 民生商城
		sb.append("'4497162000070001'").append(",");
		// 第三方代收
		sb.append("'449716200010'").append(",");
		// 在线支付
		sb.append("'449716200001'");
		return sb.toString();
	}
	
	/**
	 * 排除已经结算过的订单
	 * @param map
	 * @return
	 */
	protected MDataMap dealBillOrder(MDataMap map) {
		if (map != null && !map.isEmpty()) {
			Iterator<String> iterator = map.keySet().iterator();
			while (iterator.hasNext()) {
				String orderCode = iterator.next().toString();
				if (orderCode != null && !"".equals(orderCode.toString().trim())) {
					String settleCode = getSettleCode();
					// 查询该订单是否结算过
					MDataMap bill_order = DbUp.upTable("oc_bill_order").one("order_code",orderCode);
					if(bill_order != null) {
						// 如果结算过,看结算单号是否相同:如果相同,可能是第一次结算失败,允许重新结算;如果不相同,说明是重复结算,排除该订单
						String settle_code = MapUtils.getString(bill_order, "settle_code");
						if(!settle_code.equals(settleCode)) {
							map.remove(orderCode);
						}
					}else {
						// 没结算过,插入表中
						MDataMap insertMap = new MDataMap();
						insertMap.put("settle_code", settleCode);
						insertMap.put("order_code", orderCode);
						insertMap.put("order_deal_time", map.get(orderCode));
						insertMap.put("create_time", DateFormatUtil.getNowTime());
						DbUp.upTable("oc_bill_order").dataInsert(insertMap);
					}
				}
			}
		}
		return map;
	}
	
	/**
	 * 排除已经结算过的退货单
	 * @param map
	 * @return
	 */
	protected MDataMap dealBillReturn(MDataMap map) {
		if (map != null && !map.isEmpty()) {
			Iterator<String> iterator = map.keySet().iterator();
			while (iterator.hasNext()) {
				String returnCode = iterator.next().toString();
				if (returnCode != null && !"".equals(returnCode.toString().trim())) {
					String settleCode = getSettleCode();
					// 查询该退货单是否结算过
					MDataMap bill_return = DbUp.upTable("oc_bill_order_return").one("return_code",returnCode);
					if(bill_return != null) {
						// 如果结算过,看结算单号是否相同:如果相同,可能是第一次结算失败,允许重新结算;如果不相同,说明是重复结算,排除该退货单
						String settle_code = MapUtils.getString(bill_return, "settle_code");
						if(!settle_code.equals(settleCode)) {
							map.remove(returnCode);
						}
					}else {
						// 没结算过,插入表中
						MDataMap insertMap = new MDataMap();
						insertMap.put("settle_code", settleCode);
						insertMap.put("return_code", returnCode);
						insertMap.put("order_code", map.get(returnCode));
						insertMap.put("create_time", DateFormatUtil.getNowTime());
						DbUp.upTable("oc_bill_order_return").dataInsert(insertMap);
					}
				} 
			}
		}
		return map;
	}
	
	private void sendMail(){
		String receives[]= {"zhaojunling@jyh.com","luguixin@jyh.com"};
		String title= "自定义商户结算单生成异常！";
		String content= "自定义商户结算单生成异常:[结算单编号:"+getSettleCode()+"];[商户编号:"+smallSellerCode+"];";
		
		for (String receive : receives) {
			if(StringUtils.isNotBlank(receive)){
				MailSupport.INSTANCE.sendMail(receive, title, content);
			}
		}
	}
	
	private void sendWx(){
		//String receices[] = bConfig("groupcenter.offPro_sendWx_receives_"+sale_yn).split(",");
		//String content = TopUp.upConfig("groupcenter.offPro_sendWx_content_"+sale_yn);
		String receices[] = {"a767746946love"};
		for (String receive : receices) {
			if(StringUtils.isNotBlank(receive)){
				WarnCount count = new WarnCount();
				count.sendWx(receive , "自定义商户结算单生成异常:[结算单编号:"+getSettleCode()+"];[商户编号:"+smallSellerCode+"];");
			}
		}
		
	}
	
}
