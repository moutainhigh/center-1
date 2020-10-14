package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
/**
 * 订单-已购买的商品组及总数量
 * @author liqiang
 * date 2014-7-22
 * @version 1.0
 */
public class SaleProductGroup {

	@ZapcomApi(value="数量",demo="100")
	private int amout = 0;

	@ZapcomApi(value="在售商品信息")
	private Sale_Product product = new Sale_Product();

	public int getAmout() {
		return amout;
	}

	public void setAmout(int amout) {
		this.amout = amout;
	}

	public Sale_Product getProduct() {
		return product;
	}

	public void setProduct(Sale_Product product) {
		this.product = product;
	}
	
	
}
