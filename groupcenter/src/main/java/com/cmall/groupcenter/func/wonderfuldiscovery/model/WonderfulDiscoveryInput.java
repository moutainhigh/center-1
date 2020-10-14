package com.cmall.groupcenter.func.wonderfuldiscovery.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 */
public class WonderfulDiscoveryInput extends RootInput {
	@ZapcomApi(value="是否隐藏当前app",remark="0为不隐藏，1为隐藏",require = 1)
	private String isHidden = "0";

	public String getIsHidden() {
		return isHidden;
	}

	public void setIsHidden(String isHidden) {
		this.isHidden = isHidden;
	}
	
}