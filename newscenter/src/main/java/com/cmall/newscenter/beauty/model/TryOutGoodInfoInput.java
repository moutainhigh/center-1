package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 试用商品详情输入类
 * @author yangrong
 * date: 2014-09-16
 * @version1.0
 */
public class TryOutGoodInfoInput extends RootInput {

	@ZapcomApi(value = "商品sku_code",require = 1)
	private String sku_code = "";
	
	@ZapcomApi(value = "屏幕宽度")
	private String width = "";
	
	@ZapcomApi(value = "试用商品结束时间")
	private String end_time = "";

	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	
}
