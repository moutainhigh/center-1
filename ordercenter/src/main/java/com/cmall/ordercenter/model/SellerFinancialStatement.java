package com.cmall.ordercenter.model;

import com.srnpr.zapcom.baseclass.BaseClass;

/**
 * 商户结算3.0-商户周期结算单
 * 对应oc_bill_merchant_new
 * @author zht
 *
 */
public class SellerFinancialStatement extends BaseClass {
	private String settleCode;
	private String merchantCode;
	private String merchantName;
	private String invoiceAmount;
//	private String taxAmount;
	private String periodMoney;
	private String addDeduction;
	private String actualPayAmount;
	private String taxAmount;
	private String startTime;
	private String endTime;
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
	
	public String getTaxAmount() {
		return taxAmount;
	}
	public void setTaxAmount(String taxAmount) {
		this.taxAmount = taxAmount;
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
}
