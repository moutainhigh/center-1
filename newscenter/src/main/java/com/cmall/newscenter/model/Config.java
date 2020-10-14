package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 设置 - 用户相关配置，如通知开关，以及绑定帐号
 * @author liqiang
 * date 2014-7-22
 * @version 1.0
 */
public class Config {

	@ZapcomApi(value="通知是否打开,打开-1，关闭-0",demo="1")
	private int push=0;
	
	@ZapcomApi(value="已绑定帐号")
	private Account accounts = new Account();

	public int getPush() {
		return push;
	}

	public void setPush(int push) {
		this.push = push;
	}

	public Account getAccounts() {
		return accounts;
	}

	public void setAccounts(Account accounts) {
		this.accounts = accounts;
	}

	
}
