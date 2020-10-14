package com.cmall.ordercenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiAlipayMoveProcessOrderInput extends RootInput{
	@ZapcomApi(value="商品订单号")
	private String out_trade_no="";
	@ZapcomApi(value="商品名称")
	private String subject="";
	@ZapcomApi(value="商品详情")
	private String body="";
	
	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getOut_trade_no() {
		return out_trade_no;
	}
	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
	
}
