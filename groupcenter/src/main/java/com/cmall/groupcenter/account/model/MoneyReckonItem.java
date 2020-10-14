package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class MoneyReckonItem {

	@ZapcomApi(value = "账户名称")
	private String accountName = "";
	@ZapcomApi(value = "关系维度")
	private String relation = "";
	@ZapcomApi(value = "清分时间")
	private String reckonTime = "";
	@ZapcomApi(value = "清分金额")
	private String reckonMoney = "";
	@ZapcomApi(value = "清分类型")
	private String reckonType = "";

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public String getReckonMoney() {
		return reckonMoney;
	}

	public void setReckonMoney(String reckonMoney) {
		this.reckonMoney = reckonMoney;
	}

	public String getReckonTime() {
		return reckonTime;
	}

	public void setReckonTime(String reckonTime) {
		this.reckonTime = reckonTime;
	}

	public String getReckonType() {
		return reckonType;
	}

	public void setReckonType(String reckonType) {
		this.reckonType = reckonType;
	}

}
