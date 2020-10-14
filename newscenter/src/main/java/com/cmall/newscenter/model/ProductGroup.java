package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 试用商品实体类 产品
 * @author jl
 *
 */
public class ProductGroup {

	@ZapcomApi(value = "产品数量",demo="1")
	private int amount=1;//产品数量
	@ZapcomApi(value = "商品信息")
	private Product product=new Product();
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
}
