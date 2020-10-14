package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 删除购物车商品 输入类
 * @author yangrong
 * date 2014-9-20
 * @version 1.0
 */
public class DeleteSkuToShopCarInput extends RootInput {

	@ZapcomApi(value="sku编码",demo="购物车列表获取",require=1)
	private String sku_code = "";

	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}
	
	
}
