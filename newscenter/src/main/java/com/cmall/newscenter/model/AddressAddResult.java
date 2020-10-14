package com.cmall.newscenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 添加地址输出类
 * @author shiyz
 * date 2014-8-4
 * @version 1.0
 */
public class AddressAddResult extends RootResultWeb {

	@ZapcomApi(value = "地址详情")
	private List<Address> address = new ArrayList<Address>();

	public List<Address> getAddress() {
		return address;
	}

	public void setAddress(List<Address> address) {
		this.address = address;
	}
	
}
