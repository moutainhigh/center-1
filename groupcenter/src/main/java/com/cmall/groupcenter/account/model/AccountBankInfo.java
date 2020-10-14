package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 用户微公社账户信息
 * 
 * @author srnpr
 * 
 */
public class AccountBankInfo {

	@ZapcomApi(value = "信息编号", remark = "信息编号，列表时用到", demo = "GCMB123456", require = 0)
	private String bankCode = "";

	@ZapcomApi(value = "银行名称", remark = "银行名称", demo = "工商银行", require = 0, verify = { "maxlength=40" })
	private String bankName = "";
	@ZapcomApi(value = "银行卡号", remark = "银行卡号", demo = "1234567890123456", require = 1, verify = {
			"minlength=6", "maxlength=30" })
	private String cardCode = "";
	@ZapcomApi(value = "证件类型", demo = "1234567890123456", require = 1, verify = { "maxlength=20" })
	private String papersType = "";
	@ZapcomApi(value = "证件号码", demo = "123456", require = 1, verify = { "maxlength=40" })
	private String papersCode = "";
	@ZapcomApi(value = "银行预留手机号", demo = "13512345678", require = 1, verify = "base=mobile")
	private String bankPhone = "";

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
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

	public String getPapersType() {
		return papersType;
	}

	public void setPapersType(String papersType) {
		this.papersType = papersType;
	}

	public String getPapersCode() {
		return papersCode;
	}

	public void setPapersCode(String papersCode) {
		this.papersCode = papersCode;
	}

	public String getBankPhone() {
		return bankPhone;
	}

	public void setBankPhone(String bankPhone) {
		this.bankPhone = bankPhone;
	}

}
