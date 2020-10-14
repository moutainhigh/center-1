package com.cmall.ordercenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiGetOrdersForThreeInput extends RootInput {
	
	
	/**
	 * 商户编号
	 */
	@ZapcomApi(value="商户编号")
	private String sellerCode="";

	public String getSellerCode() {
		return sellerCode;
	}

	public void setSellerCode(String sellerCode) {
		this.sellerCode = sellerCode;
	}
	
	/**
	 * 1  查询店铺已支付的且未发送到第三方的订单-4497153900010002,
	 * 2 查询店铺支付后取消的订单 4497153900010006, 
	 * 3 查询店铺完成的订单 4497153900010005
	 */
	private int type = 0;

	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	
	
	
}
