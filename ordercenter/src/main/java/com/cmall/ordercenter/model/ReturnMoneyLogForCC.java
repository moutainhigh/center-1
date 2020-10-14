package com.cmall.ordercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 退款流水 
 * @author:     zhaoxq 
 * project_name:ordercenter
 */
public class ReturnMoneyLogForCC{
	
	/**
	 * 退货单号
	 */
	@ZapcomApi(value="退货单号")
	private String returnMoneyNo  = "";
	
	/**
	 * 日志信息
	 */
	@ZapcomApi(value="日志信息")
	private String info = "";
	
	/**
	 * 创建时间
	 */
	@ZapcomApi(value="创建时间")
	private String createTime = "";
	
	/**
	 * 创建人
	 */
	@ZapcomApi(value="创建人")
	private String createUser = "";
	
	/**
	 * 状态
	 */
	@ZapcomApi(value="状态",remark="4497153900040001:已退款<br/>"
								 +"4497153900040002:否决审批<br/>"
								 +"4497153900040003:待退款")
	private String status = "";

	public String getReturnMoneyNo() {
		return returnMoneyNo;
	}

	public void setReturnMoneyNo(String returnMoneyNo) {
		this.returnMoneyNo = returnMoneyNo;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
