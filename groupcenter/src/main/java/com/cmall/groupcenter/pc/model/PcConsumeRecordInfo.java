package com.cmall.groupcenter.pc.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * PC版本消费内容
 * @author GaoYang
 *
 */
public class PcConsumeRecordInfo {
	
	@ZapcomApi(value = "年月", remark = "年月")
	private String yearMonth="";
	
	@ZapcomApi(value = "当月消费", remark = "当月消费(")
	private String monthConsumeMoney="0.00";
	
	@ZapcomApi(value = "当月级别", remark = "当月级别")
	private String monthLevel="";
	
	@ZapcomApi(value = "当月活跃好友数", remark = "当月活跃好友数")
	private String monthActiveNum="0";

	public String getYearMonth() {
		return yearMonth;
	}

	public void setYearMonth(String yearMonth) {
		this.yearMonth = yearMonth;
	}

	public String getMonthConsumeMoney() {
		return monthConsumeMoney;
	}

	public void setMonthConsumeMoney(String monthConsumeMoney) {
		this.monthConsumeMoney = monthConsumeMoney;
	}

	public String getMonthLevel() {
		return monthLevel;
	}

	public void setMonthLevel(String monthLevel) {
		this.monthLevel = monthLevel;
	}

	public String getMonthActiveNum() {
		return monthActiveNum;
	}

	public void setMonthActiveNum(String monthActiveNum) {
		this.monthActiveNum = monthActiveNum;
	}
	
	
	
}
