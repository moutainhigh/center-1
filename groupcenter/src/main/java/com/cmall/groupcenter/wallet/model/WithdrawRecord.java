package com.cmall.groupcenter.wallet.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class WithdrawRecord {
	@ZapcomApi(value = "提现日期", remark = "提现日期")
	private String withdrawTime;

	@ZapcomApi(value = "提现状态码", remark = "提现状态码(提现中:4497476000010001,提现成功:4497476000010005,提现失败:4497476000010006)")
	private String withdrawStatusCode;
	
	@ZapcomApi(value = "提现状态", remark = "提现状态(提现中,提现成功,提现失败)")
	private String withdrawStatus;

	@ZapcomApi(value = "提现账户", remark = "提现账户")
	private String withdrawAccount;

	@ZapcomApi(value = "提现金额", remark = "提现金额")
	private String withdrawMoney;
	
	@ZapcomApi(value = "第三方提现编号", remark = "第三方提现编号")
	private String thirdWithdrawCode;
	
	@ZapcomApi(value = "提现编号", remark = "提现编号")
	private String withdrawCode;

	public String getThirdWithdrawCode() {
		return thirdWithdrawCode;
	}

	public void setThirdWithdrawCode(String thirdWithdrawCode) {
		this.thirdWithdrawCode = thirdWithdrawCode;
	}

	public String getWithdrawCode() {
		return withdrawCode;
	}

	public void setWithdrawCode(String withdrawCode) {
		this.withdrawCode = withdrawCode;
	}

	public String getWithdrawTime() {
		return withdrawTime;
	}

	public void setWithdrawTime(String withdrawTime) {
		this.withdrawTime = withdrawTime;
	}

	

	public String getWithdrawStatus() {
		return withdrawStatus;
	}

	public void setWithdrawStatus(String withdrawStatus) {
		this.withdrawStatus = withdrawStatus;
	}

	public String getWithdrawAccount() {
		return withdrawAccount;
	}

	public void setWithdrawAccount(String withdrawAccount) {
		this.withdrawAccount = withdrawAccount;
	}

	public String getWithdrawMoney() {
		return withdrawMoney;
	}

	public void setWithdrawMoney(String withdrawMoney) {
		this.withdrawMoney = withdrawMoney;
	}

	public String getWithdrawStatusCode() {
		return withdrawStatusCode;
	}

	public void setWithdrawStatusCode(String withdrawStatusCode) {
		this.withdrawStatusCode = withdrawStatusCode;
	}
	
	

}
