package com.cmall.groupcenter.wallet.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class WalletAccountCheckInput extends RootInput{
	
	@ZapcomApi(value = "开始时间",remark = "2015-04-15 14:34:23",require = 1,verify = "base=datetime")
	private String startDate = "";
	
	@ZapcomApi(value = "结束时间",remark = "2015-04-15 14:34:23",require = 1,verify = "base=datetime")
	private String endDate = "";

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
	
	
}
