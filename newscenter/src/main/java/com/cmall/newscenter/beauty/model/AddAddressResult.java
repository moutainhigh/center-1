package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 惠美丽-新增收货地址输出类
 * @author yangrong	
 * date 2014-8-20
 * @version 1.0
 */
public class AddAddressResult extends RootResultWeb {
	
	@ZapcomApi(value = "收货地址列表")
	private List<BeautyAddress> adress = new ArrayList<BeautyAddress>();
	
	@ZapcomApi(value = "新增的地址编号")
	private String addressId = "";

	public List<BeautyAddress> getAdress() {
		return adress;
	}

	public void setAdress(List<BeautyAddress> adress) {
		this.adress = adress;
	}

	public String getAddressId() {
		return addressId;
	}

	public void setAddressId(String addressId) {
		this.addressId = addressId;
	}
	
}
