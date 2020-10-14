package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncRequest;
/**
 * 支付宝移动支付参数
 * @author wz
 *
 */
public class RsyncRequestAlipayMoveInformation implements IRsyncRequest{
	private String ordId;  //订单号
	private String payNo; //支付流水号
	private String bankCd; //入款银行
	private String payMoney; //支付金额
	private String payTime; //支付时间
	private String acctBankNo; //收款账号
	private String webPayNo; //网关支付订单号
	
	
	public String getWebPayNo() {
		return webPayNo;
	}
	public void setWebPayNo(String webPayNo) {
		this.webPayNo = webPayNo;
	}
	public String getOrdId() {
		return ordId;
	}
	public void setOrdId(String ordId) {
		this.ordId = ordId;
	}
	public String getPayNo() {
		return payNo;
	}
	public void setPayNo(String payNo) {
		this.payNo = payNo;
	}
	public String getBankCd() {
		return bankCd;
	}
	public void setBankCd(String bankCd) {
		this.bankCd = bankCd;
	}
	public String getPayMoney() {
		return payMoney;
	}
	public void setPayMoney(String payMoney) {
		this.payMoney = payMoney;
	}
	public String getPayTime() {
		return payTime;
	}
	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}
	public String getAcctBankNo() {
		return acctBankNo;
	}
	public void setAcctBankNo(String acctBankNo) {
		this.acctBankNo = acctBankNo;
	}
	
}
