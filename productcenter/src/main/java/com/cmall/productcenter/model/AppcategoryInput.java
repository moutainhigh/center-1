package com.cmall.productcenter.model;

import com.srnpr.zapcom.topapi.RootInput;

/**
 * ClassName 商品虚类管理
 * Date: 2014-06-26
 *@author Administrator
 *@version 1.0
 */

public class AppcategoryInput extends RootInput {

	/**app编号*/
	private String app_code;

	public String getApp_code() {
		return app_code;
	}

	public void setApp_code(String app_code) {
		this.app_code = app_code;
	}
	
	
}
