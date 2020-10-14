package com.cmall.newscenter.beauty.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 惠美丽-获取收货地址输出类
 * @author yangrong	
 * date 2014-8-20
 * @version 1.0
 */
public class GetAddressDetailsResult extends RootResultWeb  {
	
	@ZapcomApi(value = "收货地址信息")
	private BeautyAddress adress = new BeautyAddress();

	public BeautyAddress getAdress() {
		return adress;
	}

	public void setAdress(BeautyAddress adress) {
		this.adress = adress;
	}
	
}
