package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 商品 - 收藏输入类
 * @author yangrong
 * date 2014-9-11
 * @version 1.0
 */
public class ProductFavInput extends RootInput{
	
	@ZapcomApi(value="sku编码",remark="123456",demo="123456",require=1,verify="minlength=10")
	private String sku_code="";

	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}

	

}
