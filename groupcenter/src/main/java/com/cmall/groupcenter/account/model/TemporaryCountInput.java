package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class TemporaryCountInput extends RootInput{

	@ZapcomApi(value = "数据", demo = "数据", require = 0)
	String dataString="";

	public String getDataString() {
		return dataString;
	}

	public void setDataString(String dataString) {
		this.dataString = dataString;
	}
}
