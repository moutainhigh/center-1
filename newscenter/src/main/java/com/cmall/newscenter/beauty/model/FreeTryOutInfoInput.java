package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 免费试用详情输入类
 * @author yangrong
 * date: 2014-09-16
 * @version1.0
 */
public class FreeTryOutInfoInput extends RootInput {

	@ZapcomApi(value = "sku编码",require = 1)
	private String sku_code = "";
	
	@ZapcomApi(value = "试用商品结束时间",require = 1)
	private String end_time = "";
	
	@ZapcomApi(value="图片宽度")
	private  	Integer  picWidth = 0 ;
	
	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}

	public Integer getPicWidth() {
		return picWidth;
	}

	public void setPicWidth(Integer picWidth) {
		this.picWidth = picWidth;
	}
	
}
