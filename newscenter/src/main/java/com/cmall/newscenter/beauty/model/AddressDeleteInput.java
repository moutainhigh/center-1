package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;


/***
 * 删除地址输入类
 * @author yangrong
 * date 2014-9-10
 * @version 1.0
 */
public class AddressDeleteInput extends RootInput {

	@ZapcomApi(value = "地址ID",demo="DZ10001",require = 1)
	private String address = "";
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
}
