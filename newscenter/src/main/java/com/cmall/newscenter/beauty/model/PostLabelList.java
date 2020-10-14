package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 帖子标签列表类输出
 * @author houwen
 * date 2014-09-28
 * @version 1.0
 */
public class PostLabelList {
	
	@ZapcomApi(value="标签名称")
	private String label_name = "";

	public String getLabel_name() {
		return label_name;
	}

	public void setLabel_name(String label_name) {
		this.label_name = label_name;
	}
	
}
