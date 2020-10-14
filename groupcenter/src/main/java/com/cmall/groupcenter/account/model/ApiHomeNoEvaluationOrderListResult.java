package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class ApiHomeNoEvaluationOrderListResult {
	@ZapcomApi(value="商品主图")
	private String mainpic_url = "";
	@ZapcomApi(value="商品编号")
	private String product_code = "";
	@ZapcomApi(value="商品名称")
	private String product_name = "";
	@ZapcomApi(value="sku编号")
	private String sku_code = "";
	@ZapcomApi(value="积分")
	private String integral = "";
	@ZapcomApi(value="订单编号")
	private String order_code = "";
	@ZapcomApi(value = "分享到买家秀赠送积分总量")
	private String buyerShowIntegral;
	
	public String getBuyerShowIntegral() {
		return buyerShowIntegral;
	}
	public void setBuyerShowIntegral(String buyerShowIntegral) {
		this.buyerShowIntegral = buyerShowIntegral;
	}
	public String getMainpic_url() {
		return mainpic_url;
	}
	public void setMainpic_url(String mainpic_url) {
		this.mainpic_url = mainpic_url;
	}
	public String getProduct_code() {
		return product_code;
	}
	public void setProduct_code(String product_code) {
		this.product_code = product_code;
	}
	public String getProduct_name() {
		return product_name;
	}
	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}
	public String getSku_code() {
		return sku_code;
	}
	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}
	public String getIntegral() {
		return integral;
	}
	public void setIntegral(String integral) {
		this.integral = integral;
	}
	public String getOrder_code() {
		return order_code;
	}
	public void setOrder_code(String order_code) {
		this.order_code = order_code;
	}
	
	
	
}
