package com.cmall.newscenter.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 获取栏目详情（url）输出类
 * @author guz
 * date 2014-9-15
 * @version 1.0
 */
public class MienIntroductionApplyResult extends RootResultWeb{
	
	@ZapcomApi(value="链接地址",demo="www.baidu.com")
	private String column_detail_url = "";

	public String getColumn_detail_url() {
		return column_detail_url;
	}

	public void setColumn_detail_url(String column_detail_url) {
		this.column_detail_url = column_detail_url;
	}
	

}
