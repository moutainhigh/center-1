package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class MienIntroduction {
	@ZapcomApi(value="链接地址",demo="www.baidu.com")
	private String column_detail_url = "";

	@ZapcomApi(value="所属分类",demo="449746500001000200040001")
	private String info_category = "";

	public String getColumn_detail_url() {
		return column_detail_url;
	}

	public void setColumn_detail_url(String column_detail_url) {
		this.column_detail_url = column_detail_url;
	}

	public String getInfo_category() {
		return info_category;
	}

	public void setInfo_category(String info_category) {
		this.info_category = info_category;
	}
}
