package com.cmall.newscenter.beauty.model;


import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 在售商品类
 * @author houwen
 * date 2014-8-28
 * @version 1.0
 */
public class SaleProduct {
	
	@ZapcomApi(value = "sku编码")
	private String id = ""  ;

	@ZapcomApi(value = "商品图片") 
	private String photo = "";
	
	@ZapcomApi(value = "商品标签")
	private List<String> labels = new ArrayList<String>();
	
	@ZapcomApi(value = "商品标题/名称")
	private String title = "" ;
	
	@ZapcomApi(value = "商品购买数/销量")
	private String buy_count = "" ;
	
	@ZapcomApi(value = "商品当前价格")
	private String sell_price = ""  ;
	
	@ZapcomApi(value = "商品原价")
	private String market_price ="";
	
	@ZapcomApi(value = "商品类型",remark="明确商品类型的列表不返回     0：普通商品  1：限购商品   2：试用商品")
	private String productType = "";
	
	@ZapcomApi(value = "收藏时间",remark="2014-02-02 01:00:00")
	private String favTime="";
	
	public String getFavTime() {
		return favTime;
	}

	public void setFavTime(String favTime) {
		this.favTime = favTime;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBuy_count() {
		return buy_count;
	}

	public void setBuy_count(String buy_count) {
		this.buy_count = buy_count;
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


	
	
	

	

}
