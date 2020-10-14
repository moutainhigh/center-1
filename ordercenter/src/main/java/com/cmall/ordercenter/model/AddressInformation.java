package com.cmall.ordercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class AddressInformation {
	
	@ZapcomApi(value = "地址ID")
	private String address_id; 
	@ZapcomApi(value = "是否默认地址",remark="0：非默认  ；1：默认")
	private String address_default;
	@ZapcomApi(value = "姓名")
	private String address_name;
	@ZapcomApi(value = "手机号码")
	private String address_mobile;
	@ZapcomApi(value = "邮政编码")
	private String address_postalcode;
	@ZapcomApi(value = "省")
	private String address_province;
	@ZapcomApi(value = "市")
	private String address_city;
	@ZapcomApi(value = "县区")
	private String address_county;
	@ZapcomApi(value = "详细地址")
	private String address_street;
	@ZapcomApi(value = "用户编号")
	private String address_code;
	@ZapcomApi(value = "排序")
	private String sort_num;
	@ZapcomApi(value = "所属app",demo="例如：SI2009")
	private String app_code;
	@ZapcomApi(value = "区编码")
	private String area_code;
	@ZapcomApi(value = "综合费用")
	private String price;
	@ZapcomApi(value = "创建时间")
	private String create_time;
	@ZapcomApi(value = "更新时间")
	private String update_time;
	@ZapcomApi(value="身份证号码")
	private String idNumber;
	
	
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
	public String getAddress_id() {
		return address_id;
	}
	public void setAddress_id(String address_id) {
		this.address_id = address_id;
	}
	public String getAddress_default() {
		return address_default;
	}
	public void setAddress_default(String address_default) {
		this.address_default = address_default;
	}
	public String getAddress_name() {
		return address_name;
	}
	public void setAddress_name(String address_name) {
		this.address_name = address_name;
	}
	public String getAddress_mobile() {
		return address_mobile;
	}
	public void setAddress_mobile(String address_mobile) {
		this.address_mobile = address_mobile;
	}
	public String getAddress_postalcode() {
		return address_postalcode;
	}
	public void setAddress_postalcode(String address_postalcode) {
		this.address_postalcode = address_postalcode;
	}
	public String getAddress_province() {
		return address_province;
	}
	public void setAddress_province(String address_province) {
		this.address_province = address_province;
	}
	public String getAddress_city() {
		return address_city;
	}
	public void setAddress_city(String address_city) {
		this.address_city = address_city;
	}
	public String getAddress_county() {
		return address_county;
	}
	public void setAddress_county(String address_county) {
		this.address_county = address_county;
	}
	public String getAddress_street() {
		return address_street;
	}
	public void setAddress_street(String address_street) {
		this.address_street = address_street;
	}
	public String getAddress_code() {
		return address_code;
	}
	public void setAddress_code(String address_code) {
		this.address_code = address_code;
	}
	public String getSort_num() {
		return sort_num;
	}
	public void setSort_num(String sort_num) {
		this.sort_num = sort_num;
	}
	public String getApp_code() {
		return app_code;
	}
	public void setApp_code(String app_code) {
		this.app_code = app_code;
	}
	public String getArea_code() {
		return area_code;
	}
	public void setArea_code(String area_code) {
		this.area_code = area_code;
	}
	public String getIdNumber() {
		return idNumber;
	}
	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}
	
	
	
}
