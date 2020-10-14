package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 商品详情  输入类
 * @author yangrong
 * date 2014-9-15
 * @version 1.0
 */
public class ProductInfoOneInput extends RootInput {
	
	@ZapcomApi(value="sku编码",demo="132737",require=1)
	private String sku_code = "";
	
	@ZapcomApi(value = "屏幕宽度")
	private String width = "";

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}
	
	

}
