package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.productcenter.model.PicInfo;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 试用商品详情输出类
 * @author yangrong
 * date: 2014-09-16
 * @version1.0
 */
public class TryOutGoodInfoResult extends RootResultWeb{
	
	@ZapcomApi(value = "sku编码")
	private String sku_code = "";
	
	@ZapcomApi(value = "产品名称")
	private String name = "";
	
	@ZapcomApi(value = "商品编码")
	private String product_code = "";
	
	@ZapcomApi(value = "商品图片")
	private List<PicInfo> photos = new ArrayList<PicInfo>();
	
	@ZapcomApi(value = "商品折扣")
	private String rebate = "";
	
	@ZapcomApi(value = "商品原价")
	private String oldPrice = "";
	
	@ZapcomApi(value = "商品现价")
	private String newPrice = "";
	
	@ZapcomApi(value = "商品月销量")
	private String stock_num = "";
	
	@ZapcomApi(value = "商品标签")
	private List<String> labels = new ArrayList<String>();
	
	@ZapcomApi(value = "产品详情图片list")
	private List<PicInfo> infophotos = new ArrayList<PicInfo>();
	
	@ZapcomApi(value = "商品评论数")
	private String comment_count = "";
	
	@ZapcomApi(value="url")
	private String linkUrl = "";


	public String getProduct_code() {
		return product_code;
	}

	public void setProduct_code(String product_code) {
		this.product_code = product_code;
	}

	public List<PicInfo> getPhotos() {
		return photos;
	}

	public void setPhotos(List<PicInfo> photos) {
		this.photos = photos;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRebate() {
		return rebate;
	}

	public void setRebate(String rebate) {
		this.rebate = rebate;
	}

	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}

	
	public String getOldPrice() {
		return oldPrice;
	}

	public void setOldPrice(String oldPrice) {
		this.oldPrice = oldPrice;
	}

	public String getNewPrice() {
		return newPrice;
	}

	public void setNewPrice(String newPrice) {
		this.newPrice = newPrice;
	}

	public String getStock_num() {
		return stock_num;
	}

	public void setStock_num(String stock_num) {
		this.stock_num = stock_num;
	}

	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	public List<PicInfo> getInfophotos() {
		return infophotos;
	}

	public void setInfophotos(List<PicInfo> infophotos) {
		this.infophotos = infophotos;
	}

	public String getComment_count() {
		return comment_count;
	}

	public void setComment_count(String comment_count) {
		this.comment_count = comment_count;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}
	

}
