package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 发票相关属性
 * @author wz
 *
 */
public class InvoiceInformationResult {
	@ZapcomApi(value="发票类型")
	private String invoiceInformationType = "";
	@ZapcomApi(value="抬头")
	private String invoiceInformationTitle = "";
	@ZapcomApi(value="发票内容")
	private String invoiceInformationValue = "";
	
	public String getInvoiceInformationType() {
		return invoiceInformationType;
	}
	public void setInvoiceInformationType(String invoiceInformationType) {
		this.invoiceInformationType = invoiceInformationType;
	}
	public String getInvoiceInformationTitle() {
		return invoiceInformationTitle;
	}
	public void setInvoiceInformationTitle(String invoiceInformationTitle) {
		this.invoiceInformationTitle = invoiceInformationTitle;
	}
	public String getInvoiceInformationValue() {
		return invoiceInformationValue;
	}
	public void setInvoiceInformationValue(String invoiceInformationValue) {
		this.invoiceInformationValue = invoiceInformationValue;
	}
	
	
}
