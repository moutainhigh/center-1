package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncRequest;

/**
 * 判断LD系统用户是否存在请求信息
 * @author 张海生
 *
 */
public class RsyncRequestRsyncCustExist implements IRsyncRequest {

	/**
	 * 调用子系统(现在统一传001)
	 */
	private String subsystem = "";
	
	/**
	 * 调用用户
	 */
	private String account = "";
	
	/**
	 * 调用密码
	 */
	private String password = "";
	
	/**
	 * 客户身份证号
	 */
	private String citi_no = "";
	
	/**
	 * 客户手机号
	 */
	private String mobile = "";

	public String getSubsystem() {
		return subsystem;
	}

	public void setSubsystem(String subsystem) {
		this.subsystem = subsystem;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCiti_no() {
		return citi_no;
	}

	public void setCiti_no(String citi_no) {
		this.citi_no = citi_no;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	
	
}
