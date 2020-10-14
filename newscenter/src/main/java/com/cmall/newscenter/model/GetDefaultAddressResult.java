package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 试用商品收货地址输出类
 * @author shiyz
 *
 */
public class GetDefaultAddressResult extends RootResultWeb {
	
	@ZapcomApi(value="收货地址")
	Address address = new Address();

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
	

}
