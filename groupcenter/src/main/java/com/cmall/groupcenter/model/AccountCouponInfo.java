package com.cmall.groupcenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class AccountCouponInfo {
	
	@ZapcomApi(value = "优惠卷标识", remark = "优惠卷标识")
	private String uid = "";
	
	@ZapcomApi(value = "优惠卷名称", remark = "优惠卷名称")
	private String name = "";

	@ZapcomApi(value = "优惠卷面额", remark = "优惠卷面额")
	private String amount = "0";

	@ZapcomApi(value = "使用限制说明", remark = "使用限制说明")
	private String description="";
	
	@ZapcomApi(value = "使用起始时间", remark = "使用起始时间")
	private String startTime="";

	@ZapcomApi(value = "使用结束时间", remark = "使用结束时间")
	private String endTime="";
	
	@ZapcomApi(value = "系统专享标识", remark = "系统专享标识,不是专享：4497472000090001 ; 安卓专享：4497472000090002 ; 苹果专享：4497472000090003 ")
	private String isExclusiveString = "";
	
	@ZapcomApi(value = "优惠卷码", remark = "优惠卷码")
	private String couponCode = "";
	
	@ZapcomApi(value = "优惠卷背景图片", remark = "优惠卷背景图片")
	private String picUrl = "";

	@ZapcomApi(value = "优惠卷兑换方法", remark = "优惠卷兑换方法")
	private String convert = "";
	
	@ZapcomApi(value = "app名称", remark = "优惠卷相关APP名称")
	private String appName = "";
	
	@ZapcomApi(value = "ios包名", remark = "优惠卷相关APP,ios包名")
	private String iosAppPackage = "";
	
	@ZapcomApi(value = "ios包路径", remark = "优惠卷相关APP,ios包路径")
	private String iosUrl = "";
	
	@ZapcomApi(value = "android包名", remark = "优惠卷相关APP,android包名")
	private String adAppPackage = "";
	
	@ZapcomApi(value = "android包路径", remark = "优惠卷相关APP,android包路径")
	private String adUrl = "";
	
	@ZapcomApi(value = "排出版本号", remark = "排出版本号")
	private String comVersion = "";
	
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getIsExclusiveString() {
		return isExclusiveString;
	}

	public void setIsExclusiveString(String isExclusiveString) {
		this.isExclusiveString = isExclusiveString;
	}

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public String getConvert() {
		return convert;
	}

	public void setConvert(String convert) {
		this.convert = convert;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getIosUrl() {
		return iosUrl;
	}

	public void setIosUrl(String iosUrl) {
		this.iosUrl = iosUrl;
	}

	public String getAdUrl() {
		return adUrl;
	}

	public void setAdUrl(String adUrl) {
		this.adUrl = adUrl;
	}

	


	public String getIosAppPackage() {
		return iosAppPackage;
	}

	public void setIosAppPackage(String iosAppPackage) {
		this.iosAppPackage = iosAppPackage;
	}

	public String getAdAppPackage() {
		return adAppPackage;
	}

	public void setAdAppPackage(String adAppPackage) {
		this.adAppPackage = adAppPackage;
	}

	public String getComVersion() {
		return comVersion;
	}

	public void setComVersion(String comVersion) {
		this.comVersion = comVersion;
	}
	
}
