package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class SetPushInput extends RootInput {

	@ZapcomApi(value = "是否推送", remark = "449747100001：推送  449747100002：不推送", demo = "449747100001" )
	private String isSend = "";
	public String getIsSend() {
		return isSend;
	}
	public void setIsSend(String isSend) {
		this.isSend = isSend;
	}

	

}
