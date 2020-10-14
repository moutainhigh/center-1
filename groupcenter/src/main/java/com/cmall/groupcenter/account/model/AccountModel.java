package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class AccountModel {
	@ZapcomApi(value = "账户金额")
	private String accountMoney = "";
	
	@ZapcomApi(value = "预计返利")	
	private String expectedRebateMoney = "";
	
	@ZapcomApi(value = "已返利")
	private String alreadyRebateMoney = "";

	public String getAccountMoney() {
		return accountMoney;
	}

	public void setAccountMoney(String accountMoney) {
		this.accountMoney = accountMoney;
	}

	public String getExpectedRebateMoney() {
		return expectedRebateMoney;
	}

	public void setExpectedRebateMoney(String expectedRebateMoney) {
		this.expectedRebateMoney = expectedRebateMoney;
	}

	public String getAlreadyRebateMoney() {
		return alreadyRebateMoney;
	}

	public void setAlreadyRebateMoney(String alreadyRebateMoney) {
		this.alreadyRebateMoney = alreadyRebateMoney;
	}
	
	
}
