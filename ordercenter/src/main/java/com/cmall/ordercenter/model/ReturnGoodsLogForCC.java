package com.cmall.ordercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 退货流水 
 * @author:     zhaoxq 
 * project_name:ordercenter
 */
public class ReturnGoodsLogForCC{
	
	/**
	 * 退货单号
	 */
	@ZapcomApi(value="退货单号")
	private String returnNo  = "";
	
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
	@ZapcomApi(value="状态", remark="4497153900050001:通过审核(收货入库)<br/>"
			 +"4497153900050002:否决审核<br/>"
			 +"4497153900050003:待审核")
	private String status = "";

	public String getReturnNo() {
		return returnNo;
	}

	public void setReturnNo(String returnNo) {
		this.returnNo = returnNo;
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
