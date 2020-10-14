package com.cmall.productcenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootResult;

public class ApiGetProductForhpResult extends RootResult {
	
	@ZapcomApi(value="商品编码")
	private String product_code="";
	@ZapcomApi(value="商品名称")
	private String product_name="";
	@ZapcomApi(value="商品简名称")
	private String product_shortname="";
	@ZapcomApi(value="最小价格")
	private String min_sell_price="0";
	@ZapcomApi(value="最大价格")
	private String max_sell_price="0";
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
	public String getProduct_shortname() {
		return product_shortname;
	}
	public void setProduct_shortname(String product_shortname) {
		this.product_shortname = product_shortname;
	}
	public String getMin_sell_price() {
		return min_sell_price;
	}
	public void setMin_sell_price(String min_sell_price) {
		this.min_sell_price = min_sell_price;
	}
	public String getMax_sell_price() {
		return max_sell_price;
	}
	public void setMax_sell_price(String max_sell_price) {
		this.max_sell_price = max_sell_price;
	}
	
	
}
