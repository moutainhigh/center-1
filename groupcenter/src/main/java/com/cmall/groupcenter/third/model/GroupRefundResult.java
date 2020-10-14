package com.cmall.groupcenter.third.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class GroupRefundResult extends RootResultWeb{

	@ZapcomApi(value = "退款流水号",demo = "RFD14041700001")
	String refundCode="";

	public String getRefundCode() {
		return refundCode;
	}

	public void setRefundCode(String refundCode) {
		this.refundCode = refundCode;
	}
	
}
