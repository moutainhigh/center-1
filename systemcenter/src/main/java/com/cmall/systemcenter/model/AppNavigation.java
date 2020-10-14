package com.cmall.systemcenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 返回导航维护内容
 * @author liqt
 *
 */
public class AppNavigation {
	@ZapcomApi(value="导航类型",remark="共四种类型。 首页:4497467900040001;分类:4497467900040002;购物车:4497467900040003;我的:4497467900040004;导航背景图片:4497467900040005")
	private String navigationType = "";
	
	@ZapcomApi(value="Android选中前图片")
	private String before_pic = "";
	
	@ZapcomApi(value="Android选中后图片")
	private String after_pic = "";

	@ZapcomApi(value = "ios选中前图片高度")
	private int iosBeforePicHeight = 0;
	
	@ZapcomApi(value = "ios选中后图片高度")
	private int iosAfterPicHeight = 0;
	
	@ZapcomApi(value="类型名称")
	private String typeName = "";
	
	@ZapcomApi(value= "选中前字体颜色")
	private String beforeFontColor = "";
	
	@ZapcomApi(value="选中后字体颜色")
	private String afterFontColor="";
	
	public String getNavigationType() {
		return navigationType;
	}

	public void setNavigationType(String navigationType) {
		this.navigationType = navigationType;
	}

	public String getBefore_pic() {
		return before_pic;
	}

	public void setBefore_pic(String before_pic) {
		this.before_pic = before_pic;
	}

	public String getAfter_pic() {
		return after_pic;
	}

	public void setAfter_pic(String after_pic) {
		this.after_pic = after_pic;
	}

	public int getIosBeforePicHeight() {
		return iosBeforePicHeight;
	}

	public void setIosBeforePicHeight(int iosBeforePicHeight) {
		this.iosBeforePicHeight = iosBeforePicHeight;
	}

	public int getIosAfterPicHeight() {
		return iosAfterPicHeight;
	}

	public void setIosAfterPicHeight(int iosAfterPicHeight) {
		this.iosAfterPicHeight = iosAfterPicHeight;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getBeforeFontColor() {
		return beforeFontColor;
	}

	public void setBeforeFontColor(String beforeFontColor) {
		this.beforeFontColor = beforeFontColor;
	}

	public String getAfterFontColor() {
		return afterFontColor;
	}

	public void setAfterFontColor(String afterFontColor) {
		this.afterFontColor = afterFontColor;
	}

}
