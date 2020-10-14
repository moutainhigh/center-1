package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 惠美丽—皮肤类型实体类
 * 
 * @author yangrong date: 2014-12-05
 * @version1.3.0
 */
public class SkinType {

	@ZapcomApi(value = "皮肤类型code")
	private String skin_code = "";

	@ZapcomApi(value = "皮肤类型名称")
	private String skin_name = "";

	public String getSkin_code() {
		return skin_code;
	}

	public void setSkin_code(String skin_code) {
		this.skin_code = skin_code;
	}

	public String getSkin_name() {
		return skin_name;
	}

	public void setSkin_name(String skin_name) {
		this.skin_name = skin_name;
	}

}
