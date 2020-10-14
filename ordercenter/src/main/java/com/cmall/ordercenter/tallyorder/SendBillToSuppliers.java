package com.cmall.ordercenter.tallyorder;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.tallyorder.settle.strategy.PlatformSettleStrategy;
import com.cmall.ordercenter.tallyorder.settle.strategy.StdSettleStrategy;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;

/**
 * settle_type取值: 4497477900040001 常规结算 4497477900040002 跨境保税 4497477900040003
 * 跨境直邮 4497477900040004 平台入驻
 * 
 * account_type取值: 4497477900030001 月结 4497477900030002 半月结
 * 
 */
public class SendBillToSuppliers {

	/**
	 * 下发商户账单
	 * 
	 * @param start_time
	 * @param end_time
	 */
	public static void sendToMerchant(String settle_code, String settle_type, String account_type, String start_time,
			String end_time, String tuiStartTime, String tuiEndTime) {
		switch (settle_type) {
		case "4497477900040001":
			processStdBill(settle_code, settle_type, account_type, start_time, end_time, tuiStartTime, tuiEndTime);
			break;
		case "4497477900040002":
			processSpecSellerSettleBill(settle_code, settle_type, account_type, start_time, end_time, tuiStartTime,
					tuiEndTime);
			break;
		case "4497477900040003":
			processSpecSellerSettleBill(settle_code, settle_type, account_type, start_time, end_time, tuiStartTime,
					tuiEndTime);
			break;
		case "4497477900040004":
			processSpecSellerSettleBill(settle_code, settle_type, account_type, start_time, end_time, tuiStartTime,
					tuiEndTime);
			break;
		}
	}

