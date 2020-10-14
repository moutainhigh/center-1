package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 限时抢购  商品类
 * @author yangrong
 * date: 2014-09-17
 * @version1.0
 */
public class TimedScareBuying {
	
	@ZapcomApi(value = "sku编码")
	private String sku_code = "";
	
	@ZapcomApi(value = "产品名称")
	private String name = "";
	
	@ZapcomApi(value = "商品现价")
	private String newPrice = "";
	
	@ZapcomApi(value = "商品原价")
	private String oldPrice = "";
	
	@ZapcomApi(value = "商品折扣")
	private String rebate = "";
	
	@ZapcomApi(value = "商品图片url")
	private String photoUrl = "";
	
	@ZapcomApi(value = "当前服务器时间")
	private String systemTime = "";
	
	@ZapcomApi(value = "结束时间")
	private String endTime = "";
	
	@ZapcomApi(value="商品剩余数量")
	private String remaind_count="";

	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNewPrice() {
		return newPrice;
	}

	public void setNewPrice(String newPrice) {
		this.newPrice = newPrice;
	}

	public String getOldPrice() {
		return oldPrice;
	}

	public void setOldPrice(String oldPrice) {
		this.oldPrice = oldPrice;
	}

	public String getRebate() {
		return rebate;
	}

	public void setRebate(String rebate) {
		this.rebate = rebate;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getSystemTime() {
		return systemTime;
	}

	public void setSystemTime(String systemTime) {
		this.systemTime = systemTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getRemaind_count() {
		return remaind_count;
	}

	public void setRemaind_count(String remaind_count) {
		this.remaind_count = remaind_count;
	}
	
}
