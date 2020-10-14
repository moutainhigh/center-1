package com.cmall.groupcenter.wallet.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class WithdrawApplyInput extends RootInput{

	@ZapcomApi(value="用户编号", require = 1,demo="MI140702100002")
	private String memberCode = "";
	
	@ZapcomApi(value="提现金额",require = 1,verify="base=money",demo="10.00")
	private String withdrawMoney = "";
	
	@ZapcomApi(value="银行卡编号",require = 1,demo="GCMB140920100003")
	private String bankCode = "";
	
	@ZapcomApi(value="提现编号",require = 1)
	private String thirdWithdrawCode = "";

	public String getThirdWithdrawCode() {
		return thirdWithdrawCode;
	}

	public void setThirdWithdrawCode(String thirdWithdrawCode) {
		this.thirdWithdrawCode = thirdWithdrawCode;
	}

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	public String getWithdrawMoney() {
		return withdrawMoney;
	}

	public void setWithdrawMoney(String withdrawMoney) {
		this.withdrawMoney = withdrawMoney;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

}
