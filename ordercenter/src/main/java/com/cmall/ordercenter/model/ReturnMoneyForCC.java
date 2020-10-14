package com.cmall.ordercenter.model;


import java.math.BigDecimal;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 退款类 
 * @author:     zhaoxq 
 * project_name:ordercenter
 */
public class ReturnMoneyForCC{
	
	/**
	 * 退款单号
	 */
	@ZapcomApi(value="退款单号")
	private String returnMoneyCode = "";
	
	/**
	 * 注册手机号
	 */
	@ZapcomApi(value="注册手机号")
	private String registerMobile = "";
	
	/**
	 * 订单编号
	 */
	@ZapcomApi(value="订单编号")
	private String orderCode = "";
	
	/**
	 * 商家编码
	 */
	@ZapcomApi(value="商家编码")
	private String sellerCode = "";
	
	/**
	 * 商家名称
	 */
	@ZapcomApi(value="商家名称")
	private String sellerName = "";
	
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
	 * 退货状态
	 */
	@ZapcomApi(value="退货状态",remark="4497153900040001:已退款<br/>"
									+"4497153900040002:否决审批<br/>"
									+"4497153900040003:待退款")
	private String status = "";
	
	/**
	 * 退款金额
	 */
	@ZapcomApi(value="退款金额")
	private BigDecimal returnMoney = new BigDecimal(0.00);
	
	/**
	 * 创建时间
	 */
	@ZapcomApi(value="创建时间")
	private String createTime = "";

	public String getReturnMoneyCode() {
		return returnMoneyCode;
	}

	public void setReturnMoneyCode(String returnMoneyCode) {
		this.returnMoneyCode = returnMoneyCode;
	}

	public String getRegisterMobile() {
		return registerMobile;
	}

	public void setRegisterMobile(String registerMobile) {
		this.registerMobile = registerMobile;
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

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BigDecimal getReturnMoney() {
		return returnMoney;
	}

	public void setReturnMoney(BigDecimal returnMoney) {
		this.returnMoney = returnMoney;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
}
