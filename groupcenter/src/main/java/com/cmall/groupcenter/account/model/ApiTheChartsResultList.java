package com.cmall.groupcenter.account.model;

import java.math.BigDecimal;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class ApiTheChartsResultList {
	@ZapcomApi(value="月返利",remark="月返利", require= 1)
	private String money;
	@ZapcomApi(value="手机号",remark="手机号", require= 1)
	private String mobilePhone;
	@ZapcomApi(value="等级",remark="等级", require= 1)
	private String levelName;
	
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	public String getLevelName() {
		return levelName;
	}
	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}
}