package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 防伪码输入类
 * @author shiyz
 * date 2014-09-20
 */
public class GetSecurityCodeInput extends RootInput {

	@ZapcomApi(value="防伪码",demo="@http://-",require=1)
	private String securityCode = "";
	@ZapcomApi(value="设备Id")
	private String equipmentId = "";
	
	public String getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}

	public String getEquipmentId() {
		return equipmentId;
	}

	public void setEquipmentId(String equipmentId) {
		this.equipmentId = equipmentId;
	}

}
