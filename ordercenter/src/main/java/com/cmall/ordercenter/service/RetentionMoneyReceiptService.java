package com.cmall.ordercenter.service;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;

/**
 * 质保金收据相关
 */
public class RetentionMoneyReceiptService extends BaseClass {

	/**
	 * 生成新的收据编号,例：NO.00000004
	 * 
	 */
	public String createNewReceiptNo(){
		String v = WebHelper.upCode("NO").substring(2);
		
		int len = v.length();
		// 不够8位则前补0
		for(int i=0;i<8-len;i++){
			v = "0" + v;
		}
		return "NO."+v;
	}
	
	/**
	 * 根据结算单编号生成质保金收据
	 * @param uid
	 */
	public void addReceiptFromBill(String uid){
		MDataMap bill = DbUp.upTable("oc_bill_finance_amount").one("uid",uid);
		
		// 0元不生成收据
		BigDecimal money = new BigDecimal(bill.get("current_period_money")).setScale(2, BigDecimal.ROUND_HALF_UP);
		if(money.compareTo(BigDecimal.ZERO) <= 0){
			return;
		}
		
		List<MDataMap> list = DbUp.upTable("oc_bill_seller_retention_money").queryByWhere("settle_code",bill.get("settle_code"));
		for(MDataMap billSellerMData : list){
			if(new BigDecimal(billSellerMData.get("period_retention_money")).compareTo(BigDecimal.ZERO) <= 0){
				continue;
			}
			
			if(DbUp.upTable("oc_seller_retention_money_receipt").count("settle_code", billSellerMData.get("settle_code"),"small_seller_code",billSellerMData.get("small_seller_code")) == 0){
				MDataMap dataMap = new MDataMap();
				dataMap.put("receipt_retention_money_code", createNewReceiptNo());
				dataMap.put("settle_code", billSellerMData.get("settle_code"));
				dataMap.put("small_seller_code", billSellerMData.get("small_seller_code"));
				dataMap.put("settle_type", bill.get("settle_type"));
				dataMap.put("cashdeposit_collect_type", "4497477900050002");  // 结算生成的质保金收据都是：账扣  4497477900050002
				dataMap.put("retention_money", billSellerMData.get("period_retention_money"));
				dataMap.put("payment_type", "449748100002");  // 付款状态,默认： 未付款  449748100002
				dataMap.put("payment_time", "");
				dataMap.put("receipt_retention_money_type", ""); // 质保金收据状态，默认：空
				dataMap.put("receipt_retention_money_time", "");
				DbUp.upTable("oc_seller_retention_money_receipt").dataInsert(dataMap);
			}
		}
	}
	
	/**
	 * 添加预收质保金生成收据
	 * @param uid
	 * @return
	 */
	public MDataMap addReceiptFromManager(String smallSellerCode, String receiveRetentionMoney){
		// 0元不生成收据
		BigDecimal money = new BigDecimal(receiveRetentionMoney).setScale(2, BigDecimal.ROUND_HALF_UP);
		if(money.compareTo(BigDecimal.ZERO) <= 0){
			return null;
		}
		
		MDataMap sellerInfoExt = DbUp.upTable("uc_seller_info_extend").one("small_seller_code",smallSellerCode);
		
		MDataMap dataMap = new MDataMap();
		dataMap = new MDataMap();
		dataMap.put("receipt_retention_money_code", createNewReceiptNo());
		dataMap.put("settle_code", "");
		dataMap.put("small_seller_code", smallSellerCode);
		dataMap.put("settle_type", settleType(sellerInfoExt.get("uc_seller_type")));
		dataMap.put("cashdeposit_collect_type", "4497477900050001");  // 预付  4497477900050002
		dataMap.put("retention_money", money.toString());
		dataMap.put("payment_type", "449748100001");  // 付款状态,默认： 未付款  449748100002
		dataMap.put("payment_time", FormatHelper.upDateTime("yyyy-MM-dd"));
		dataMap.put("receipt_retention_money_type", "449748110002"); // 质保金收据状态，默认：未开  449748110002
		dataMap.put("receipt_retention_money_time", "");
		DbUp.upTable("oc_seller_retention_money_receipt").dataInsert(dataMap);
		return dataMap;
	}
	
	/**
	 * 根据结算单更新收据的支付状态
	 * @param settleCodes  结算单编号，多个使用逗号分割
	 * @param smallSellerCode 商户编码
	 */
	public void updatePaymentStatusByBill(String settleCodes, String smallSellerCode, String payTime){
		String[] codes = settleCodes.split(",");
		for(String v : codes){
			MDataMap receipt = DbUp.upTable("oc_seller_retention_money_receipt").one("settle_code",v,"small_seller_code",smallSellerCode);
			if(receipt != null){
				receipt.put("payment_type", "449748100001");
				receipt.put("payment_time", StringUtils.trimToEmpty(payTime));
				receipt.put("receipt_retention_money_type", "449748110002");  // 把收据状态更新为：未开
				DbUp.upTable("oc_seller_retention_money_receipt").dataUpdate(receipt, "payment_type,payment_time,receipt_retention_money_type", "zid");
			}
		}
	}
	
	/**
	 * 根据商户类型获取对应结算类型
	 * @param uc_seller_type
	 * @return
	 */
	private static String settleType(String uc_seller_type) {
		String settleType = "";
		if (StringUtils.equals("4497478100050001", uc_seller_type)
				|| StringUtils.equals("4497478100050005", uc_seller_type)) {
			settleType = "4497477900040001";
		} else if (StringUtils.equals("4497478100050002", uc_seller_type)) {
			settleType = "4497477900040002";
		} else if (StringUtils.equals("4497478100050003", uc_seller_type)) {
			settleType = "4497477900040003";
		} else if (StringUtils.equals("4497478100050004", uc_seller_type)) {
			settleType = "4497477900040004";
		}
		return settleType;
	}
}
