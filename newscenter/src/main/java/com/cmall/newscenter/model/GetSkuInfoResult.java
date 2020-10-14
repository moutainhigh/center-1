package com.cmall.newscenter.model;



import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 品牌 - 在售商品列表输出类
 * @author liqiang
 * date 2014-7-10
 * @version 1.0
 */
public class GetSkuInfoResult extends RootResultWeb{
	
	@ZapcomApi(value = "在售商品")
	private Productinfo products = new Productinfo();

	public Productinfo getProducts() {
		return products;
	}

	public void setProducts(Productinfo products) {
		this.products = products;
	}


}
