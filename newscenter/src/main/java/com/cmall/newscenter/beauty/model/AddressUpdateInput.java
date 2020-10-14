package com.cmall.newscenter.beauty.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 惠美丽-修改地址信息输入类
 * @author yangrong	
 * date 2014-9-10
 * @version 1.0
 */
public class AddressUpdateInput  extends RootInput {

	
	@ZapcomApi(value="地址id",demo="DZ140910100001",require=1)
	private String id = "";
	
	@ZapcomApi(value="姓名",demo="张三",require=1)
	private String name = "";
	
	@ZapcomApi(value="手机号码",demo="13111102059",require=1,verify = "base=mobile")
	private String mobile = "";
	
	@ZapcomApi(value="邮政编码",demo="100124")
	private String postcode = "";
	
	@ZapcomApi(value="省市区",demo="北京市朝阳区",require=1)
	private String provinces = "";
	
	@ZapcomApi(value="区编码",demo="131182",require=1)
	private String  areaCode = "";

	@ZapcomApi(value="详细地址",demo="高碑店小郊亭1376号",require=1)
	private String street = "";
	
	@ZapcomApi(value="是否为默认地址",demo="1或者0",remark="如果不传，默认不修改")
	private String isdefault = "";
	
	@ZapcomApi(value="邮箱",demo="123@aa.com")
	private String email = "";
	
	@ZapcomApi(value="身份证号码")
	private String idNumber = "";
	
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

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
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
