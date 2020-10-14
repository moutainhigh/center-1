package com.cmall.productcenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootResult;

public class ApiGetProductForFlashResult extends RootResult {

	@ZapcomApi(value="商品编号")
	private String product_code="";
	@ZapcomApi(value="商品名称")
	private String product_name="";
	@ZapcomApi(value="商品状态")
	private String product_status="";
	@ZapcomApi(value="商品市场价格")
	private String product_price="";
	@ZapcomApi(value="商品最小库存")
	private int product_stock=0;
	@ZapcomApi(value="商品最小库存的sku的售价")
	private String sell_price="0";
	public String getProduct_code() {
		return product_code;
	}
	public void setProduct_code(String product_code) {
		this.product_code = product_code;
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
	public int getProduct_stock() {
		return product_stock;
	}
	public void setProduct_stock(int product_stock) {
		this.product_stock = product_stock;
	}
	public String getSell_price() {
		return sell_price;
	}
	public void setSell_price(String sell_price) {
		this.sell_price = sell_price;
	}
	public String getProduct_price() {
		return product_price;
	}
	public void setProduct_price(String product_price) {
		this.product_price = product_price;
	}
	
	
}
