package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class MoneyHistoryDetail {

	@ZapcomApi(value = "月份", remark = "月份", demo = "")
	private String month = "";
	
	@ZapcomApi(value = "返利金额", remark = "返利金额", demo = "0.00")
	private String rebateMoney = "0.00";
	
	@ZapcomApi(value = "消费金额", remark = "消费金额", demo = "0.00")
	private String consumeMoney = "0.00";
	
	@ZapcomApi(value = "级别", remark = "级别", demo = "")
	private String levelName = "";
	
	@ZapcomApi(value = "级别描述", remark = "级别描述", demo = "")
	private String levelDescription = "";
	
	@ZapcomApi(value = "年份", remark = "年份", demo = "")
	private String year = "";

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getRebateMoney() {
		return rebateMoney;
	}

	public void setRebateMoney(String rebateMoney) {
		this.rebateMoney = rebateMoney;
	}

	public String getConsumeMoney() {
		return consumeMoney;
	}

	public void setConsumeMoney(String consumeMoney) {
		this.consumeMoney = consumeMoney;
	}

	public String getLevelName() {
		return levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

	public String getLevelDescription() {
		return levelDescription;
	}

	public void setLevelDescription(String levelDescription) {
		this.levelDescription = levelDescription;
	}
	
}
