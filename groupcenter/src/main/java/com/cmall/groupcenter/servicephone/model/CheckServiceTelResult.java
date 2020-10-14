package com.cmall.groupcenter.servicephone.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class CheckServiceTelResult extends RootResultWeb{
	
	@ZapcomApi(value = "服务电话", remark = "服务电话")
	private String serviceTel = "";

	public String getServiceTel() {
		return serviceTel;
	}

	public void setServiceTel(String serviceTel) {
		this.serviceTel = serviceTel;
	}
	
	

}
