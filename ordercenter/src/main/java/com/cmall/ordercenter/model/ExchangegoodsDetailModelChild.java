package com.cmall.ordercenter.model;

import com.srnpr.zapcom.baseclass.BaseClass;

/**
 * 
 * 换货明细信息
 * @author yang
 *
 */
public class ExchangegoodsDetailModelChild extends BaseClass{

	/**
	 * 换货单编号
	 */
	private String exchangeNo = "";
	
	/**
	 * 产品编号
	 */
	private String  skuCode = "";
	
	/**
	 * 产品名称
	 */
	private String skuName = "";
	
	/**
	 * 换货数量
	 */
	private int count = 0 ;
	
	/**
	 * 当前价格
	 */
	private float currentPrice = 0;
	
	/**
	 * 流水号
	 */
	private String  serialNumber="";
	
	/**
	 * 产品图
	 */
	private String productPicurl = "";
	
	/**
	 * 换货产品状态
	 */
	private String skuStatus = "";
	
	public String getExchangeNo() {
		return exchangeNo;
	}

	public void setExchangeNo(String exchangeNo) {
		this.exchangeNo = exchangeNo;
	}
	
	public String getSkuCode() {
		return skuCode;
	}

	public void setSkuCode(String skuCode) {
		this.skuCode = skuCode;
	}

	public String getSkuName() {
		return skuName;
	}

	public void setSkuName(String skuName) {
		this.skuName = skuName;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public float getCurrentPrice() {
		return currentPrice;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public void setCurrentPrice(float currentPrice) {
		this.currentPrice = currentPrice;
	}

	public String getProductPicurl() {
		return productPicurl;
	}

	public void setProductPicurl(String productPicurl) {
		this.productPicurl = productPicurl;
	}

	public String getSkuStatus() {
		return skuStatus;
	}

	public void setSkuStatus(String skuStatus) {
		this.skuStatus = skuStatus;
	}

}
