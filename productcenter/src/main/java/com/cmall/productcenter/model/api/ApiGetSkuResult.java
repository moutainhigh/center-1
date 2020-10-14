package com.cmall.productcenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootResult;

public class ApiGetSkuResult extends RootResult {
	
	@ZapcomApi(value="sku编号")
	private String sku_code="";
	@ZapcomApi(value="销售价")
	private String sell_price="";
	@ZapcomApi(value="市场价")
	private String market_price="";
	@ZapcomApi(value="库存数")
	private String stock_num="";
	@ZapcomApi(value="产品名称")
	private String sku_name;
	@ZapcomApi(value="商品名称")
	private String product_name;
	@ZapcomApi(value="商品状态")
	private String product_status;
	@ZapcomApi(value="所属APP")
	private String seller_code;
	
	@ZapcomApi(value="商品编号")
	private String product_code;
	
	public String getProduct_code() {
		return product_code;
	}
	public void setProduct_code(String product_code) {
		this.product_code = product_code;
	}
	public String getSku_code() {
		return sku_code;
	}
	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
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
	public String getStock_num() {
		return stock_num;
	}
	public void setStock_num(String stock_num) {
		this.stock_num = stock_num;
	}
	public String getSku_name() {
		return sku_name;
	}
	public void setSku_name(String sku_name) {
		this.sku_name = sku_name;
	}
	public String getProduct_name() {
		return product_name;
	}
	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}
	public String getProduct_status() {
		return product_status;
	}
	public void setProduct_status(String product_status) {
		this.product_status = product_status;
	}
	public String getSeller_code() {
		return seller_code;
	}
	public void setSeller_code(String seller_code) {
		this.seller_code = seller_code;
	}
	
}
