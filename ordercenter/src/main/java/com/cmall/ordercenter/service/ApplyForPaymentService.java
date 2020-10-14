package com.cmall.ordercenter.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 商户下付款申请审核  service
 * @author houw
 *
 */
public class ApplyForPaymentService extends BaseClass{ 

	/**
	 * 常规商户结算单对应的付款申请单信息 
	 * @param pay_code  付款申请单编号
	 * @return
	 */
	public Map<String, Object> getPaymentInfo(String pay_code) {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		if(!StringUtils.isEmpty(pay_code) ) { 
			
			MDataMap mWhereMap = new MDataMap();
			MDataMap mWhereMap2 = new MDataMap();
			Map<String,Object> retentionList = new HashMap<String, Object>();
			mWhereMap.put("pay_code", pay_code);
			String sql = "select * from oc_bill_apply_payment oc where oc.pay_code =:pay_code ";
			Map<String,Object> payList = DbUp.upTable("oc_bill_apply_payment").dataSqlOne(sql, mWhereMap);
			if(payList != null) {
				mWhereMap2.put("small_seller_code", payList.get("merchant_code").toString());
				retentionList = DbUp.upTable("oc_bill_seller_retention_money").dataSqlOne("select * from oc_bill_seller_retention_money ob where ob.small_seller_code =:small_seller_code order by zid desc LIMIT 1", mWhereMap2);
				
			   resultMap.put("pay_code", pay_code);
			   resultMap.put("invoice_amount", payList.get("invoice_amount"));					//发票应开金额
			   resultMap.put("actual_invoice_amount", payList.get("actual_invoice_amount"));    //发票实开额
			   if(retentionList!=null) {
				   resultMap.put("max_retention_money", retentionList.get("max_retention_money"));			//质保金上限
				   resultMap.put("deduct_retention_money", retentionList.get("deduct_retention_money"));	//累计已扣质保金
			   } 	   
			   resultMap.put("period_money", payList.get("period_money"));			//本期质保金
			   resultMap.put("add_deduction", payList.get("add_deduction"));		//相关扣费
			   resultMap.put("actual_pay_amount", payList.get("actual_pay_amount"));//应付金额
			   resultMap.put("wait_pay_amount", payList.get("wait_pay_amount"));	//待付金额
			   resultMap.put("flag", payList.get("flag"));						    //状态
			   resultMap.put("reject_reason", isEmpty(payList.get("reject_reason")) ? "" : payList.get("reject_reason"));		//拒绝原因	
			}
	    }
			
		return resultMap;
	}
	
