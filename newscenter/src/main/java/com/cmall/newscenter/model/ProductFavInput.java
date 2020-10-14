package com.cmall.newscenter.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 商品 - 收藏输入类
 * @author liqiang
 * date 2014-7-10
 * @version 1.0
 */
public class ProductFavInput extends RootInput{
	
	@ZapcomApi(value="product.id",remark="123456",demo="123456",require=1)
	private String product="";

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

}
