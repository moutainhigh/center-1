 package com.cmall.groupcenter.third.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class GroupSetRebateScaleResult extends RootResultWeb{

	@ZapcomApi(value = "返利编号",remark="返利编号，以后可使用返利编号进行操作")
	String rebateCode="";

	public String getRebateCode() {
		return rebateCode;
	}

	public void setRebateCode(String rebateCode) {
		this.rebateCode = rebateCode;
	}
	
}
