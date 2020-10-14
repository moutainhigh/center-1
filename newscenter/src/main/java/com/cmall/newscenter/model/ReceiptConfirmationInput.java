package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 防伪码输入类
 * @author shiyz
 * date 2014-09-20
 */
public class ReceiptConfirmationInput extends RootInput {

	@ZapcomApi(value="用户编号",require=1)
    private String agent_code = "";
	
	@ZapcomApi(value="二维码",require=1)
	private String securityCode = "";
	
	public String getAgent_code() {
		return agent_code;
	}

	public void setAgent_code(String agent_code) {
		this.agent_code = agent_code;
	}

	public String getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}
	
}
