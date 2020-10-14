package com.cmall.ordercenter.service.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cmall.ordercenter.service.api.ApiGetSellerSettleAccount.ApiGetSellerSettleAccountInput;
import com.cmall.ordercenter.service.api.ApiGetSellerSettleAccount.ApiGetSellerSettleAccountResult;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 商户结算3.0
 * 创建商户结算付款申请单时根据选择的结算单号反查结算单属性
 * @author zht
 *
 */
public class ApiGetSellerSettleAccount extends RootApi<ApiGetSellerSettleAccountResult, ApiGetSellerSettleAccountInput> {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public ApiGetSellerSettleAccountResult Process(ApiGetSellerSettleAccountInput input, MDataMap requestMap) {
		// TODO Auto-generated method stub
		ApiGetSellerSettleAccountResult result = new ApiGetSellerSettleAccountResult();
		String settleCodes = input.getSettleCodes();
		if(StringUtils.isEmpty(settleCodes))
			return result;
		String smallSellerCode = input.getSmallSellerCode();
		if(StringUtils.isEmpty(smallSellerCode))
			return result;
		
		String sWhere = "settle_code in ('"+settleCodes.replace(",", "','")+"') and merchant_code='" + smallSellerCode + "'";
		String sFields = "uid,settle_code,merchant_code,merchant_name,invoice_amount,period_money,add_deduction,actual_pay_amount,start_time,end_time";
		List<MDataMap> map=DbUp.upTable("oc_bill_merchant_new").queryAll(sFields, "settle_code", sWhere, null);
		for (MDataMap mDataMap : map) {
			OcBillMerchantNew omn = new OcBillMerchantNew();
			String settleCode = mDataMap.get("settle_code");
			omn.setSettleCode(settleCode);
			String sellerCode = mDataMap.get("merchant_code");
			omn.setMerchantCode(sellerCode);
			omn.setMerchantName(mDataMap.get("merchant_name"));
			omn.setInvoiceAmount(mDataMap.get("invoice_amount"));
			omn.setPeriodMoney(mDataMap.get("period_money"));
			omn.setAddDeduction(mDataMap.get("add_deduction"));
			omn.setActualPayAmount(mDataMap.get("actual_pay_amount"));
			try {
				String startTime = isEmpty(mDataMap.get("start_time")) ? "" : sdf.format(sdf.parse(mDataMap.get("start_time")));
				omn.setStartTime(startTime);
			} catch(Exception e) {
				e.printStackTrace();
			}
			try {
				String end_time = isEmpty(mDataMap.get("end_time")) ? "" : sdf.format(sdf.parse(mDataMap.get("end_time")));
				omn.setEndTime(end_time);
			} catch(Exception e) {
				e.printStackTrace();
			}
			//累加每个结算单的进项税金小计
			List<Map<String, Object>> taxList = DbUp.upTable("oc_bill_final_export").dataSqlList("SELECT sum(oe.input_tax_subtotal) as tax " +
					"FROM oc_bill_final_export oe,oc_bill_merchant_new mc " +
					"WHERE oe.start_time = mc.start_time AND oe.end_time = mc.end_time AND oe.small_seller_code = mc.merchant_code " +
					"AND mc.settle_code = '" + settleCode + "' AND mc.merchant_code = '" + sellerCode + "'",new MDataMap());
			if(null != taxList && taxList.size() > 0) {
				Map<String, Object> tax = taxList.get(0);
				omn.setTaxAmount(isEmpty(tax.get("tax")) ? "0.0" : tax.get("tax").toString());
			}
			result.getSettleAccountList().add(omn);
		}
		return result;
	} 
	
	private boolean isEmpty(Object obj) {
		return obj == null || obj.toString().equals("");
	}

	public static class ApiGetSellerSettleAccountInput extends RootInput
	{
		public ApiGetSellerSettleAccountInput() {}
		
		@ZapcomApi(value = "结算单号", require=1)
		private String settleCodes;
		
		@ZapcomApi(value = "商户编号", require=1)
		private String smallSellerCode;

		public String getSettleCodes() {
			return settleCodes;
		}

		public void setSettleCodes(String settleCodes) {
			this.settleCodes = settleCodes;
		}

		public String getSmallSellerCode() {
			return smallSellerCode;
		}

		public void setSmallSellerCode(String smallSellerCode) {
			this.smallSellerCode = smallSellerCode;
		}
	}
	
	public static class OcBillMerchantNew
	{
		public OcBillMerchantNew() {}
		
		private String settleCode;
		private String merchantCode;
		private String merchantName;
		private String invoiceAmount;
		private String periodMoney;
		private String addDeduction;
		private String actualPayAmount;
		private String startTime;
		private String endTime;
		private String taxAmount;
		
		public String getSettleCode() {
			return settleCode;
		}
		public void setSettleCode(String settleCode) {
			this.settleCode = settleCode;
		}
		public String getMerchantCode() {
			return merchantCode;
		}
		public void setMerchantCode(String merchantCode) {
			this.merchantCode = merchantCode;
		}
		public String getMerchantName() {
			return merchantName;
		}
		public void setMerchantName(String merchantName) {
			this.merchantName = merchantName;
		}
		public String getInvoiceAmount() {
			return invoiceAmount;
		}
		public void setInvoiceAmount(String invoiceAmount) {
			this.invoiceAmount = invoiceAmount;
		}
		public String getPeriodMoney() {
			return periodMoney;
		}
		public void setPeriodMoney(String periodMoney) {
			this.periodMoney = periodMoney;
		}
		public String getAddDeduction() {
			return addDeduction;
		}
		public void setAddDeduction(String addDeduction) {
			this.addDeduction = addDeduction;
		}
		public String getActualPayAmount() {
			return actualPayAmount;
		}
		public void setActualPayAmount(String actualPayAmount) {
			this.actualPayAmount = actualPayAmount;
		}
		public String getStartTime() {
			return startTime;
		}
		public void setStartTime(String startTime) {
			this.startTime = startTime;
		}
		public String getEndTime() {
			return endTime;
		}
		public void setEndTime(String endTime) {
			this.endTime = endTime;
		}
		public String getTaxAmount() {
			return taxAmount;
		}
		public void setTaxAmount(String taxAmount) {
			this.taxAmount = taxAmount;
		}
	}
	
	public static class ApiGetSellerSettleAccountResult extends RootResult
	{
		public ApiGetSellerSettleAccountResult() {}
		
		@ZapcomApi(value = "商户结算单列表",require=1)
		private List<OcBillMerchantNew> settleAccountList = new ArrayList<OcBillMerchantNew>();

		public List<OcBillMerchantNew> getSettleAccountList() {
			return settleAccountList;
		}

		public void setSettleAccountList(List<OcBillMerchantNew> settleAccountList) {
			this.settleAccountList = settleAccountList;
		}
	} 
}
