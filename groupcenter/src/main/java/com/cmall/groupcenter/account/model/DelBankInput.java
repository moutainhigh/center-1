package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class DelBankInput extends RootInput {

	@ZapcomApi(value = "银行卡信息", remark = "银行卡信息", demo = "GCMB140808100001", require = 0)
	private String bankCode = "";

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

}
