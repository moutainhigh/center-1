package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/***
 * 栏目
 * @author liqiang
 * date 2014-7-17
 * @version 1.0
 */
public class Column {

	@ZapcomApi(value="欄目ID")
	private String id = "";
	
	@ZapcomApi(value = "栏目类型")
	private int type;
	
	@ZapcomApi(value = "动态",remark="动态")
	private String name = "";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	} 
	
}
