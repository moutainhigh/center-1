package com.cmall.groupcenter.third.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class GroupUpdateRebateScaleInput extends RootInput{

	@ZapcomApi(value = "返利编号",remark = "返利编号", require = 1)
	String rebateCode="";

	public String getRebateCode() {
		return rebateCode;
	}

	public void setRebateCode(String rebateCode) {
		this.rebateCode = rebateCode;
	}
	
}
