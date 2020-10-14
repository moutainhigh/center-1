package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.productcenter.model.PicInfo;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 限时抢购 详情输出类
 * @author houwen
 * date: 2014-09-18
 * @version1.0
 */
public class TimedScareBuyingDetailListResult extends RootResultWeb{
	
	@ZapcomApi(value = "sku编码")
	private String sku_code = "";
	
	@ZapcomApi(value = "产品名称")
	private String name = "";
	
	@ZapcomApi(value = "商品编码")
	private String product_code = "";
	
	@ZapcomApi(value = "商品现价")
	private String newPrice = "";
	
	@ZapcomApi(value = "商品原价")
	private String oldPrice = "";
	
	@ZapcomApi(value = "商品折扣")
	private String rebate = "";
	
	@ZapcomApi(value = "商品图片url")
	private List<PicInfo> photos = new ArrayList<PicInfo>();
	
	@ZapcomApi(value = "商品剩余件数")
	private String remaind_count = "";
	
	@ZapcomApi(value = "当前服务器时间")
	private String systemTime = "";
	
	@ZapcomApi(value = "结束时间")
	private String endTime = "";
	
	@ZapcomApi(value = "倒计时")
	private String surplusTime = "";
	
	@ZapcomApi(value = "商品标签")
	private String labels = "";
	
	@ZapcomApi(value = "评论数量")
	private String comment_count = "";
	
	@ZapcomApi(value = "商品数量")
	private String count = "";
	
	@ZapcomApi(value = "产品详情URl")
	private List<PicInfo> infophotos = new ArrayList<PicInfo>();

	@ZapcomApi(value = "收藏状态",remark="0为未收藏    1是已收藏")
	private String favstatus = "";
	
	@ZapcomApi(value="url")
	private String linkUrl = "";
	

	public String getProduct_code() {
		return product_code;
	}

	public void setProduct_code(String product_code) {
		this.product_code = product_code;
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

	public String getRemaind_count() {
		return remaind_count;
	}

	public void setRemaind_count(String remaind_count) {
		this.remaind_count = remaind_count;
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

	public String getLabels() {
		return labels;
	}

	public void setLabels(String labels) {
		this.labels = labels;
	}

	public String getComment_count() {
		return comment_count;
	}

	public void setComment_count(String comment_count) {
		this.comment_count = comment_count;
	}


	public List<PicInfo> getPhotos() {
		return photos;
	}

	public void setPhotos(List<PicInfo> photos) {
		this.photos = photos;
	}

	

	public List<PicInfo> getInfophotos() {
		return infophotos;
	}

	public void setInfophotos(List<PicInfo> infophotos) {
		this.infophotos = infophotos;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getFavstatus() {
		return favstatus;
	}

	public void setFavstatus(String favstatus) {
		this.favstatus = favstatus;
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
