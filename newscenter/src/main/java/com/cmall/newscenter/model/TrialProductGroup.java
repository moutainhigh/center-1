package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
/**
 * 订单-在试用的商品清单及总数量
 * @author liqiang
 * date 2014-7-22
 * @version 1.0
 */
public class TrialProductGroup {

	@ZapcomApi(value="数量",demo="100")
	private int amout;

	@ZapcomApi(value="试用商品信息")
	private Trial_product product = new Trial_product();

	public int getAmout() {
		return amout;
	}

	public void setAmout(int amout) {
		this.amout = amout;
	}

	public Trial_product getProduct() {
		return product;
	}

	public void setProduct(Trial_product product) {
		this.product = product;
	}
	
}