	/**
	 * 常规结算单发布到商户
	 * 
	 * @param settle_code
	 * @param settle_type
	 * @param start_time
	 * @param end_time
	 * @param tuiStart
	 * @param tuiEnd
	 */
	public static void processStdBill(String settle_code, String settle_type, String account_type, String start_time,
			String end_time, String tuiStart, String tuiEnd) {
		MDataMap map = new MDataMap();
		// map.put("start_time", start_time);
		// map.put("end_time", end_time);
		map.put("settle_code", settle_code);

		List<Map<String, Object>> list = DbUp.upTable("oc_bill_final_export")
				.dataSqlList("select small_seller_code,small_seller_name,"
						+ "sum(settle_amount) as invoice_amount,sum(period_retention_money) as period_money,sum(add_amount) as add_deduction,"
						+ "sum(settle_amount-period_retention_money-add_amount) as actual_pay_amount from oc_bill_final_export "
						+ "where settle_code=:settle_code group by small_seller_code", map);
		// + "where start_time=:start_time and end_time=:end_time group by
		// small_seller_code",map);
		if (list.size() > 0) {
			for (Map<String, Object> mapinfo : list) {
				String merchant_code = mapinfo.get("small_seller_code").toString();
				String merchant_name = mapinfo.get("small_seller_name").toString();
				String invoice_amount = mapinfo.get("invoice_amount").toString();
				String period_money = mapinfo.get("period_money").toString();
				String add_deduction = mapinfo.get("add_deduction").toString();
				String actual_pay_amount = mapinfo.get("actual_pay_amount").toString();
				DbUp.upTable("oc_bill_merchant_new").insert("settle_code", settle_code, "settle_type", settle_type,
						"account_type", account_type, "merchant_code", merchant_code, "merchant_name", merchant_name,
						"invoice_amount", invoice_amount, "period_money", period_money, "add_deduction", add_deduction,
						"flag", "4497476900040008", "actual_pay_amount", actual_pay_amount, "start_time", start_time,
						"end_time", end_time, "tuistart", tuiStart, "tuiend", tuiEnd);

				/**
				 * 添加记录到商户结算质保金汇总表 2017-06-12 zhy
				 */
				MDataMap oc_bill_seller_retention_money = new MDataMap();
				oc_bill_seller_retention_money.put("small_seller_code", merchant_code);
				oc_bill_seller_retention_money.put("settle_code", settle_code);
				Map<String, Object> retention_money = DbUp.upTable("oc_bill_seller_retention_money").dataSqlOne(
						"select * from oc_bill_seller_retention_money where settle_code=:settle_code and small_seller_code=:small_seller_code and retention_money_type='4497480200010001'",
						oc_bill_seller_retention_money);
				if (retention_money != null) {
					// 获取调整质保金之和
					String adjust_retention_money_sql = "select SUM(period_retention_money) as period_retention_money from ordercenter.oc_bill_seller_retention_money where small_seller_code=:small_seller_code and retention_money_type='4497480200010002'";
					Map<String, Object> adjust_retention_money_map = DbUp.upTable("oc_bill_seller_retention_money")
							.dataSqlOne(adjust_retention_money_sql, new MDataMap("small_seller_code", merchant_code));
					Double old_adjust_retention_money = Double.valueOf("0.00");
					if (adjust_retention_money_map != null
							&& adjust_retention_money_map.get("period_retention_money") != null) {
						old_adjust_retention_money = Double
								.valueOf(adjust_retention_money_map.get("period_retention_money").toString());
					}
					// 往期本期质保金之和
					String old_period_retention_money_sql = "SELECT SUM(tmp.period_retention_money) as period_retention_money FROM (SELECT DISTINCT m.settle_code,m.period_retention_money FROM ordercenter.oc_bill_seller_retention_money AS m,ordercenter.oc_bill_finance_amount AS a";
					old_period_retention_money_sql += " WHERE m.settle_code = a.settle_code AND m.small_seller_code =:small_seller_code AND a.settle_status = '2' AND m.retention_money_type = '4497480200010001' AND m.settle_code !=:settle_code) tmp";
					Map<String, Object> old_period_retention_money_map = DbUp.upTable("oc_bill_seller_retention_money")
							.dataSqlOne(old_period_retention_money_sql,
									new MDataMap("small_seller_code", merchant_code, "settle_code", settle_code));
					// 已扣质保金=往期本期质保金之和+调整质保金之和
					Double deduct_retention_money = old_adjust_retention_money;
					if (old_period_retention_money_map != null
							&& old_period_retention_money_map.get("period_retention_money") != null) {
						deduct_retention_money = deduct_retention_money + Double
								.valueOf(old_period_retention_money_map.get("period_retention_money").toString());
					}
					MDataMap oc_retention_money_merchant = new MDataMap();
					oc_retention_money_merchant.put("small_seller_code", merchant_code);
					oc_retention_money_merchant.put("small_seller_name", merchant_name);
					oc_retention_money_merchant.put("settle_code", settle_code);
					oc_retention_money_merchant.put("settle_type", settle_type);
					oc_retention_money_merchant.put("account_type", account_type);
					oc_retention_money_merchant.put("max_retention_money",
							retention_money.get("max_retention_money").toString());
					oc_retention_money_merchant.put("deduct_retention_money", String.valueOf(deduct_retention_money));
					oc_retention_money_merchant.put("period_retention_money",
							retention_money.get("period_retention_money").toString());
					oc_retention_money_merchant.put("retention_money_type", "4497480200010001");
					DbUp.upTable("oc_retention_money_merchant").dataInsert(oc_retention_money_merchant);

					/**
					 * 根据商户编码获取所有结算类型的本期质保金之和
					 */
					String period_retention_money_sql = "SELECT SUM(tmp.period_retention_money) as period_retention_money FROM (SELECT DISTINCT m.settle_code,m.period_retention_money FROM ordercenter.oc_bill_seller_retention_money AS m,ordercenter.oc_bill_finance_amount AS a";
					period_retention_money_sql += " WHERE m.settle_code = a.settle_code AND m.small_seller_code =:small_seller_code AND a.settle_status = '2' AND m.retention_money_type = '4497480200010001') tmp";
					Map<String, Object> period_retention_money_map = DbUp.upTable("oc_bill_seller_retention_money")
							.dataSqlOne(period_retention_money_sql, new MDataMap("small_seller_code", merchant_code));
					if (period_retention_money_map != null
							&& period_retention_money_map.get("period_retention_money") != null) {

						double current_deduct_retention_money = Double
								.valueOf(period_retention_money_map.get("period_retention_money").toString());
						/**
						 * 修改质保金管理表
						 */
						String sql = "update ordercenter.oc_seller_retention_money set deduct_retention_money="
								+ current_deduct_retention_money
								+ " ,retention_money_sum=receive_retention_money+adjust_retention_money+"
								+ current_deduct_retention_money + " where small_seller_code='" + merchant_code + "'";
						DbUp.upTable("oc_seller_retention_money").dataExec(sql, new MDataMap());
					}
				}
			}
		}
		stdProductDetailInfo(settle_code, start_time, end_time, tuiStart, tuiEnd);
		//同步商户结算单到发票库
		sysnBillTicket();;
	}
	

