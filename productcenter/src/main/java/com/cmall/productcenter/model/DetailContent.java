package com.cmall.productcenter.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class DetailContent {

	@ZapcomApi(value = "1为按钮，2为区间",  remark = "1为按钮，2为区间")
	private String type = "";
	
	@ZapcomApi(value = "文字",  remark = "如果类型为2:文字格式为    文字1&文字2")
	private String text = "";

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	
	
}
