package com.cmall.ordercenter.model;
/**
 * 和包支付属性
 * @author wz
 *
 */
public class UnionpayProcessInput {
	private String amount;    //订单金额 
	private String bankAbbr;   //银行代码
	private String currency;   //币种
	private String orderDate;   //订单提交日期
	private String orderId;     //商户订单号
	private String period;      //有效期数量
	private String periodUnit;  //有效期单位
	private String merchantAbbr;  //商户展示名称
	private String productDesc;   //商品描述
	private String productId;     //商品编号
	private String productName;   //商品名称
	private String productNum;    //商品数量
	private String reserved1;     //保留字段1
	private String reserved2;     //保留字段2
	private String userToken;     //用户标识
	private String payType;       
	private String showUrl;       //商品展示地址
	private String couponsFlag;   //营销工具使用控制
	
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getBankAbbr() {
		return bankAbbr;
	}
	public void setBankAbbr(String bankAbbr) {
		this.bankAbbr = bankAbbr;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public String getPeriodUnit() {
		return periodUnit;
	}
	public void setPeriodUnit(String periodUnit) {
		this.periodUnit = periodUnit;
	}
	public String getMerchantAbbr() {
		return merchantAbbr;
	}
	public void setMerchantAbbr(String merchantAbbr) {
		this.merchantAbbr = merchantAbbr;
	}
	public String getProductDesc() {
		return productDesc;
	}
	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductNum() {
		return productNum;
	}
	public void setProductNum(String productNum) {
		this.productNum = productNum;
	}
	public String getReserved1() {
		return reserved1;
	}
	public void setReserved1(String reserved1) {
		this.reserved1 = reserved1;
	}
	public String getReserved2() {
		return reserved2;
	}
	public void setReserved2(String reserved2) {
		this.reserved2 = reserved2;
	}
	public String getUserToken() {
		return userToken;
	}
	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public String getShowUrl() {
		return showUrl;
	}
	public void setShowUrl(String showUrl) {
		this.showUrl = showUrl;
	}
	public String getCouponsFlag() {
		return couponsFlag;
	}
	public void setCouponsFlag(String couponsFlag) {
		this.couponsFlag = couponsFlag;
	}
	
	
	
}
