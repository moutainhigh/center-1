package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class MoneyWithdrawItem {

	@ZapcomApi(value = "主标题")
	private String titleName = "";

	@ZapcomApi(value = "展示类型", remark = "0表示行项目  1表示表头", demo = "0")
	private int showType = 0;

	@ZapcomApi(value = "标题一")
	private String titleOne = "";

	@ZapcomApi(value = "标题二")
	private String titleTwo = "";

	@ZapcomApi(value = "文本一")
	private String textOne = "";

	@ZapcomApi(value = "文本二")
	private String textTwo = "";
	@ZapcomApi(value = "月份")
	private String month = "";

	public String getTitleName() {
		return titleName;
	}

	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}

	public int getShowType() {
		return showType;
	}

	public void setShowType(int showType) {
		this.showType = showType;
	}

	public String getTitleOne() {
		return titleOne;
	}

	public void setTitleOne(String titleOne) {
		this.titleOne = titleOne;
	}

	public String getTitleTwo() {
		return titleTwo;
	}

	public void setTitleTwo(String titleTwo) {
		this.titleTwo = titleTwo;
	}

	public String getTextOne() {
		return textOne;
	}

	public void setTextOne(String textOne) {
		this.textOne = textOne;
	}

	public String getTextTwo() {
		return textTwo;
	}

	public void setTextTwo(String textTwo) {
		this.textTwo = textTwo;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

}
