package com.cmall.productcenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiGetAnnounceInput   extends RootInput{
	
	@ZapcomApi(value="店铺公告ID",remark="")
	private String zid = "";

	public String getZid() {
		return zid;
	}

	public void setZid(String zid) {
		this.zid = zid;
	}
	
}
