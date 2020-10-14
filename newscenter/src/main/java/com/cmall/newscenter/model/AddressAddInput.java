package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 添加地址输入类
 * @author shiyz
 * date 2014-8-4
 * @version 1.0
 */

public class AddressAddInput extends RootInput {

	@ZapcomApi(value = "添加地址详情")
	private Address address = new Address();

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
	
}
