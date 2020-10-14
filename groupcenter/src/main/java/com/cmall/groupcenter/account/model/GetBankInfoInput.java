package com.cmall.groupcenter.account.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class GetBankInfoInput extends RootInput {

	@ZapcomApi(value = "卡号", demo = "6228480010586235", require = 1, verify = { "maxlength=40" })
	private String bankNo = "";

	public String getBankNo() {
		return bankNo;
	}

	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
	}
	
}
