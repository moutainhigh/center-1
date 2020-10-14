package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/***
 * 修改默认地址输入类
 * @author shiyz
 * date 2014-8-4
 * @version 1.0
 */
public class AddressSelectInput extends RootInput {

	@ZapcomApi(value = "地址ID",demo="DZ10001",require = 1)
	private String address = "";
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
}
