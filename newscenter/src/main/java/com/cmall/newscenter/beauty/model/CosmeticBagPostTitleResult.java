package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 惠美丽—发帖标题输出类
 * 
 * @author yangrong date: 2015-01-30
 * @version1.3.2
 */
public class CosmeticBagPostTitleResult extends RootResultWeb {

	
	@ZapcomApi(value = "晒贴标题内容")
	private String title_content = "";

	public String getTitle_content() {
		return title_content;
	}

	public void setTitle_content(String title_content) {
		this.title_content = title_content;
	}
	
}
