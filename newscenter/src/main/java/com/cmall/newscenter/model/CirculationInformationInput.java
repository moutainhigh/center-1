package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 商品流通输入类
 * @author shiyz
 * date 2016-03-20
 */
public class CirculationInformationInput extends RootInput {

	@ZapcomApi(value="二维码",demo="@http://-",require=1)
	private String securityCode = "";

	public String getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}
}
