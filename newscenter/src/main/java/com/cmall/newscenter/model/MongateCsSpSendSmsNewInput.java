package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class MongateCsSpSendSmsNewInput extends RootInput {

	@ZapcomApi(value="用户编号")
	String userId = "";
	
	@ZapcomApi(value="用户名")
	String password = "";
	
	@ZapcomApi(value="手机号")
	String pszMobis = "";
	
	@ZapcomApi(value="输入内容",verify = { "maxlength=700" } )
	String pszMsg = "";
	
	@ZapcomApi(value="手机数量")
	int iMobiCount = 0;
	
	@ZapcomApi(value="子端口")
	String pszSubPort = "";

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPszMobis() {
		return pszMobis;
	}

	public void setPszMobis(String pszMobis) {
		this.pszMobis = pszMobis;
	}

	public String getPszMsg() {
		return pszMsg;
	}

	public void setPszMsg(String pszMsg) {
		this.pszMsg = pszMsg;
	}

	public int getiMobiCount() {
		return iMobiCount;
	}

	public void setiMobiCount(int iMobiCount) {
		this.iMobiCount = iMobiCount;
	}

	public String getPszSubPort() {
		return pszSubPort;
	}

	public void setPszSubPort(String pszSubPort) {
		this.pszSubPort = pszSubPort;
	}
	
	
}