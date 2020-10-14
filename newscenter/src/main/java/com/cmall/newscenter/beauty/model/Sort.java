package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 惠美丽_商品排序类
 * @author yangrong
 * date: 2014-09-11
 * @version1.0
 */
public class Sort {
	
	@ZapcomApi(value = "id",remark="排序id")
	private String id="";
	
	@ZapcomApi(value = "名称",remark="排序名称")
	private String name="";

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
