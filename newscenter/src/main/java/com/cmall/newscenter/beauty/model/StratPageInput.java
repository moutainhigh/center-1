package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 惠美丽—启动页输入类
 * @author yangrong
 * date: 2014-09-10
 * @version1.0
 */
public class StratPageInput extends RootInput {
	
	@ZapcomApi(value="app_code",demo="SI2007",require=1)
	private String app_code = "";

	@ZapcomApi(value="图片宽度")
	private  	Integer  picWidth = 0 ;
	
	public String getApp_code() {
		return app_code;
	}

	public void setApp_code(String app_code) {
		this.app_code = app_code;
	}

	public Integer getPicWidth() {
		return picWidth;
	}

	public void setPicWidth(Integer picWidth) {
		this.picWidth = picWidth;
	}
	
}
