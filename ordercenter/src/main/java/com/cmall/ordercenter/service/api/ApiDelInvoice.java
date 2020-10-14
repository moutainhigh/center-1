package com.cmall.ordercenter.service.api;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.ordercenter.service.api.ApiDelInvoice.ApiDelInvoiceInput;
import com.cmall.ordercenter.service.api.ApiDelInvoice.ApiDelInvoiceResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 结算单修改删除发票时调用
 * @author zht
 *
 */
public class ApiDelInvoice extends RootApi<ApiDelInvoiceResult, ApiDelInvoiceInput> {
	
	public static class ApiDelInvoiceResult extends RootResult
	{
		private String result;

		public String getResult() {
			return result;
		}

		public void setResult(String result) {
			this.result = result;
		}
	}
	
	public static class ApiDelInvoiceInput extends RootInput
	{
		private String invoiceCode;
		private String payCode;
		
		public String getPayCode() {
			return payCode;
		}

		public void setPayCode(String payCode) {
			this.payCode = payCode;
		}

		public String getInvoiceCode() {
			return invoiceCode;
		}

		public void setInvoiceCode(String invoiceCode) {
			this.invoiceCode = invoiceCode;
		}
	}

	@Override
	public ApiDelInvoiceResult Process(ApiDelInvoiceInput input, MDataMap mRequestMap) {
		ApiDelInvoiceResult result = new ApiDelInvoiceResult();
		String invoiceCode = input.getInvoiceCode();
		String payCode = input.getPayCode();
		if(!StringUtils.isEmpty(invoiceCode)) {
			String sql = "delete from oc_bill_invoice where invoice_code='" + invoiceCode + "'";
			int recCount = DbUp.upTable("oc_bill_invoice").dataCount("invoice_code=:invoice_code", new MDataMap("invoice_code", invoiceCode));
			int delCount = DbUp.upTable("oc_bill_invoice").dataExec(sql,new MDataMap());
			if(recCount == delCount) {
				sql = "select invoice_codes from oc_bill_apply_payment where pay_code='" + payCode + "'";
				Map<String, Object> apply = DbUp.upTable("oc_bill_apply_payment").dataSqlOne(sql, new MDataMap());
				if(null != apply) {
					String invoiceCodes = apply.get("invoice_codes") == null ? "" : apply.get("invoice_codes").toString();
					if(StringUtils.isNotEmpty(invoiceCodes)) {
						String[] invoices = invoiceCodes.split(",");
						List<String> list = new ArrayList<String>();
						for(int i=0;i<invoices.length;i++){  
				            list.add(invoices[i]);  
				        }  
						list.remove(invoiceCode);
						StringBuilder sb = new StringBuilder();
						for(String invoice: list) {  
				            sb.append(invoice).append(",");  
				        }
						if(sb.length() >0) {
							invoiceCode = sb.toString().substring(0,  sb.length() -1);
						} else {
							invoiceCode = "";
						}
						DbUp.upTable("oc_bill_apply_payment").dataUpdate(new MDataMap("invoice_codes", invoiceCode, "pay_code", payCode), "invoice_codes", "pay_code");
					}
				}
			}
			else if(recCount > 0 && delCount == 0 ) {
				//只返回删除失败信息
				result.setResultCode(939303200);
				result.setResultMessage(bInfo(939303200, invoiceCode));				
			}
		}
		return result;
	}
}
