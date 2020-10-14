package com.cmall.groupcenter.wallet.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
/**
 * 钱包(银行卡列表)
 * 
 * @author huangs
 * 
 */
public class BankInfo {

	@ZapcomApi(value = "银行预留手机号", demo = "13512345678", require = 1, verify = "base=mobile")
	private String bankPhone = "";
	
	@ZapcomApi(value = "银行名称", remark = "银行名称", demo = "工商银行", require = 0, verify = { "maxlength=40" })
	private String bankName = "";
	
	@ZapcomApi(value = "银行卡号", remark = "银行卡号", demo = "1234567890123456", require = 1, verify = {"minlength=6", "maxlength=30" })
	private String cardCode = "";
	
	@ZapcomApi(value = "信息编号", remark = "信息编号，列表时用到", demo = "GCMB123456", require = 0)
	private String bankCode = "";
	
	@ZapcomApi(value = "银行卡类型", remark = "银行卡类型", demo = "信用卡", require = 0)
	private String cardKind = "";

	public String getBankPhone() {
		return bankPhone;
	}

	public void setBankPhone(String bankPhone) {
		this.bankPhone = bankPhone;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getCardCode() {
		return cardCode;
	}

	public void setCardCode(String cardCode) {
		this.cardCode = cardCode;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getCardKind() {
		return cardKind;
	}

	public void setCardKind(String cardKind) {
		this.cardKind = cardKind;
	}
	
	
}
