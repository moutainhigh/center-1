package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 *  区类
 * @author yangrong
 * date 2014-9-11
 * @version 1.0
 */
public class Area {

	@ZapcomApi(value="区id")
	private String Id  = "";
	
	@ZapcomApi(value="区名称")
	private String name = "";

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
