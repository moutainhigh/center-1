package com.cmall.productcenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 提示语信息
 * 
 * @author ligj
 * 
 */
public class ReminderContent {

	@ZapcomApi(value="提示内容")
	private String content = "";

	@ZapcomApi(value="提示图片")
	private String picUrl = "";

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	
	
}
