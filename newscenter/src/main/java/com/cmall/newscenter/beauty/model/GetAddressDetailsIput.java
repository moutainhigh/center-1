package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 惠美丽-获取收货地址输入类
 * @author yangrong
 * date 2014-8-20
 * @version 1.0
 */
public class GetAddressDetailsIput extends RootInput {
	
	@ZapcomApi(value = "地址ID")
	private String address_id = "";

	public String getAddress_id() {
		return address_id;
	}

	public void setAddress_id(String address_id) {
		this.address_id = address_id;
	}

}
