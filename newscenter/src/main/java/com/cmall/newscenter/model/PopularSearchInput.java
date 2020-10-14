package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 热门搜索
 * @author shiyz
 * date 2014-7-18
 * @version 1.0
 */
public class PopularSearchInput extends RootInput {

	@ZapcomApi(value = "关键字",demo="电影",remark="关键字",require = 1)
	private String keyword = "";

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
}
