package com.cmall.ordercenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiGetOrdersForCCInput extends RootInput {

	/**
	 * 订单编号  
	 */
	@ZapcomApi(value="订单编号")
	private String orderCode = "";
	
	/**
	 * 外部订单编号
	 */
	@ZapcomApi(value="外部订单编号")
	private String outOrderCode = "";
	
	/**
	 * 支付订单编号
	 */
	@ZapcomApi(value="支付订单编号")
	private String bigOrderCode = "";
	
	/**
	 * 
	 * 订单状态 
	 */
	@ZapcomApi(value="订单状态")
	private String orderStatus = "";
	
	/**
	 * 
	 * 订单辅助状态
	 */
	@ZapcomApi(value="订单辅助状态")
	private String orderStatusExt = "";

	/**
	 * 
	 * 注册手机号
	 */
	@ZapcomApi(value="注册手机号")
	private String registerMobile = "";

	/**
	 * 
	 * 收货人
	 */
	@ZapcomApi(value="收货人")
	private String receivePerson = "";
	
	/**
	 * 
	 * 收货人手机号
	 */
	@ZapcomApi(value="收货人手机号")
	private String mobilephone = "";
	
	/**
	 * 创建时间开始
	 */
	@ZapcomApi(value="创建时间开始")
	private String createTimeStart = "";
	
	/**
	 * 创建时间结束
	 */
	@ZapcomApi(value="创建时间结束")
	private String createTimeEnd = "";
	
	/**
	 * 订单来源 
	 */
	@ZapcomApi(value="订单来源")
	private String orderSource = "";

	/**
	 * 商品名称
	 */
	@ZapcomApi(value="商品名称")
	private String productName = "";
	
	/**
	 * 收货地址
	 */
	@ZapcomApi(value="收货地址")
	private String address="";
	
	/**
	 * 物流单号
	 */
	@ZapcomApi(value="物流单号")
	private String waybill="";

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getOutOrderCode() {
		return outOrderCode;
	}

	public void setOutOrderCode(String outOrderCode) {
		this.outOrderCode = outOrderCode;
	}

	public String getBigOrderCode() {
		return bigOrderCode;
	}

	public void setBigOrderCode(String bigOrderCode) {
		this.bigOrderCode = bigOrderCode;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getOrderStatusExt() {
		return orderStatusExt;
	}

	public void setOrderStatusExt(String orderStatusExt) {
		this.orderStatusExt = orderStatusExt;
	}

	public String getRegisterMobile() {
		return registerMobile;
	}

	public void setRegisterMobile(String registerMobile) {
		this.registerMobile = registerMobile;
	}

	public String getReceivePerson() {
		return receivePerson;
	}

	public void setReceivePerson(String receivePerson) {
		this.receivePerson = receivePerson;
	}

	public String getMobilephone() {
		return mobilephone;
	}

	public void setMobilephone(String mobilephone) {
		this.mobilephone = mobilephone;
	}

	public String getCreateTimeStart() {
		return createTimeStart;
	}

	public void setCreateTimeStart(String createTimeStart) {
		this.createTimeStart = createTimeStart;
	}

	public String getCreateTimeEnd() {
		return createTimeEnd;
	}

	public void setCreateTimeEnd(String createTimeEnd) {
		this.createTimeEnd = createTimeEnd;
	}

	public String getOrderSource() {
		return orderSource;
	}

	public void setOrderSource(String orderSource) {
		this.orderSource = orderSource;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getWaybill() {
		return waybill;
	}

	public void setWaybill(String waybill) {
		this.waybill = waybill;
	}
}
