package com.cmall.groupcenter.third.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class RebateProductDetail {

	@ZapcomApi(value = "商品编号", remark = "商品编号")
	private String productCode="";
	
	@ZapcomApi(value = "sku编号", remark = "sku编号")
	private String skuCode="";
	
	@ZapcomApi(value = "商品金额", remark = "商品金额")
	private String reckonMoney="";
	
	@ZapcomApi(value = "购买数量", remark = "购买数量")
	private String productNum="";
	
	@ZapcomApi(value = "返利金额", remark = "返利金额")
	private String rebateMoney="";

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getSkuCode() {
		return skuCode;
	}

	public void setSkuCode(String skuCode) {
		this.skuCode = skuCode;
	}

	public String getReckonMoney() {
		return reckonMoney;
	}

	public void setReckonMoney(String reckonMoney) {
		this.reckonMoney = reckonMoney;
	}

	public String getProductNum() {
		return productNum;
	}

	public void setProductNum(String productNum) {
		this.productNum = productNum;
	}

	public String getRebateMoney() {
		return rebateMoney;
	}

	public void setRebateMoney(String rebateMoney) {
		this.rebateMoney = rebateMoney;
	}
}
