package com.cmall.groupcenter.behavior.request;

import com.cmall.groupcenter.behavior.face.IBfdRequest;

/**
 * 百分点请求信息
 * @author pang_jhui
 *
 */
public class BfdLoginRequest implements IBfdRequest {
	
	/*客户Id*/
	private String cid = "";
	
	/*密码*/
	private String pwd = "";

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

}
