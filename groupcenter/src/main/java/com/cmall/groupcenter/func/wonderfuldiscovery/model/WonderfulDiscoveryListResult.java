package com.cmall.groupcenter.func.wonderfuldiscovery.model;


import com.cmall.groupcenter.paymoney.util.Base64Util;
import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 模块:精彩发现
 * 功能:提供移动终端数据显示信息
 * @author LHY
 * 2015年1月14日 下午4:40:23
 */
public class WonderfulDiscoveryListResult {
	@ZapcomApi(value="名称",remark="名称", require= 1)
	private String title;
	@ZapcomApi(value="图片",remark="图片", require= 1)
	private String picUrl;
	@ZapcomApi(value="描述信息",remark="描述信息", require= 1)
	private String description;
	@ZapcomApi(value="ios下载地址",remark="ios下载地址")
	private String iosUrl;
	@ZapcomApi(value="android下载地址",remark="android下载地址")
	private String androidUrl;
	@ZapcomApi(value="更新时间",remark="更新时间")
	private String updateTime;
	@ZapcomApi(value="ios包名",remark="ios包名")
	private String iosPackage;
	@ZapcomApi(value="android包名",remark="android包名")
	private String androidPackage;
	@ZapcomApi(value="ios64位编码",remark="对ios参数进行编码", demo="{'appname':'iosPackage','appurl':'iosUrl'}")
	private String iso64Code = "";
	@ZapcomApi(value="appcode",remark="区分app")
	private String appCode ="";
	@ZapcomApi(value="android版本号")
	private String androidVersion = "";
	@ZapcomApi(value="圆图图片地址")
	private String picCircleUrl = "";
	@ZapcomApi(value="排除版本号")
	private String compareVersion = "";
	
	
	public String getAppCode() {
		return appCode;
	}
	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPicUrl() {
		return picUrl;
	}
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getIosUrl() {
		return iosUrl;
	}
	public void setIosUrl(String iosUrl) {
		this.iosUrl = iosUrl;
	}
	public String getAndroidUrl() {
		return androidUrl;
	}
	public void setAndroidUrl(String androidUrl) {
		this.androidUrl = androidUrl;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public String getIosPackage() {
		return iosPackage;
	}
	public void setIosPackage(String iosPackage) {
		this.iosPackage = iosPackage;
	}
	public String getAndroidPackage() {
		return androidPackage;
	}
	public void setAndroidPackage(String androidPackage) {
		this.androidPackage = androidPackage;
	}
	public String getIso64Code() {
		String data = "{'appname':'"+iosPackage+"','appurl':'"+iosUrl+"'}";
		return Base64Util.encode(data.getBytes());
	}
	public void setIso64Code(String iso64Code) {
		this.iso64Code = iso64Code;
	}
	public String getAndroidVersion() {
		return androidVersion;
	}
	public void setAndroidVersion(String androidVersion) {
		this.androidVersion = androidVersion;
	}
	public String getPicCircleUrl() {
		return picCircleUrl;
	}
	public void setPicCircleUrl(String picCircleUrl) {
		this.picCircleUrl = picCircleUrl;
	}
	public String getCompareVersion() {
		return compareVersion;
	}
	public void setCompareVersion(String compareVersion) {
		this.compareVersion = compareVersion;
	}
}
