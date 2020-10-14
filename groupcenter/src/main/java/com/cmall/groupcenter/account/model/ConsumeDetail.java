package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class ConsumeDetail {

	@ZapcomApi(value = "日期", demo = "2015-06")
	private String date = "";
	
	@ZapcomApi(value = "返利一", demo = "345")
	private String rebateOne = "";
	
	@ZapcomApi(value = "返利二", demo = "345")
	private String rebateTwo = "";
	
	@ZapcomApi(value = "返利三", demo = "345")
	private String rebateThree = "";
	
	@ZapcomApi(value = "消费一", demo = "345")
	private String consumeOne = "";
	
	@ZapcomApi(value = "消费二", demo = "345")
	private String consumeTwo = "";
	
	@ZapcomApi(value = "消费三", demo = "345")
	private String consumeThree = "";

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getRebateOne() {
		return rebateOne;
	}

	public void setRebateOne(String rebateOne) {
		this.rebateOne = rebateOne;
	}

	public String getRebateTwo() {
		return rebateTwo;
	}

	public void setRebateTwo(String rebateTwo) {
		this.rebateTwo = rebateTwo;
	}

	public String getRebateThree() {
		return rebateThree;
	}

	public void setRebateThree(String rebateThree) {
		this.rebateThree = rebateThree;
	}

	public String getConsumeOne() {
		return consumeOne;
	}

	public void setConsumeOne(String consumeOne) {
		this.consumeOne = consumeOne;
	}

	public String getConsumeTwo() {
		return consumeTwo;
	}

	public void setConsumeTwo(String consumeTwo) {
		this.consumeTwo = consumeTwo;
	}

	public String getConsumeThree() {
		return consumeThree;
	}

	public void setConsumeThree(String consumeThree) {
		this.consumeThree = consumeThree;
	}
	
	
}
