package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 删除订单-输入类
 * @author yangrong
 * date: 2014-10-08
 * @version1.0
 */
public class DeleteOrderInput extends RootInput {
	
	@ZapcomApi(value = "用户code",remark = "非必填   可根据当前登录用户获取")
	private String user_code="";
	
	@ZapcomApi(value = "订单code",remark = "我的订单中获取" ,require=1)
	private String order_code="";

	public String getUser_code() {
		return user_code;
	}

	public void setUser_code(String user_code) {
		this.user_code = user_code;
	}

	public String getOrder_code() {
		return order_code;
	}

	public void setOrder_code(String order_code) {
		this.order_code = order_code;
	}
	
	
	
}
