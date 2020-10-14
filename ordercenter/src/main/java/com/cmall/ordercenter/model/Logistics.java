package com.cmall.ordercenter.model;


/** 
* @ClassName: Logistics 
* @Description: 订单物流相关信息
* @author 张海生
* @date 2015-12-23 下午5:04:30 
*  
*/
public class Logistics {
	
	/**
	 * 订单号
	 */
	private String orderCode="";
	
	/**
	 * 成交时间
	 */
	private String dealTime ="";
	
	/**
	 * 交易成功时间
	 */
	private String dealFinishTime ="";
	
	/**
	 * 买家手机号
	 */
	private String buyerMobile ="";
	
	/**
	 * 订单状态
	 */
	private String orderStatus ="";

	/**
	 * 订单金额
	 */
	private String orderTotalMoney ="";
	
	/**
	 * 已支付金额
	 */
	private String payMoney ="";
	
	/**
	 * 商品名称
	 */
	
	private String productName = "";
	
	/**
	 * 订购件数
	 */
	private String buyNum ="";
	
	/**
	 * 商品编号
	 */
	private String productCode ="";
	
	/**
	 * sku编号
	 */
	private String skuCode ="";
	
	/**
	 * 商品单价
	 */
	private String price ="";
	
	/**
	 * 收货人
	 */
	private String buyer ="";
	
	/**
	 * 收货人地址
	 */
	private String buyAddress ="";
	
	/**
	 * 物流公司
	 */
	private String logisticsCompany ="";
	
	/**
	 * 物流单号
	 */
	private String waybill ="";


	public String getDealTime() {
		return dealTime;
	}

	public void setDealTime(String dealTime) {
		this.dealTime = dealTime;
	}

	public String getDealFinishTime() {
		return dealFinishTime;
	}

	public void setDealFinishTime(String dealFinishTime) {
		this.dealFinishTime = dealFinishTime;
	}

	public String getBuyerMobile() {
		return buyerMobile;
	}

	public void setBuyerMobile(String buyerMobile) {
		this.buyerMobile = buyerMobile;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getOrderTotalMoney() {
		return orderTotalMoney;
	}

	public void setOrderTotalMoney(String orderTotalMoney) {
		this.orderTotalMoney = orderTotalMoney;
	}

	public String getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(String payMoney) {
		this.payMoney = payMoney;
	}

	public String getBuyNum() {
		return buyNum;
	}

	public void setBuyNum(String buyNum) {
		this.buyNum = buyNum;
	}

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

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getBuyer() {
		return buyer;
	}

	public void setBuyer(String buyer) {
		this.buyer = buyer;
	}

	public String getBuyAddress() {
		return buyAddress;
	}

	public void setBuyAddress(String buyAddress) {
		this.buyAddress = buyAddress;
	}

	public String getLogisticsCompany() {
		return logisticsCompany;
	}

	public void setLogisticsCompany(String logisticsCompany) {
		this.logisticsCompany = logisticsCompany;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getWaybill() {
		return waybill;
	}

	public void setWaybill(String waybill) {
		this.waybill = waybill;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}
	
}
