package com.cmall.ordercenter.model;

import java.math.BigDecimal;

/**
 * 商户结算3.0-发票实体
 * @author zht
 *
 */
public class Invoice {
	private String invoiceCode;		//发票号
	private BigDecimal amount = new BigDecimal(0);			//金额(元)
	private BigDecimal taxAmount = new BigDecimal(0);		//税额(元)
	private BigDecimal taxRate = new BigDecimal(0);			//税率
	private BigDecimal totalMoney = new BigDecimal(0);		//价税合计(元)
	public String getInvoiceCode() {
		return invoiceCode;
	}
	public void setInvoiceCode(String invoiceCode) {
		this.invoiceCode = invoiceCode;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public BigDecimal getTaxAmount() {
		return taxAmount;
	}
	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}
	public BigDecimal getTaxRate() {
		return taxRate;
	}
	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
	}
	public BigDecimal getTotalMoney() {
		return totalMoney;
	}
	public void setTotalMoney(BigDecimal totalMoney) {
		this.totalMoney = totalMoney;
	}
}
