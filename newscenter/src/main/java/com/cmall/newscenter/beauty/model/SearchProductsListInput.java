package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 搜索商品  输入类
 * @author yangrong
 * date 2014-9-20
 * @version 1.0
 */
public class SearchProductsListInput extends RootInput {
	
	@ZapcomApi(value="搜索关键字",remark="商品编号或商品名称的模糊查询")
	private String  keyword = "";
	
	@ZapcomApi(value="图片宽度")
	private  	Integer  picWidth = 0 ;

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public Integer getPicWidth() {
		return picWidth;
	}

	public void setPicWidth(Integer picWidth) {
		this.picWidth = picWidth;
	}

}
