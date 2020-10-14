package com.cmall.newscenter.model;

import java.math.BigInteger;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 设置 - 第三方帐号，包括微公社
 * @author liqiang
 * date 2014-7-22
 * @version 1.0
 */
public class Account {

	@ZapcomApi(value="名称",demo="新浪微博")
	private String name="";

	@ZapcomApi(value="帐号类型")
	private BigInteger account_type = new BigInteger("0"); 
	
	@ZapcomApi(value="app_id",demo="XXXX")
	private String app_id="";

	@ZapcomApi(value="app_key",demo="XXXX")
	private String app_key="";
	
	@ZapcomApi(value="帐号过期时间",demo="2014/06/23 14:29:00")
	private String expires="";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public BigInteger getAccount_type() {
		return account_type;
	}

	public void setAccount_type(BigInteger account_type) {
		this.account_type = account_type;
	}

	public String getApp_id() {
		return app_id;
	}

	public void setApp_id(String app_id) {
		this.app_id = app_id;
	}

	public String getApp_key() {
		return app_key;
	}

	public void setApp_key(String app_key) {
		this.app_key = app_key;
	}

	public String getExpires() {
		return expires;
	}

	public void setExpires(String expires) {
		this.expires = expires;
	}
	
}
