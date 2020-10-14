package com.cmall.ordercenter.model;

import java.math.BigDecimal;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class FSkuPrice extends OrderBase {

	@ZapcomApi(value="sku_code",demo="211",remark="和传入的sku_code 一样")
	private String sku_code="";
	
	@ZapcomApi(value="销售价",demo="33.11")
	private BigDecimal sell_price=new BigDecimal(0);
	
	@ZapcomApi(value="优惠价",demo="33.11")
	private BigDecimal vip_price=new BigDecimal(0);

	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}

	public BigDecimal getSell_price() {
		return sell_price;
	}

	public void setSell_price(BigDecimal sell_price) {
		this.sell_price = sell_price;
	}

	public BigDecimal getVip_price() {
		return vip_price;
	}

	public void setVip_price(BigDecimal vip_price) {
		this.vip_price = vip_price;
	}

}
