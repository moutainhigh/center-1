package com.cmall.newscenter.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 更新地址输入类
 * @author shiyz
 * date 2014-8-4
 * @version 1.0
 */
public class AddressUpateInput extends RootInput {

	@ZapcomApi(value = "地址信息")
	private Address address = new Address();

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
	
}
