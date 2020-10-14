package com.cmall.groupcenter.active.product;

import com.cmall.groupcenter.active.BaseSkuInfo;

public class SkuInfoForRet extends BaseSkuInfo{

	private String productCode;
	
	/**
	 * skuKey
	 */
	private String skuKey = "";
	
	
	/**
	 * skuValue
	 */
	private String skuValue = "";
	
	private String skuAdv = "";
	private String skuPicurl = "";


	/**
	 * 闪购 每单限购数量
	 */
	private int purchase_limit_order_num=Integer.valueOf(bConfig("homepool.skuMaxNum"));
	
	public String getSkuKey() {
		return skuKey;
	}


	public void setSkuKey(String skuKey) {
		this.skuKey = skuKey;
	}


	public String getSkuValue() {
		return skuValue;
	}


	public void setSkuValue(String skuValue) {
		this.skuValue = skuValue;
	}


	public String getProductCode() {
		return productCode;
	}


	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}


	public String getSkuAdv() {
		return skuAdv;
	}


	public void setSkuAdv(String skuAdv) {
		this.skuAdv = skuAdv;
	}


	public String getSkuPicurl() {
		return skuPicurl;
	}


	public void setSkuPicurl(String skuPicurl) {
		this.skuPicurl = skuPicurl;
	}


	public int getPurchase_limit_order_num() {
		return purchase_limit_order_num;
	}


	public void setPurchase_limit_order_num(int purchase_limit_order_num) {
		this.purchase_limit_order_num = purchase_limit_order_num;
	}
	
	
}
