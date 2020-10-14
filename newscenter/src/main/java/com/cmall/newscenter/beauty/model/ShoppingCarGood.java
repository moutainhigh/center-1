package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 购物车商品类
 * @author yangrong
 * date: 2014-09-16
 * @version1.0
 */
public class ShoppingCarGood {
	
	@ZapcomApi(value = "商品图片")
	private String photo = "";
	
	@ZapcomApi(value = "商品标签")
	private List<String> labels = new ArrayList<String>();
	
	@ZapcomApi(value = "商品标题")
	private String title = "";
	
	@ZapcomApi(value = "库存状态",remark="是否有货")
	private String status = "";
	
	@ZapcomApi(value = "商品购买数")
	private String count = "";
	
	@ZapcomApi(value = "商品当前价格")
	private String sell_price = "";
	
	@ZapcomApi(value = "商品id")
	private String product_code = "";
	
	@ZapcomApi(value = "商品原价")
	private String market_price = "";

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getSell_price() {
		return sell_price;
	}

	public void setSell_price(String sell_price) {
		this.sell_price = sell_price;
	}

	public String getProduct_code() {
		return product_code;
	}

	public void setProduct_code(String product_code) {
		this.product_code = product_code;
	}

	public String getMarket_price() {
		return market_price;
	}

	public void setMarket_price(String market_price) {
		this.market_price = market_price;
	}
	
	
	
}
