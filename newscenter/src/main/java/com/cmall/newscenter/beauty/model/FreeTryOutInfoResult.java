package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 免费试用详情输出类
 * @author yangrong
 * date: 2014-09-16
 * @version1.0
 */
public class FreeTryOutInfoResult extends RootResultWeb{
	
	@ZapcomApi(value = "活动编号")
	private String activityCode = "";
	
	@ZapcomApi(value = "商品图片")
	private String photo = "";
	
	@ZapcomApi(value = "sku编码")
	private String sku_code = "";
	
	@ZapcomApi(value = "产品名称")
	private String name = "";
	
	@ZapcomApi(value = "商品原价")
	private String old_price = "";
	
	@ZapcomApi(value = "商品试用价")
	private String tryout_price = "";
	
	@ZapcomApi(value = "商品件数")
	private String count = "";
	
	@ZapcomApi(value = "商品剩余件数")
	private String surplus_count = "";
	
	@ZapcomApi(value = "试用商品当前系统时间")
	private String Systemtime = "";
	
	@ZapcomApi(value = "试用商品结束时间")
	private String time = "";
	
	@ZapcomApi(value = "倒计时")
	private String surplusTime = "";
	
	@ZapcomApi(value = "商品申请人数")
	private String tryout_count = "";
	
	@ZapcomApi(value = "申请试用状态",remark="申请状态 ：未申请：449746890001；已申请：449746890002；申请通过：449746890003；已结束：449746890004；已发货：449746890005")
	private String status = "";
	
	@ZapcomApi(value = "试用须知描述")
	private String describe = "";
	
	@ZapcomApi(value="url")
	private String linkUrl = "";

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOld_price() {
		return old_price;
	}

	public void setOld_price(String old_price) {
		this.old_price = old_price;
	}

	public String getTryout_price() {
		return tryout_price;
	}

	public void setTryout_price(String tryout_price) {
		this.tryout_price = tryout_price;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTryout_count() {
		return tryout_count;
	}

	public void setTryout_count(String tryout_count) {
		this.tryout_count = tryout_count;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public String getSystemtime() {
		return Systemtime;
	}

	public void setSystemtime(String systemtime) {
		Systemtime = systemtime;
	}

	public String getActivityCode() {
		return activityCode;
	}

	public void setActivityCode(String activityCode) {
		this.activityCode = activityCode;
	}

	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}

	public String getSurplus_count() {
		return surplus_count;
	}

	public void setSurplus_count(String surplus_count) {
		this.surplus_count = surplus_count;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

	public String getSurplusTime() {
		return surplusTime;
	}

	public void setSurplusTime(String surplusTime) {
		this.surplusTime = surplusTime;
	}
	
}
