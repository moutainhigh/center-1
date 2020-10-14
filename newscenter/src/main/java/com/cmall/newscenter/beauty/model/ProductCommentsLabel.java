package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 商品评论列表类
 * @author houwen
 * date 2014-08-21
 * @version 1.0
 */
public class ProductCommentsLabel {
	
	
	@ZapcomApi(value="印象标签")
	private String label = "";
	
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
