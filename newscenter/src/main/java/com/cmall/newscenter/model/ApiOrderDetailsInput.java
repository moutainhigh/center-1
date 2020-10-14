package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 订单详情输入参数
 * @author wz
 *
 */
public class ApiOrderDetailsInput extends RootInput{
	@ZapcomApi(value="买家编号",require=1)
	private String buyer_code="";
	@ZapcomApi(value="订单编号",require=1)
	private String order_code="";
	@ZapcomApi(value="图片宽度")
	private  	Integer  picWidth = 0 ;
	@ZapcomApi(value="浏览器IP",remark = "微信支付时该字段必输")
	private String browserUrl="";
	
	public String getBuyer_code() {
		return buyer_code;
	}
	public void setBuyer_code(String buyer_code) {
		this.buyer_code = buyer_code;
	}
	public String getOrder_code() {
		return order_code;
	}
	public void setOrder_code(String order_code) {
		this.order_code = order_code;
	}
	public Integer getPicWidth() {
		return picWidth;
	}
	public void setPicWidth(Integer picWidth) {
		this.picWidth = picWidth;
	}
	public String getBrowserUrl() {
		return browserUrl;
	}
	public void setBrowserUrl(String browserUrl) {
		this.browserUrl = browserUrl;
	}
}
