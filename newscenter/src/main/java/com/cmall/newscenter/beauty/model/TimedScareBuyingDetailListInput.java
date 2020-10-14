package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 限时抢购详情 输入类
 * @author houwen
 * date: 2014-09-17
 * @version1.0
 */
public class TimedScareBuyingDetailListInput extends RootInput {

	@ZapcomApi(value = "商品sku编号",remark = "商品sku编号" ,demo= "132737",require = 1)
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
