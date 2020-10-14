package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 试用商品在线下单输出类
 * @author shiyz
 * date 2014-8-26
 */
public class TryOrderTrialApiInput extends RootInput {
	
	@ZapcomApi(value="商品编号",require=1,demo="123456",verify="base=number")
	private String product = "";
	
	@ZapcomApi(value="送货地址",require=1,demo="123456")
	private String address = "";
	
	@ZapcomApi(value="数量",demo="10")
	private int amount = 0;
	
	@ZapcomApi(value="三级区域编码")
	private String  areaCode="";

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	
}
