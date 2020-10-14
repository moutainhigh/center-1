package com.cmall.ordercenter.model;

import java.math.BigDecimal;

public class ActivitySkuEntity {
	
	/**
	 * 商品的sku编号
	 */
	private String sku_code="";
	

	/**
	 * 商品的活动编号
	 */
	private String activity_code="";
	
	
	/**
	 * 商品的零售价格
	 */
	private BigDecimal sell_price= new BigDecimal(0.00);
	

	/**
	 * 活动的起始时间
	 */
	private String begin_time="";
	
	
	/**
	 * 活动的结束时间
	 */
	private String end_time="";
	
	
	/**
	 * 卖家编号
	 */
	private String sellerCode = "";

	
	public String getSellerCode() {
		return sellerCode;
	}


	public void setSellerCode(String sellerCode) {
		this.sellerCode = sellerCode;
	}


	public String getSku_code() {
		return sku_code;
	}


	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}


	public String getActivity_code() {
		return activity_code;
	}


	public void setActivity_code(String activity_code) {
		this.activity_code = activity_code;
	}

	public BigDecimal getSell_price() {
		return sell_price;
	}

	public void setSell_price(BigDecimal sell_price) {
		this.sell_price = sell_price;
	}


	public String getBegin_time() {
		return begin_time;
	}


	public void setBegin_time(String begin_time) {
		this.begin_time = begin_time;
	}


	public String getEnd_time() {
		return end_time;
	}


	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	
	
	
	
}
