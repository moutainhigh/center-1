package com.cmall.groupcenter.groupapp.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 首页广告
 * @author panwei
 *
 */
public class HomeContent {

	@ZapcomApi(value = "图片链接")
	private String imageUrl="";
	
	@ZapcomApi(value = "跳转类型",remark="0:web页面")
	private String jumpType="";
	
	@ZapcomApi(value = "跳转所需传递的参数",remark="web页面的话就是链接")
	private String params="";
	
	@ZapcomApi(value = "wab显示标题")
	private String title="";

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getJumpType() {
		return jumpType;
	}

	public void setJumpType(String jumpType) {
		this.jumpType = jumpType;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}
	
	
}
