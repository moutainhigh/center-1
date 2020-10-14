package com.cmall.groupcenter.behavior.response;

import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 百分点登录响应信息
 * @author pang_jhui
 *
 */
public class BfdLoginResponse extends RootResultWeb {
	
	/*会话key*/
	private String sessionKey = "";

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}

}