	public static void sysnBillTicket() {
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
	 * 常规结算商户商品明细
	 * 
	 * @param start_time
	 * @param end_time
	 */
	public static void stdProductDetailInfo(String settle_code, String start_time, String end_time, String tuiStart,
			String tuiEnd) {
		MDataMap map = new MDataMap();
		// map.put("start_time", start_time);
		// map.put("end_time", end_time);
		map.put("settle_code", settle_code);
		List<Map<String, Object>> list = DbUp.upTable("oc_bill_final_export")
				.dataSqlList("select small_seller_code,small_seller_name,"
						+ "product_code,product_name,sku_code, sku_name, cost_price,"
						+ "settle_num ,settle_amount,period_retention_money,"
						+ "sale_money ,postage ,manage_money ,others, input_tax_subtotal "
						+ " from oc_bill_final_export where settle_code=:settle_code ", map);
		// + " from oc_bill_final_export where start_time=:start_time and
		// end_time=:end_time ", map);
		if (list.size() > 0) {
			for (Map<String, Object> mapinfo : list) {
				String small_seller_code = mapinfo.get("small_seller_code").toString();
				String small_seller_name = mapinfo.get("small_seller_name").toString();
				String product_code = mapinfo.get("product_code").toString();
				String product_name = mapinfo.get("product_name").toString();
				String sku_code = mapinfo.get("sku_code").toString();
				String sku_name = mapinfo.get("sku_name").toString();
				double cost_price = Double.valueOf(mapinfo.get("cost_price").toString());
				double settle_count = Double.valueOf(mapinfo.get("settle_num").toString());
				double invoice_amount = cost_price * settle_count;
				double period_money = Double.valueOf(mapinfo.get("period_retention_money") == null ? "0.00"
						: mapinfo.get("period_retention_money").toString());
				double sale_money = Double
						.valueOf(mapinfo.get("sale_money") == null ? "0.00" : mapinfo.get("sale_money").toString());
				double postage = Double
						.valueOf(mapinfo.get("postage") == null ? "0.00" : mapinfo.get("postage").toString());
				double manage_money = Double
						.valueOf(mapinfo.get("manage_money") == null ? "0.00" : mapinfo.get("manage_money").toString());
				double others = Double
						.valueOf(mapinfo.get("others") == null ? "0.00" : mapinfo.get("others").toString());
				double input_tax_subtotal = Double.valueOf(mapinfo.get("input_tax_subtotal") == null ? "0.00"
						: mapinfo.get("input_tax_subtotal").toString());

				// String actual_pay_amount =
				// mapinfo.get("actual_pay_amount").toString();
				double actual_pay_amount = invoice_amount - period_money - sale_money - postage - manage_money - others;
				DbUp.upTable("oc_bill_product_detail_new").insert("settle_code", settle_code, "small_seller_code",
						small_seller_code, "seller_name", small_seller_name, "product_code", product_code,
						"product_name", product_name, "sku_code", sku_code, "sku_name", sku_name, "cost_price",
						String.valueOf(cost_price), "settle_count", String.valueOf(settle_count), "invoice_amount",
						String.valueOf(invoice_amount), "period_money", String.valueOf(period_money), "sale_money",
						String.valueOf(sale_money), "postage", String.valueOf(postage), "manage_money",
						String.valueOf(manage_money), "others", String.valueOf(others), "actual_pay_amount",
						String.valueOf(actual_pay_amount), "tax_amount", String.valueOf(input_tax_subtotal),
						"start_time", start_time, "end_time", end_time, "tuistart", tuiStart, "tuiend", tuiEnd);
			}
		}
	}

	/**
	 * 处理跨境保税,跨境直邮,平台入驻三种商户的结算帐期内汇总结算单的发布. 如以后不同商户有不同发布逻辑时可自行重写. add by
	 * zht.2017-06-06
	 * 
	 * @param settle_code
	 * @param settle_type
	 * @param account_type
	 * @param start_time
	 * @param end_time
	 * @param tuiStart
	 * @param tuiEnd
	 */
	private static void processSpecSellerSettleBill(String settle_code, String settle_type, String account_type,
			String start_time, String end_time, String tuiStart, String tuiEnd) {
		MDataMap map = new MDataMap();
		map.put("settle_code", settle_code);

		List<Map<String, Object>> list = DbUp.upTable("oc_bill_final_export")
				.dataSqlList("select small_seller_code,small_seller_name,"
						+ "sum(settle_amount) as settle_amount, sum(service_fee) as service_fee, sum(settle_amount-service_fee) as payable_collect_amount,"
						+ "sum(add_amount) as add_deduction, sum(settle_amount-service_fee-add_amount) as settle_collect_amount,"
						// 添加查询字段质保金 2016-11-25 zhy
						+ "sum(period_retention_money) as period_retention_money,"
						+ "sum(settle_amount-service_fee-add_amount-period_retention_money) as actual_pay_amount from oc_bill_final_export "
						+ "where settle_code=:settle_code group by small_seller_code", map);

		if (list.size() > 0) {
			for (Map<String, Object> mapinfo : list) {
				String merchant_code = mapinfo.get("small_seller_code").toString();
				String merchant_name = mapinfo.get("small_seller_name").toString();
				String settle_amount = mapinfo.get("settle_amount").toString();
				String service_fee = mapinfo.get("service_fee").toString();
				String payable_collect_amount = mapinfo.get("payable_collect_amount").toString();
				String add_deduction = mapinfo.get("add_deduction").toString();
				String settle_collect_amount = mapinfo.get("settle_collect_amount").toString();
				// 添加查询字段质保金 2016-11-25 zhy
				String period_retention_money = mapinfo.get("period_retention_money").toString();
				String actual_pay_amount = mapinfo.get("actual_pay_amount").toString();

				DbUp.upTable("oc_bill_merchant_new_spec").insert("settle_code", settle_code, "settle_type", settle_type,
						"account_type", account_type, "merchant_code", merchant_code, "merchant_name", merchant_name,
						"income_amount", settle_amount, "service_fee", service_fee, "payable_collect_amount",
						payable_collect_amount, "add_deduction", add_deduction, "settle_collect_amount",
						settle_collect_amount, "period_money", period_retention_money, "actual_pay_amount",
						actual_pay_amount, "flag", "4497476900040008", "start_time", start_time, "end_time", end_time,
						"tuistart", tuiStart, "tuiend", tuiEnd);

				/**
				 * 添加记录到商户结算质保金汇总表 2017-06-12 zhy
				 */
				MDataMap oc_bill_seller_retention_money = new MDataMap();
				oc_bill_seller_retention_money.put("small_seller_code", merchant_code);
				oc_bill_seller_retention_money.put("settle_code", settle_code);
				Map<String, Object> retention_money = DbUp.upTable("oc_bill_seller_retention_money").dataSqlOne(
						"select * from oc_bill_seller_retention_money where settle_code=:settle_code and small_seller_code=:small_seller_code and retention_money_type='4497480200010001'",
						oc_bill_seller_retention_money);

				if (retention_money != null) {
					// 获取调整质保金之和
					String adjust_retention_money_sql = "select SUM(period_retention_money) as period_retention_money from ordercenter.oc_bill_seller_retention_money where small_seller_code=:small_seller_code and retention_money_type='4497480200010002'";
					Map<String, Object> adjust_retention_money_map = DbUp.upTable("oc_bill_seller_retention_money")
							.dataSqlOne(adjust_retention_money_sql, new MDataMap("small_seller_code", merchant_code));
					Double old_adjust_retention_money = Double.valueOf("0.00");
					if (adjust_retention_money_map != null
							&& adjust_retention_money_map.get("period_retention_money") != null) {
						old_adjust_retention_money = Double
								.valueOf(adjust_retention_money_map.get("period_retention_money").toString());
					}
					// 往期本期质保金之和
					String old_period_retention_money_sql = "SELECT SUM(tmp.period_retention_money) as period_retention_money FROM (SELECT DISTINCT m.settle_code,m.period_retention_money FROM ordercenter.oc_bill_seller_retention_money AS m,ordercenter.oc_bill_finance_amount AS a";
					old_period_retention_money_sql += " WHERE m.settle_code = a.settle_code AND m.small_seller_code =:small_seller_code AND a.settle_status = '2' AND m.retention_money_type = '4497480200010001' AND m.settle_code !=:settle_code) tmp";
					Map<String, Object> old_period_retention_money_map = DbUp.upTable("oc_bill_seller_retention_money")
							.dataSqlOne(old_period_retention_money_sql,
									new MDataMap("small_seller_code", merchant_code, "settle_code", settle_code));
					// 已扣质保金=往期本期质保金之和+调整质保金之和
					Double deduct_retention_money = old_adjust_retention_money;
					if (old_period_retention_money_map != null
							&& old_period_retention_money_map.get("period_retention_money") != null) {
						deduct_retention_money = deduct_retention_money + Double
								.valueOf(old_period_retention_money_map.get("period_retention_money").toString());
					}

					MDataMap oc_retention_money_merchant = new MDataMap();
					oc_retention_money_merchant.put("small_seller_code", merchant_code);
					oc_retention_money_merchant.put("small_seller_name", merchant_name);
					oc_retention_money_merchant.put("settle_code", settle_code);
					oc_retention_money_merchant.put("settle_type", settle_type);
					oc_retention_money_merchant.put("account_type", account_type);
					oc_retention_money_merchant.put("max_retention_money",
							retention_money.get("max_retention_money").toString());
					oc_retention_money_merchant.put("deduct_retention_money", String.valueOf(deduct_retention_money));
					oc_retention_money_merchant.put("period_retention_money",
							retention_money.get("period_retention_money").toString());
					oc_retention_money_merchant.put("retention_money_type", "4497480200010001");
					DbUp.upTable("oc_retention_money_merchant").dataInsert(oc_retention_money_merchant);

					/**
					 * 根据商户编码获取所有结算类型的本期质保金之和
					 */
					String period_retention_money_sql = "SELECT SUM(tmp.period_retention_money) as period_retention_money FROM (SELECT DISTINCT m.settle_code,m.period_retention_money FROM ordercenter.oc_bill_seller_retention_money AS m,ordercenter.oc_bill_finance_amount AS a";
					period_retention_money_sql += " WHERE m.settle_code = a.settle_code AND m.small_seller_code =:small_seller_code AND a.settle_status = '2' AND m.retention_money_type = '4497480200010001') tmp";
					Map<String, Object> period_retention_money_map = DbUp.upTable("oc_bill_seller_retention_money")
							.dataSqlOne(period_retention_money_sql, new MDataMap("small_seller_code", merchant_code));
					if (period_retention_money_map != null
							&& period_retention_money_map.get("period_retention_money") != null) {
						double current_deduct_retention_money = Double
								.valueOf(period_retention_money_map.get("period_retention_money").toString());
						/**
						 * 修改质保金管理表
						 */
						String sql = "update ordercenter.oc_seller_retention_money set deduct_retention_money="
								+ current_deduct_retention_money
								+ " ,retention_money_sum=receive_retention_money+adjust_retention_money+"
								+ current_deduct_retention_money + " where small_seller_code='" + merchant_code + "'";
						DbUp.upTable("oc_seller_retention_money").dataExec(sql, new MDataMap());
					}
				}
			}
		}
		processSpecSellerSettleProductDetailInfo(settle_code, start_time, end_time, tuiStart, tuiEnd);
		//同步商户结算单到发票库
		sysnBillTicket2();
	}
	public static void sysnBillTicket2() {
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
	 * 处理跨境保税,跨境直邮,平台入驻三种商户的结算帐期内结算单的商口明细发布. 如以后不同商户有不同发布逻辑时可自行重写. add by
	 * zht.2017-06-06
	 * 
	 * @param settle_code
	 * @param start_time
	 * @param end_time
	 * @param tuiStart
	 * @param tuiEnd
	 */
	private static void processSpecSellerSettleProductDetailInfo(String settle_code, String start_time, String end_time,
			String tuiStart, String tuiEnd) {
		MDataMap map = new MDataMap();
		map.put("settle_code", settle_code);
		List<Map<String, Object>> list = DbUp.upTable("oc_bill_final_export")
				.dataSqlList("select small_seller_code,small_seller_name,"
						+ "product_code,product_name,sku_code, sku_name,cost_price, sell_price,"
						+ "settle_num ,settle_amount,service_fee,"
						+ "(settle_amount - service_fee) as payable_collect_amount,"
						+ "sale_money, postage, manage_money, others, add_amount,"
						+ "(settle_amount - service_fee - add_amount) as settle_collect_amount,"
						+ "period_retention_money, "
						+ "(settle_amount - service_fee - sale_money - postage - manage_money - others -period_retention_money) as actual_pay_amount "
						+ "from oc_bill_final_export where settle_code=:settle_code ", map);

		if (list.size() > 0) {
			for (Map<String, Object> mapinfo : list) {
				String small_seller_code = mapinfo.get("small_seller_code").toString();
				String small_seller_name = mapinfo.get("small_seller_name").toString();
				String product_code = mapinfo.get("product_code").toString();
				String product_name = mapinfo.get("product_name").toString();
				String sku_code = mapinfo.get("sku_code").toString();
				String sku_name = mapinfo.get("sku_name").toString();
				// 获取成本价 2016-11-28 zhy
				String cost_price = mapinfo.get("cost_price") == null ? "00:00" : mapinfo.get("cost_price").toString();
				double sell_price = Double
						.valueOf(mapinfo.get("sell_price") == null ? "0.00" : mapinfo.get("sell_price").toString());
				double settle_count = Double
						.valueOf(mapinfo.get("settle_num") == null ? "0.00" : mapinfo.get("settle_num").toString());
				double invoice_amount = Double.valueOf(
						mapinfo.get("settle_amount") == null ? "0.00" : mapinfo.get("settle_amount").toString());
				double service_fee = Double
						.valueOf(mapinfo.get("service_fee") == null ? "0.00" : mapinfo.get("service_fee").toString());
				// 应付代收货款
				double payable_collect_amount = Double.valueOf(mapinfo.get("payable_collect_amount") == null ? "0.00"
						: mapinfo.get("payable_collect_amount").toString());
				double sale_money = Double
						.valueOf(mapinfo.get("sale_money") == null ? "0.00" : mapinfo.get("sale_money").toString());
				double postage = Double
						.valueOf(mapinfo.get("postage") == null ? "0.00" : mapinfo.get("postage").toString());
				double manage_money = Double
						.valueOf(mapinfo.get("manage_money") == null ? "0.00" : mapinfo.get("manage_money").toString());
				double others = Double
						.valueOf(mapinfo.get("others") == null ? "0.00" : mapinfo.get("others").toString());
				double add_amount = Double
						.valueOf(mapinfo.get("add_amount") == null ? "0.00" : mapinfo.get("add_amount").toString());
				// 结算代收货款
				double settle_collect_amount = Double.valueOf(mapinfo.get("settle_collect_amount") == null ? "0.00"
						: mapinfo.get("settle_collect_amount").toString());
				String period_retention_money = mapinfo.get("period_retention_money") == null ? "0.00"
						: mapinfo.get("period_retention_money").toString();
				double actual_pay_amount = Double.valueOf(mapinfo.get("actual_pay_amount") == null ? "0.00"
						: mapinfo.get("actual_pay_amount").toString());

				DbUp.upTable("oc_bill_product_detail_new").insert("settle_code", settle_code, "small_seller_code",
						small_seller_code, "seller_name", small_seller_name, "cost_price", cost_price, "product_code",
						product_code, "product_name", product_name, "sku_code", sku_code, "sku_name", sku_name,
						"sell_price", String.valueOf(sell_price), "settle_count", String.valueOf(settle_count),
						"invoice_amount", String.valueOf(invoice_amount), "service_fee", String.valueOf(service_fee),
						"payable_collect_amount", String.valueOf(payable_collect_amount), "sale_money",
						String.valueOf(sale_money), "postage", String.valueOf(postage), "manage_money",
						String.valueOf(manage_money), "others", String.valueOf(others), "add_amount",
						String.valueOf(add_amount), "settle_collect_amount", String.valueOf(settle_collect_amount),
						"period_money", period_retention_money, "actual_pay_amount", String.valueOf(actual_pay_amount),
						"tax_amount", "0.0", "start_time", start_time, "end_time", end_time, "tuistart", tuiStart,
						"tuiend", tuiEnd);
			}
		}
	}
}
