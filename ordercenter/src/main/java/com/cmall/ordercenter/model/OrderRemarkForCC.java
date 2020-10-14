package com.cmall.ordercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 订单备注  
 * @author zhaoxq
 *
 */
public class OrderRemarkForCC{
	
	/**
	 * 订单编号
	 */
	@ZapcomApi(value="订单编号")
	private String orderCode = "";
	
	/**
	 * 备注内容
	 */
	@ZapcomApi(value="备注内容")
	private String remark = "";
	
	/**
	 * 创建时间
	 */
	@ZapcomApi(value="创建时间")
	private String createTime ="";
	
	/**
	 * 创建人
	 */
	@ZapcomApi(value="创建人")
	private String createUserName = "";
	
	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
}
