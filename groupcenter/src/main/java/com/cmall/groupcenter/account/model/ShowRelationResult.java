package com.cmall.groupcenter.account.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class ShowRelationResult extends RootResultWeb {

	@ZapcomApi(value = "社友总数")
	private int sumRelation = 0;
	@ZapcomApi(value = "社友明细")
	private List<DayRetion> dayRetions = new ArrayList<DayRetion>();
	@ZapcomApi(value = "一度好友月度活跃数量")
	private int oneActiveAccount = 0;
	@ZapcomApi(value = "一度好友总数")
	private int oneSumAccount=0;
	@ZapcomApi(value = "一度好友月度活跃消费")
	private String oneActiveMoney = "0.00";
	@ZapcomApi(value = "二度好友月度活跃数量")
	private int twoActiveAccount = 0;
	@ZapcomApi(value = "二度好友总数")
	private int twoSumAccount = 0;
	@ZapcomApi(value = "二度好友月度活跃消费")
	private String twoActiveMoney = "0.00";
	@ZapcomApi(value = "个人头像url")
	private String headIconUrl = ""; 
	public String getHeadIconUrl() {
		return headIconUrl;
	}
	public void setHeadIconUrl(String headIconUrl) {
		this.headIconUrl = headIconUrl;
	}

	public int getSumRelation() {
		return sumRelation;
	}

	public void setSumRelation(int sumRelation) {
		this.sumRelation = sumRelation;
	}

	public List<DayRetion> getDayRetions() {
		return dayRetions;
	}

	public void setDayRetions(List<DayRetion> dayRetions) {
		this.dayRetions = dayRetions;
	}

	public int getOneActiveAccount() {
		return oneActiveAccount;
	}

	public void setOneActiveAccount(int oneActiveAccount) {
		this.oneActiveAccount = oneActiveAccount;
	}

	public String getOneActiveMoney() {
		return oneActiveMoney;
	}

	public void setOneActiveMoney(String oneActiveMoney) {
		this.oneActiveMoney = oneActiveMoney;
	}

	public int getTwoActiveAccount() {
		return twoActiveAccount;
	}

	public void setTwoActiveAccount(int twoActiveAccount) {
		this.twoActiveAccount = twoActiveAccount;
	}

	public String getTwoActiveMoney() {
		return twoActiveMoney;
	}

	public void setTwoActiveMoney(String twoActiveMoney) {
		this.twoActiveMoney = twoActiveMoney;
	}

	public int getOneSumAccount() {
		return oneSumAccount;
	}

	public void setOneSumAccount(int oneSumAccount) {
		this.oneSumAccount = oneSumAccount;
	}

	public int getTwoSumAccount() {
		return twoSumAccount;
	}

	public void setTwoSumAccount(int twoSumAccount) {
		this.twoSumAccount = twoSumAccount;
	}

}
