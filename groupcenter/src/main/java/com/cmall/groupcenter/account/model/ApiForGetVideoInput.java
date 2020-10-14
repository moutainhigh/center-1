package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiForGetVideoInput extends RootInput {

	@ZapcomApi(value = "videoid", demo = "opNzEuDAYxCLh6Vcrn4TU3OjX-GA")
	public String videoid = "";
	private String operate="";

	public String getOperate() {
		return operate;
	}

	public void setOperate(String operate) {
		this.operate = operate;
	}

	public String getVideoid() {
		return videoid;
	}

	public void setVideoid(String videoid) {
		this.videoid = videoid;
	}
	
	

	
	
}
