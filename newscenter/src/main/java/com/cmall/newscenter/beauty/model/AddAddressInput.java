package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 惠美丽-新增收货地址输入类
 * 
 * @author yangrong date 2014-8-20
 * @version 1.0
 */
public class AddAddressInput extends RootInput {

	@ZapcomApi(value = "收货人", remark = "收货人", demo = "张三", require = 1)
	private String name = "";

	@ZapcomApi(value = "手机号码", remark = "手机号码", demo = "13033102025", require = 1, verify = "base=mobile")
	private String phone = "";

	@ZapcomApi(value = "邮政编码", remark = "邮政编码", demo = "100000")
	private String postcode = "";

	@ZapcomApi(value = "省市区", remark = "省市区", demo = "北京市朝阳区", require = 1)
	private String province = "";

	@ZapcomApi(value = "区编码", demo = "131182", require = 1)
	private String areaCode = "";

	@ZapcomApi(value = "是否默认", demo = "0",remark="第一次添加地址直接设为默认地址")
	private String isDefault = "";

	@ZapcomApi(value = "详细地址", remark = "详细地址", demo = "高碑店小郊亭1376号", require = 1)
	private String address = "";

	@ZapcomApi(value = "邮箱", demo = "abc@aa.com")
	private String email = "";
	
	@ZapcomApi(value="身份证号码")
	private String idNumber = "";
	

	public String getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
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
