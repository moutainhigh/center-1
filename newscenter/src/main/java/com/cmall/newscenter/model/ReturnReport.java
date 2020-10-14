package com.cmall.newscenter.model;


/**
 * 退货报表VO
 * @author lilei
 * 
 */
public class ReturnReport {
	//问题编号 excel字段不插入数据库
	private String problemCode;
	//订单编号(第三方订单编号) 后加字段 商品表
	private String thirdOrderCode;
	//外部平台单号(表内的订单编号) 商品表
	private String orderCode;
	//店铺名称(表内的商家编码) 商品表
	private String sellerCode;
	//产品编号(表内的商品编号) 商品明细表
	private String skuCode;
	//产品名称(表内的商品名称)商品明细表 
	private String skuName;
	//规格 excel字段 不插入数据库
	private String standard;
	//产品数量(表内的数量) 商品明细表
	private String count; 
	//成交单价(表内的当前价格) 商品明细表
	private String currentPrice;
	//退款金额 后加字段 商品明细表
	private String returnPrice;
	//处理进度 excel字段 不插入数据库
	private String handleSchedule;
	//退货状态(表内的状态) 商品表
	private String status;
	//退款状态 excel字段 不插入数据库
	private String refundState;
	//退款时间 (表内的创建时间) 商品表
	private String createTime;
	public String getProblemCode() {
		return problemCode;
	}
	public void setProblemCode(String problemCode) {
		this.problemCode = problemCode;
	}
	public String getThirdOrderCode() {
		return thirdOrderCode;
	}
	public void setThirdOrderCode(String thirdOrderCode) {
		this.thirdOrderCode = thirdOrderCode;
	}
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	public String getSellerCode() {
		return sellerCode;
	}
	public void setSellerCode(String sellerCode) {
		this.sellerCode = sellerCode;
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
	public String getStandard() {
		return standard;
	}
	public void setStandard(String standard) {
		this.standard = standard;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public String getCurrentPrice() {
		return currentPrice;
	}
	public void setCurrentPrice(String currentPrice) {
		this.currentPrice = currentPrice;
	}
	public String getReturnPrice() {
		return returnPrice;
	}
	public void setReturnPrice(String returnPrice) {
		this.returnPrice = returnPrice;
	}
	public String getHandleSchedule() {
		return handleSchedule;
	}
	public void setHandleSchedule(String handleSchedule) {
		this.handleSchedule = handleSchedule;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRefundState() {
		return refundState;
	}
	public void setRefundState(String refundState) {
		this.refundState = refundState;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
	
	
}
