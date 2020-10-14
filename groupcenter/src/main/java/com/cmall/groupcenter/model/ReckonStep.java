package com.cmall.groupcenter.model;

public class ReckonStep {

	/**
	 * 流程编号
	 */
	private String stepCode = "";

	/**
	 * 订单编号
	 */
	private String orderCode = "";

	/**
	 * 订单所属账户编号
	 */
	private String accountCode = "";

	/**
	 * 是否成功
	 */
	private int flagSucces;

	/**
	 * 执行类型
	 */
	private String execType = "";

	/**
	 * 备注
	 */
	private String remark = "";
	
	/**
	 * 唯一约束
	 */
	private String uqcode = "";
	
	/**
	 * 创建时间
	 */
	private String createTime = "";

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUqcode() {
		return uqcode;
	}

	public void setUqcode(String uqcode) {
		this.uqcode = uqcode;
	}

	public String getStepCode() {
		return stepCode;
	}

	public void setStepCode(String stepCode) {
		this.stepCode = stepCode;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public int getFlagSucces() {
		return flagSucces;
	}

	public void setFlagSucces(int flagSucces) {
		this.flagSucces = flagSucces;
	}

	public String getExecType() {
		return execType;
	}

	public void setExecType(String execType) {
		this.execType = execType;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
