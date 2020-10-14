package com.cmall.groupcenter.account.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class AddBankInput extends RootInput {

	@ZapcomApi(value = "银行卡号", remark = "银行卡号", demo = "1234567890123456", require = 1, verify = {
			"minlength=10", "maxlength=30", "base=number" })
	private String cardCode = "";
	
	@ZapcomApi(value = "银行预留手机号", demo = "13512345678", require = 1, verify = "base=mobile")
	private String bankPhone = "";
	
	@ZapcomApi(value = "卡的种类", demo = "储蓄卡", require = 1)
	private String cardKind = "";
	@ZapcomApi(value = "银行名称", demo = "中国银行", require = 1)
	private String bankName = "";
	
	public String getCardCode() {
		return cardCode;
	}
	public void setCardCode(String cardCode) {
		this.cardCode = cardCode;
	}
	public String getBankPhone() {
		return bankPhone;
	}
	public void setBankPhone(String bankPhone) {
		this.bankPhone = bankPhone;
	}
	public String getCardKind() {
		return cardKind;
	}
	public void setCardKind(String cardKind) {
		this.cardKind = cardKind;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	
	
}
