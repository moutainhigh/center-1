package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 删除地址输入类
 * @author shiyz
 * date 2014-8-4
 * @version 1.0
 */

public class AddressDeleteInput extends RootInput {
	
	@ZapcomApi(value = "地址Id",demo="dz10001",require = 1)
	private String address = "";

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
