package com.cmall.ordercenter.service.api;

import com.cmall.ordercenter.service.api.ApiCheckSameInvoice.ApiCheckSameInvoiceInput;
import com.cmall.ordercenter.service.api.ApiCheckSameInvoice.ApiCheckSameInvoiceResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 商户结算3.0-判断发票号是否重复
 * 
 * @author zht
 * 
 */
public class ApiCheckSameInvoice extends
		RootApi<ApiCheckSameInvoiceResult, ApiCheckSameInvoiceInput> {

	public static class ApiCheckSameInvoiceInput extends RootInput {
		private String invoiceCode;

		public String getInvoiceCode() {
			return invoiceCode;
		}

		public void setInvoiceCode(String invoiceCode) {
			this.invoiceCode = invoiceCode;
		}
	}

	public static class ApiCheckSameInvoiceResult extends RootResult {
		private int samed;

		public int getSamed() {
			return samed;
		}

		public void setSamed(int samed) {
			this.samed = samed;
		}
	}

	@Override
	public ApiCheckSameInvoiceResult Process(
			ApiCheckSameInvoiceInput input, MDataMap requestMap) {
		// TODO Auto-generated method stub
		ApiCheckSameInvoiceResult result = new ApiCheckSameInvoiceResult();
		String invoiceCode = input.getInvoiceCode();
		int count = DbUp.upTable("oc_bill_invoice").dataCount("invoice_code='" + invoiceCode + "'", null);
		if(count > 0)
			result.setSamed(1);	//重复
		return result;
	}
}
