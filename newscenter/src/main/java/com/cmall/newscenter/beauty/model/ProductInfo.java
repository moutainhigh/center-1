package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 帖子详情-》商品信息  输出类
 * @author houwen
 * date 2014-9-15
 * @version 1.0
 */
public class ProductInfo {
	
	@ZapcomApi(value = "sku编码")
	private String  id= "";
	
	@ZapcomApi(value = "商品名称")
	private String name = "";
	
	@ZapcomApi(value = "商品图片")
	private String photos ="";
	
	@ZapcomApi(value = "月销量")
	private String stock_num = "";
	
	@ZapcomApi(value = "商品当前价格")
	private String sell_price = ""  ;
	
	@ZapcomApi(value = "商品原价")
	private String market_price ="";
	
	@ZapcomApi(value = "商品类型",remark="明确商品类型的列表不返回     0：普通商品  1：限购商品   2：试用商品")
	private String productType ="";
	
	@ZapcomApi(value = "商品标签")
	private List<String> labels = new ArrayList<String>();
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStock_num() {
		return stock_num;
	}

	public void setStock_num(String stock_num) {
		this.stock_num = stock_num;
	}

	public String getPhotos() {
		return photos;
	}

	public void setPhotos(String photos) {
		this.photos = photos;
	}

	public String getSell_price() {
		return sell_price;
	}

	public void setSell_price(String sell_price) {
		this.sell_price = sell_price;
	}

	public String getMarket_price() {
		return market_price;
	}

	public void setMarket_price(String market_price) {
		this.market_price = market_price;
	}

	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

}
