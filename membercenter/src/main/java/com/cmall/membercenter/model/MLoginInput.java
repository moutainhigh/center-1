package com.cmall.membercenter.model;

import com.cmall.membercenter.enumer.ELoginType;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 用户注册输入参数
 * 
 * @author srnpr
 * 
 */
public class MLoginInput extends RootInput {

	/**
	 * 登陆名称
	 */
	private String loginName = "";

	/**
	 * 登陆密码
	 */
	private String loginPassword = "";

	/**
	 * 应用编号
	 */
	private String manageCode = "";

	/**
	 * 登陆信息分组
	 */
	private String loginGroup = "";
	
	/**
	 * 流水号
	 */
	private String serialNumber = "";

	/**
	 * 登陆类型 默认为用户密码登陆
	 */
	private ELoginType loginType = ELoginType.Password;
	
	/**
	 * 
	 * 是否需要立即登陆操作
	 */

	private int isInsideLogin = 1;
	
	
	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getLoginPassword() {
		return loginPassword;
	}

	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}

	public String getManageCode() {
		return manageCode;
	}

	public void setManageCode(String manageCode) {
		this.manageCode = manageCode;
	}

	public String getLoginGroup() {
		return loginGroup;
	}

	public void setLoginGroup(String loginGroup) {
		this.loginGroup = loginGroup;
	}

	public ELoginType getLoginType() {
		return loginType;
	}

	public void setLoginType(ELoginType loginType) {
		this.loginType = loginType;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public boolean isInsideLogin() {
		return this.isInsideLogin== 1;
	}

	public void setIsInsideLogin(int isInsideLogin) {
		this.isInsideLogin = isInsideLogin;
	}
}