	/**
	 * 付款信息
	 * 
	 * @param pay_code
	 *            付款申请单编号
	 * @return
	 */
	public Map<String, Object> getPaymentInfoPlatform(String pay_code) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (!StringUtils.isEmpty(pay_code)) {
			MDataMap mWhereMap = new MDataMap();
			mWhereMap.put("pay_code", pay_code);
			String sql = "select * from oc_bill_apply_payment_pt oc where oc.pay_code =:pay_code ";
			Map<String, Object> payList = DbUp.upTable("oc_bill_apply_payment_pt").dataSqlOne(sql, mWhereMap);
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
	 * 发票信息 
	 * @param pay_code  付款申请单编号
	 * @return
	 */
	public List<Map<String, Object>> getInvoice(String pay_code) {
		List<Map<String, Object>> invoList = new ArrayList<Map<String, Object>>();
		if(!StringUtils.isEmpty(pay_code) ) {
			MDataMap map = new MDataMap();
			MDataMap mWhereMap = new MDataMap();
			mWhereMap.put("pay_code", pay_code);
			String sql = "select * from oc_bill_apply_payment oc where oc.pay_code =:pay_code ";
			Map<String,Object> payList = DbUp.upTable("oc_bill_apply_payment").dataSqlOne(sql, mWhereMap);
			if(payList != null) {
				String invoiceString = payList.get("invoice_codes").toString();
				String invoice = getArrayToString(invoiceString);
				String merchantCode = payList.get("merchant_code").toString();
				invoList = DbUp.upTable("oc_bill_invoice").dataSqlList("SELECT oi.invoice_code,oi.amount,oi.tax_amount,oi.tax_rate,oi.total_money,oi.create_time from oc_bill_invoice oi where oi.invoice_code in ("+invoice+")",new MDataMap());
				map.put("small_seller_code", merchantCode);
				
				String account = "", branchName = "";
				Map<String, Object> brankMap = DbUp.upTable("uc_seller_info_extend").dataSqlOne("select mc.small_seller_code,mc.branch_name,mc.bank_account from usercenter.uc_seller_info_extend mc where mc.small_seller_code =:small_seller_code",map);
				if(null != brankMap) {
					account = isEmpty(brankMap.get("bank_account")) ? "" : brankMap.get("bank_account").toString();
					branchName = isEmpty(brankMap.get("branch_name")) ? "" : brankMap.get("branch_name").toString();
				}
				
				//计算合计
				if(null != invoList && invoList.size() > 0) {
					double sumTotalMoney = 0.0;  //累计发票金额
					double sumAmount = 0.0;      //累计不含税金额
					double sumTaxAmount = 0.0;   //累计税额
					for(Map<String, Object> invo : invoList) {
						invo.put("account", account);
						invo.put("branchName", branchName);
						sumTotalMoney += isEmpty(invo.get("total_money")) ? 0.0 : Double.parseDouble(invo.get("total_money").toString());
						sumAmount += isEmpty(invo.get("amount")) ? 0.0 : Double.parseDouble(invo.get("amount").toString());
						sumTaxAmount += isEmpty(invo.get("tax_amount")) ? 0.0 : Double.parseDouble(invo.get("tax_amount").toString());
					}
					Map<String, Object> sumRec = new HashMap<String, Object>();
					sumRec.put("branchName", "合计");
					sumRec.put("total_money", sumTotalMoney);
					sumRec.put("amount", sumAmount);
					sumRec.put("tax_amount", sumTaxAmount);
					invoList.add(sumRec);
				}
			}

	    }
		return invoList;
	}
	
	
	/**
	 * 商品结算表信息 
	 * @param pay_code  结算单编号
	 * @return
	 */
	public List<Map<String, Object>> getSettle(String settle_codes,String merchant_code) {
		
//		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> productList = new ArrayList<Map<String,Object>>();
		
		if(!StringUtils.isEmpty(settle_codes) && !StringUtils.isEmpty(merchant_code)) { 
			
			String settleString = getArrayToString(settle_codes);
			productList = DbUp.upTable("oc_bill_final_export").dataSqlList("SELECT " +
					"mc.settle_code,oe.product_code,oe.product_name,oe.cost_price,oe.settle_num,oe.settle_amount," +
					"oe.period_retention_money,oe.sale_money,oe.postage,oe.manage_money,oe.others," +
					"(oe.settle_amount - oe.period_retention_money - oe.sale_money - oe.postage - oe.manage_money - oe.others) AS actual_pay_amount " +
					"FROM oc_bill_final_export oe,oc_bill_merchant_new mc " +
					"WHERE oe.start_time = mc.start_time AND oe.end_time = mc.end_time AND oe.small_seller_code = mc.merchant_code " +
					" AND oe.settle_code = mc.settle_code AND mc.settle_code IN ("+settleString+") AND mc.merchant_code = '"+merchant_code+"'",new MDataMap());
			if(null != productList && productList.size() > 0) {
				double sumSettleNum = 0.0, sumSettleAmount = 0.0;
				double sumPRM = 0.0, sumSaleMoney = 0.0;
				double sumPostage = 0.0, sumManageMoney = 0.0, sumOthers = 0.0;
				double sumActualPayAmount = 0.0;
				for(Map<String, Object> product : productList) {
					sumSettleNum += isEmpty(product.get("settle_num")) ? 0.0 : Double.parseDouble(product.get("settle_num").toString());
					sumSettleAmount += isEmpty(product.get("settle_amount")) ? 0.0 : Double.parseDouble(product.get("settle_amount").toString());
					sumPRM += isEmpty(product.get("period_retention_money")) ? 0.0 : Double.parseDouble(product.get("period_retention_money").toString());
					sumSaleMoney += isEmpty(product.get("sale_money")) ? 0.0 : Double.parseDouble(product.get("sale_money").toString());
					sumPostage += isEmpty(product.get("postage")) ? 0.0 : Double.parseDouble(product.get("postage").toString());
					sumManageMoney += isEmpty(product.get("manage_money")) ? 0.0 : Double.parseDouble(product.get("manage_money").toString());
					sumOthers += isEmpty(product.get("others")) ? 0.0 : Double.parseDouble(product.get("others").toString());	
					sumActualPayAmount += isEmpty(product.get("actual_pay_amount")) ? 0.0 : Double.parseDouble(product.get("actual_pay_amount").toString());	
				}
				Map<String, Object> sumRec = new HashMap<String, Object>();
				sumRec.put("settle_code", "合计");
				sumRec.put("settle_num", sumSettleNum);
				sumRec.put("settle_amount", sumSettleAmount);
				sumRec.put("period_retention_money", sumPRM);
				sumRec.put("sale_money", sumSaleMoney);
				sumRec.put("postage", sumPostage);
				sumRec.put("manage_money", sumManageMoney);
				sumRec.put("others", sumOthers);
				sumRec.put("actual_pay_amount", sumActualPayAmount);
				productList.add(sumRec);
			}
	    }

//		resultMap.put("productList", productList);
		return productList;
	}
	
	/**
	 * 日志信息
	 * @param pay_code  付款申请单编号
	 * @return
	 */
	public List<Map<String,Object>> getLogInfo(String pay_code) {
//		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String,Object>> applyLogList = new ArrayList<Map<String,Object>>();
		if(!StringUtils.isEmpty(pay_code) ) {
			MDataMap mWhereMap = new MDataMap();
			mWhereMap.put("pay_code", pay_code);
			String sql = "select * from lc_apply_for_payment afp where afp.pay_code =:pay_code order by zid desc";
			applyLogList = DbUp.upTable("lc_apply_for_payment").dataSqlList(sql, mWhereMap);
//			if(null != applyLogList && applyLogList.size() > 0) {
//				resultMap.put("logList", applyLogList);
//			}
	    }		
		return applyLogList;
	}
	
	
	/**将以逗号分隔的字符串转化为带引号以逗号分隔的字符串
	 *  example: 1,2,3 =>
	 *           '1','2','3'
	 * @param paras
	 * @return
	 */
	private String getArrayToString(String paras) {
		String paraString [] ;
		String para ="";
		paraString = paras.split(",");
		for(int i=0;i<paraString.length;i++){
			para = para +"'"+ paraString[i]+"',";
		}
		para = para.substring(0, para.length()-1);
		return para;
	}
	
	private boolean isEmpty(Object obj) {
		return null == obj || "".equals(obj.toString());
	}
}
