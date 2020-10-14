package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 订单-收货地址类
 * @author liqiang
 * date 2014-7-22
 * @version 1.0
 */
public class Address {
	
	@ZapcomApi(value="地址id")
	private String id = "";

	@ZapcomApi(value="是否默认地址，是-1，否-0",demo="0,1",require=1,verify={ "in=0,1" })
	private int is_default ;
	
	@ZapcomApi(value="姓名",demo="郭XX",require = 1)
	private String name = "";
	
	
	@ZapcomApi(value="手机号码",demo="13520351350",require = 1,verify="base=mobile")
	private String mobile = "";

	@ZapcomApi(value="邮政编码",demo="100080",require = 1,verify={"base=number","equallength=6"})
	private String zipcode = "";

	@ZapcomApi(value="省",demo="辽宁")
	private String province = "";

	@ZapcomApi(value="市",demo="沈阳")
	private String city = "";

	@ZapcomApi(value="县区",demo="和平")
	private String county = "";

	@ZapcomApi(value="详细地址",demo="1号楼XXX",require=1)
	private String street = "";
	
	@ZapcomApi(value="三级区域编号",demo="102323")
	private String  county_code = "";
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public int getIs_default() {
		return is_default;
	}

	public void setIs_default(int is_default) {
		this.is_default = is_default;
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

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCounty_code() {
		return county_code;
	}

	public void setCounty_code(String county_code) {
		this.county_code = county_code;
	}

	
}
