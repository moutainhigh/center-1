package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiOrderListInput extends RootInput{
	@ZapcomApi(value="买家编号",require=1)
	private String buyer_code="";
	@ZapcomApi(value="下一页",require=1)
	private String nextPage="";
	@ZapcomApi(value="订单状态",remark="下单成功-未付款:4497153900010001,下单成功-未发货:4497153900010002,已发货:4497153900010003,已收货:4497153900010004,交易成功:4497153900010005,交易失败:4497153900010006")
	private String order_status="";
	@ZapcomApi(value="微信支付IP")
	private String browserUrl = "";
	public String getBuyer_code() {
		return buyer_code;
	}
	public void setBuyer_code(String buyer_code) {
		this.buyer_code = buyer_code;
	}
	public String getNextPage() {
		return nextPage;
	}
	public void setNextPage(String nextPage) {
		this.nextPage = nextPage;
	}
	public String getOrder_status() {
		return order_status;
	}
	public void setOrder_status(String order_status) {
		this.order_status = order_status;
	}
	public String getBrowserUrl() {
		return browserUrl;
	}
	public void setBrowserUrl(String browserUrl) {
		this.browserUrl = browserUrl;
	}
	
}
