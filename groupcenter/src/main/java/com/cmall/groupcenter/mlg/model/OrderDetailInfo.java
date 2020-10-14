package com.cmall.groupcenter.mlg.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class OrderDetailInfo {

	@ZapcomApi(value = "商品编号 ", remark = "")
	String goods_id = "";

	@ZapcomApi(value = "商品名称", remark = "")
	String goods_name = "";

	@ZapcomApi(value = " ", remark = "")
	String spec = "";

	@ZapcomApi(value = "Sku编号", remark = "")
	String sku = "";
	
	@ZapcomApi(value = "购买单价 ", remark = "")
	double price = 0.0;
	
	@ZapcomApi(value = "购买数量", remark = "")
	int quantity = 0;

	public String getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(String goods_id) {
		this.goods_id = goods_id;
	}

	public String getGoods_name() {
		return goods_name;
	}

	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}

	public String getSpec() {
		return spec;
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	
}
