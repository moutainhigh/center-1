package com.cmall.newscenter.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 订单-配送地址输出类
 * @author liqiang
 * date 2014-7-22
 * @version 1.0
 */
public class OrderAdressResult extends RootResultWeb{
	
	@ZapcomApi(value="地址信息")
	private Address address = new Address();

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

}
