package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncResponse;

/**
 * 判断LD系统用户是否存在响应
 * 
 * @author 张海生
 * 
 */
public class RsyncResponseRsyncCustExist implements IRsyncResponse {

	/*
	 * 成功返回true
	 */
	private boolean success;
	/*
	 * 成功返回true
	 */
	private String message;

	/*
	 * 客户id
	 */
	private String custId;
	/*
	 * 客户身份证号
	 */
	private String citiNo;
	/*
	 * 手机号
	 */
	private String tel;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public String getCitiNo() {
		return citiNo;
	}

	public void setCitiNo(String citiNo) {
		this.citiNo = citiNo;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

}
