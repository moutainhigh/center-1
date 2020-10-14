package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 每日关系
 * 
 * @author srnpr
 * 
 */
public class DayRetion {

	@ZapcomApi(value = "日期")
	private String date = "";
	@ZapcomApi(value = "一度社友")
	private int one = 0;
	@ZapcomApi(value = "二度社友")
	private int two = 0;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getOne() {
		return one;
	}

	public void setOne(int one) {
		this.one = one;
	}

	public int getTwo() {
		return two;
	}

	public void setTwo(int two) {
		this.two = two;
	}

}
