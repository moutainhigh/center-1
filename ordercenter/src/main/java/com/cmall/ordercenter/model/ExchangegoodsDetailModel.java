package com.cmall.ordercenter.model;

import com.srnpr.zapcom.baseclass.BaseClass;

/**
 * 
 * 换货明细信息
 * @author gaoy
 *
 */
public class ExchangegoodsDetailModel extends BaseClass {
	
	/**
	 * 产品编号
	 */
//	private String  skuCode = "";
	
	/**
	 * 产品名称
	 */
//	private String skuName = "";
	
	/**
	 * 换货数量
	 */
	private int count = 0;
	
	/**
	 * 当前价格
	 */
//	private float currentPrice = 0;
	
	/**
	 * 产品流水号
	 * 
	 */
	private String serialNumber = ""; 
	
//	public String getSkuCode() {
//		return skuCode;
//	}
//
//	public void setSkuCode(String skuCode) {
//		this.skuCode = skuCode;
//	}
//
//	public String getSkuName() {
//		return skuName;
//	}
//
//	public void setSkuName(String skuName) {
//		this.skuName = skuName;
//	}
//
//	public float getCurrentPrice() {
//		return currentPrice;
//	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

//	public void setCurrentPrice(float currentPrice) {
//		this.currentPrice = currentPrice;
//	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	
}
