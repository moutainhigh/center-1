package com.cmall.ordercenter.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 商户下付款申请审核 service
 * 
 * @author houw
 *
 */
public class ApplyForPaymentServiceKj extends BaseClass {

	/**
	 * 付款信息
	 * 
	 * @param pay_code
	 *            付款申请单编号
	 * @return
	 */
	public Map<String, Object> getPaymentInfo(String pay_code) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (!StringUtils.isEmpty(pay_code)) {
			MDataMap mWhereMap = new MDataMap();
			mWhereMap.put("pay_code", pay_code);
			String sql = "select * from oc_bill_apply_payment_kj oc where oc.pay_code =:pay_code ";
			Map<String, Object> payList = DbUp.upTable("oc_bill_apply_payment_kj").dataSqlOne(sql, mWhereMap);
			if (payList != null) {
				// 结算单编码
				resultMap.put("settle_codes", payList.get("settle_codes").toString());
				// 状态
				resultMap.put("flag", payList.get("flag").toString());
				// 商户编码
				resultMap.put("merchant_code", payList.get("merchant_code").toString());
				// 商户名称
				resultMap.put("merchant_name", payList.get("merchant_name").toString());
				// 开户行
				resultMap.put("branch_name",
						payList.get("branch_name") != null ? payList.get("branch_name").toString() : "");
				// 账号
				resultMap.put("bank_account",
						payList.get("bank_account") != null ? payList.get("bank_account").toString() : "");
				// 本期代收货款合计
				resultMap.put("period_collect_amount_total", payList.get("period_collect_amount_total") != null
						? payList.get("period_collect_amount_total").toString() : "");
				// 平台服务费
				resultMap.put("service_fee",
						payList.get("service_fee") != null ? payList.get("service_fee").toString() : "");
				// 应付代收货款
				resultMap.put("payable_collect_amount", payList.get("payable_collect_amount") != null
						? payList.get("payable_collect_amount").toString() : "");
				String smallSellerCode = payList.get("merchant_code").toString();
				Map<String, Object> seller = DbUp.upTable("uc_seller_info_extend").dataSqlOne(
						"select quality_retention_money,(select define_name from systemcenter.sc_define where define_code=money_collection_way) as money_collection_way from usercenter.uc_seller_info_extend where small_seller_code=:small_seller_code",
						new MDataMap("small_seller_code", smallSellerCode));
				// 保证金收取方式
				resultMap.put("money_collection_way", seller.get("money_collection_way") != null
						? seller.get("money_collection_way").toString() : "");
				// 最大保证金
				resultMap.put("quality_retention_money", seller.get("quality_retention_money") != null
						? seller.get("quality_retention_money").toString() : "");
				// 附加扣费合计
				resultMap.put("add_deduction",
						payList.get("add_deduction") != null ? payList.get("add_deduction").toString() : "");
				// 结算代收货款
				resultMap.put("settle_collect_amount", payList.get("settle_collect_amount") != null
						? payList.get("settle_collect_amount").toString() : "");
				// 本期质保金
				resultMap.put("period_money",
						payList.get("period_money") != null ? payList.get("period_money").toString() : "");
				// 实付代收货款
				resultMap.put("actual_pay_amount",
						payList.get("actual_pay_amount") != null ? payList.get("actual_pay_amount").toString() : "");

			}
		}
		return resultMap;
	}

	/**
	 * 商品结算表信息
	 * 
	 * @param pay_code
	 *            结算单编号
	 * @return
	 */
	public List<Map<String, String>> getSettle(String settle_codes, String merchant_code) {
		List<Map<String, String>> products = new ArrayList<Map<String, String>>();
		if (!StringUtils.isEmpty(settle_codes) && !StringUtils.isEmpty(merchant_code)) {
			String settleString = getArrayToString(settle_codes);
			String sql = "SELECT oe.settle_code,oe.product_code,oe.sku_code,oe.sku_name,oe.cost_price,oe.sell_price,oe.settle_num,";
			sql += " oe.settle_amount,oe.service_fee,(oe.settle_amount-oe.service_fee)as payable_collect_amount,oe.sale_money,oe.postage,oe.manage_money,oe.others,";
			sql += " (oe.settle_amount-oe.service_fee - oe.sale_money - oe.postage - oe.manage_money - oe.others) AS settle_collect_amount,oe.period_retention_money,";
			sql += " (oe.settle_amount-oe.service_fee - oe.sale_money - oe.postage - oe.manage_money - oe.others -oe.period_retention_money) as actual_pay_amount";
			sql += " FROM oc_bill_final_export oe WHERE oe.small_seller_code  = '" + merchant_code + "' AND oe.settle_code IN (" + settleString + ")";
			List<Map<String, Object>> productList = DbUp.upTable("oc_bill_final_export").dataSqlList(sql, new MDataMap());
			if (null != productList && productList.size() > 0) {
				for (Map<String, Object> product : productList) {
					Map<String, String> p = new HashMap<String, String>();
					//结算单编号
					p.put("settle_code", product.get("settle_code").toString());
					// 商品编码
					p.put("product_code", product.get("product_code").toString());
					// SKU编码
					p.put("sku_code", product.get("sku_code").toString());
					// 商品名称
					p.put("sku_name", product.get("sku_name").toString());
					// 代收单价
					Double cost_price = Double.valueOf(product.get("cost_price") != null ? product.get("cost_price").toString() : "0.00");
					p.put("cost_price", BigDecimal.valueOf(cost_price).setScale(2,BigDecimal.ROUND_HALF_UP).toString());
					// 应付代收单价
					Double sell_price = Double.valueOf(product.get("sell_price") != null ? product.get("sell_price").toString() : "0.00");
					p.put("sell_price", BigDecimal.valueOf(sell_price).setScale(2,BigDecimal.ROUND_HALF_UP).toString());
					// 本期代收数量
					p.put("settle_num", product.get("settle_num") != null ? product.get("settle_num").toString() : "0");
					// 本期代收货款合计
					Double settle_amount = Double.valueOf(product.get("settle_amount") != null ? product.get("settle_amount").toString() : "0.00");
					p.put("settle_amount", BigDecimal.valueOf(settle_amount).setScale(2,BigDecimal.ROUND_HALF_UP).toString());
					// 平台服务费
					Double service_fee = Double.valueOf(product.get("service_fee") != null ? product.get("service_fee").toString() : "0.00");
					p.put("service_fee", BigDecimal.valueOf(service_fee).setScale(2,BigDecimal.ROUND_HALF_UP).toString());
					// 应付代收货款
					Double payable_collect_amount = Double.valueOf(product.get("payable_collect_amount") != null ? product.get("payable_collect_amount").toString() : "0.00");
					p.put("payable_collect_amount", BigDecimal.valueOf(payable_collect_amount).setScale(2,BigDecimal.ROUND_HALF_UP).toString());
					// 促销费用
					Double sale_money = Double.valueOf(product.get("sale_money") != null ? product.get("sale_money").toString() : "0.00");
					p.put("sale_money", BigDecimal.valueOf(sale_money).setScale(2,BigDecimal.ROUND_HALF_UP).toString());
					// 邮费
					Double postage = Double.valueOf(product.get("postage") != null ? product.get("postage").toString() : "0.00");
					p.put("postage", BigDecimal.valueOf(postage).setScale(2,BigDecimal.ROUND_HALF_UP).toString());
					// 平台管理费
					Double manage_money = Double.valueOf(product.get("manage_money") != null ? product.get("manage_money").toString() : "0.00");
					p.put("manage_money", BigDecimal.valueOf(manage_money).setScale(2,BigDecimal.ROUND_HALF_UP).toString());
					// 其他
					Double others = Double.valueOf(product.get("others") != null ? product.get("others").toString() : "0.00");
					p.put("others", BigDecimal.valueOf(others).setScale(2,BigDecimal.ROUND_HALF_UP).toString());
					// 结算代收货款
					Double settle_collect_amount = Double.valueOf(product.get("settle_collect_amount") != null ? product.get("settle_collect_amount").toString() : "0.00");
					p.put("settle_collect_amount", BigDecimal.valueOf(settle_collect_amount).setScale(2,BigDecimal.ROUND_HALF_UP).toString());
					// 本期质保金
					Double period_retention_money = Double.valueOf(product.get("period_retention_money") != null ? product.get("period_retention_money").toString() : "0.00");
					p.put("period_retention_money", BigDecimal.valueOf(period_retention_money).setScale(2,BigDecimal.ROUND_HALF_UP).toString());
					// 实付代收货款
					Double actual_pay_amount = Double.valueOf(product.get("actual_pay_amount") != null ? product.get("actual_pay_amount").toString() : "0.00");
					p.put("actual_pay_amount", BigDecimal.valueOf(actual_pay_amount).setScale(2,BigDecimal.ROUND_HALF_UP).toString());
					products.add(p);
				}
			}
		}
		return products;
	}

	/**
	 * 日志信息
	 * 
	 * @param pay_code
	 *            付款申请单编号
	 * @return
	 */
	public List<Map<String, Object>> getLogInfo(String pay_code) {
		List<Map<String, Object>> applyLogList = new ArrayList<Map<String, Object>>();
		if (!StringUtils.isEmpty(pay_code)) {
			MDataMap mWhereMap = new MDataMap();
			mWhereMap.put("pay_code", pay_code);
			String sql = "select * from lc_apply_for_payment afp where afp.pay_code =:pay_code order by zid desc";
			applyLogList = DbUp.upTable("lc_apply_for_payment").dataSqlList(sql, mWhereMap);
		}
		return applyLogList;
	}

	/**
	 * 将以逗号分隔的字符串转化为带引号以逗号分隔的字符串 example: 1,2,3 => '1','2','3'
	 * 
	 * @param paras
	 * @return
	 */
	private String getArrayToString(String paras) {
		String paraString[];
		String para = "";
		paraString = paras.split(",");
		for (int i = 0; i < paraString.length; i++) {
			para = para + "'" + paraString[i] + "',";
		}
		para = para.substring(0, para.length() - 1);
		return para;
	}
}
