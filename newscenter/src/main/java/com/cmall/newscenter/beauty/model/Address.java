package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 惠美丽地址类
 * @author yangrong
 * date 2014-8-25
 * @version 1.0
 */
public class Address {
	
	@ZapcomApi(value="姓名")
	private String name = "";
	
	@ZapcomApi(value="手机号码")
	private String mobile = "";

	@ZapcomApi(value="详细地址")
	private String street = "";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}
	

	
}
