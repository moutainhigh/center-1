package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 惠美丽_商品分类
 * @author yangrong
 * date: 2014-09-11
 * @version1.0
 */
public class ProductCategory {

	@ZapcomApi(value = "分类Id",remark="100")
	private String id="";
	
	@ZapcomApi(value = "名称",remark="分类名称")
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
