package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 活动类
 * @author yangrong
 * date 2014-7-21
 * @version 1.0
 */
public class SecurityCode {

	@ZapcomApi(value="批次内序号")
	private String securityBatchnum = "";
	
	@ZapcomApi(value="防伪码")
	private String securityCode = "";

	public String getSecurityBatchnum() {
		return securityBatchnum;
	}

	public void setSecurityBatchnum(String securityBatchnum) {
		this.securityBatchnum = securityBatchnum;
	}

	public String getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}

}
