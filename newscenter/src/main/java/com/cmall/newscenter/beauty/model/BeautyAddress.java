package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 惠美丽地址类
 * @author yangrong
 * date 2014-8-25
 * @version 1.0
 */
public class BeautyAddress {
	
	@ZapcomApi(value="地址id")
	private String id = "";
	
	@ZapcomApi(value="姓名")
	private String name = "";
	
	@ZapcomApi(value="手机号码")
	private String mobile = "";
	
	@ZapcomApi(value="邮政编码")
	private String postcode = "";
	
	@ZapcomApi(value="省市区")
	private String provinces = "";
	
	@ZapcomApi(value="区编码",demo="131182")
	private String  areaCode = "";

	@ZapcomApi(value="详细地址")
	private String street = "";
	
	@ZapcomApi(value="是否默认",remark="1为默认地址    0不是默认")
	private String isdefault = "";
	
	@ZapcomApi(value="邮箱")
	private String email = "";
	
	@ZapcomApi(value="身份证号码")
	private String idNumber = "";
	
	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	
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

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getProvinces() {
		return provinces;
	}

	public void setProvinces(String provinces) {
		this.provinces = provinces;
	}

	public String getIsdefault() {
		return isdefault;
	}

	public void setIsdefault(String isdefault) {
		this.isdefault = isdefault;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}
	
	

	
}
