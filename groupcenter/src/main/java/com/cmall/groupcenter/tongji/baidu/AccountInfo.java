package com.cmall.groupcenter.tongji.baidu;

public class AccountInfo {

	/** 登录账户 */
	private String username;
	/** 登录密码 */
	private String password;
	/** 申请获取到的token */
	private String token;
	/** 账户类型，如  ZhanZhang:1,FengChao:2,Union:3,Columbus:4 */
	private String accountType = "1";
	/** 账户id，从百度统计后台获得 */
	private String userId = "";
	/** 设备唯一标识 */
	private String uuid = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBg-1";
	
	public AccountInfo(String username, String password, String token, String userId) {
		super();
		this.username = username;
		this.password = password;
		this.token = token;
		this.userId = userId;
	}
	
	public AccountInfo(String username, String password, String token) {
		super();
		this.username = username;
		this.password = password;
		this.token = token;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	
}
