package com.cmall.productcenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 
 * 获取高端商品分类下具体内容的输入参数
 * @author GaoYang
 *
 */
public class ApiGetHighendTypeInput extends RootInput{

	/**
	 * 高端商品分类(现阶段主要是好物产)
	 */
	@ZapcomApi(value="高端商品分类")
	private String highendType = "";

	public String getHighendType() {
		return highendType;
	}

	public void setHighendType(String highendType) {
		this.highendType = highendType;
	}
	
}
