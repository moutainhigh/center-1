package com.cmall.ordercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;



/**
 * 换货信息  
 * @author zhaoxq
 *
 */
public class ExchangegoodsForCC{

	/**
	 * 换货单号
	 */
	@ZapcomApi(value="换货单号")
	private String exchangeNo = "";
	
	/**
	 * 订单编号
	 */
	@ZapcomApi(value="订单编号")
	private String orderCode = "";
	
	/**
	 * 注册手机号
	 */
	@ZapcomApi(value="注册手机号")
	private String registerMobile = "";
	
	/**
	 * 换货原因
	 */
	@ZapcomApi(value="换货原因")
	private String exchangeReason = "";
	
	/**
	 * 换货描述
	 */
	@ZapcomApi(value="换货描述")
	private String description = "";
	
	/**
	 * 换货状态
	 */
	@ZapcomApi(value="换货状态",remark="4497153900020002:待审核<br/>"
									+"4497153900020003:审核失败<br/>"
									+"4497153900020004:通过审核")
	private String status = "";
	
	/**
	 * 运费承担方
	 */
	@ZapcomApi(value="运费承担方",remark="449747390001:商户<br/>"
			                          +"449747390002:客户")
	private String transportPeople = "";
	
	/**
	 * 卖家编号
	 */
	@ZapcomApi(value="卖家编号")
	private String sellerCode = "";
	
	/**
	 * 卖家名称
	 */
	@ZapcomApi(value="卖家名称")
	private String sellerName = "";
	
	/**
	 * 创建时间
	 */
	@ZapcomApi(value="创建时间")
	private String createTime = "";
	
	/**
	 * 收货人
	 */
	@ZapcomApi(value="收货人")
	private String contacts = "";
	
	/**
	 * 收货人电话
	 */
	@ZapcomApi(value="收货人电话")
	private String mobile = "";
	
	/**
	 * 收货地址
	 */
	@ZapcomApi(value="收货地址")
	private String address = "";

	public String getExchangeNo() {
		return exchangeNo;
	}

	public void setExchangeNo(String exchangeNo) {
		this.exchangeNo = exchangeNo;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getRegisterMobile() {
		return registerMobile;
	}

	public void setRegisterMobile(String registerMobile) {
		this.registerMobile = registerMobile;
	}

	public String getExchangeReason() {
		return exchangeReason;
	}

	public void setExchangeReason(String exchangeReason) {
		this.exchangeReason = exchangeReason;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTransportPeople() {
		return transportPeople;
	}

	public void setTransportPeople(String transportPeople) {
		this.transportPeople = transportPeople;
	}

	public String getSellerCode() {
		return sellerCode;
	}

	public void setSellerCode(String sellerCode) {
		this.sellerCode = sellerCode;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getContacts() {
		return contacts;
	}

	public void setContacts(String contacts) {
		this.contacts = contacts;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
