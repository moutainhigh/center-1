package com.cmall.groupcenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class WithdrawApiInput extends RootInput{
	
	@ZapcomApi(value="银行信息编号",remark="银行信息编号",demo="GCMB140817100003",require=0,verify={})
	private String bankCode="";
	
	
	
	@ZapcomApi(value="提现金额",remark="提现金额",demo="12",require=1,verify="base=money")
	private String withdrawAmount="";

	

	public String getWithdrawAmount() {
		return withdrawAmount;
	}

	public void setWithdrawAmount(String withdrawAmount) {
		this.withdrawAmount = withdrawAmount;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

}
