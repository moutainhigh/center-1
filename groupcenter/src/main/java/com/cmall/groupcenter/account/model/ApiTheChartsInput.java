package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiTheChartsInput extends RootInput {
	@ZapcomApi(value = "日期",remark = "月份查询条件,最早不能早于2014-10" ,demo= "2014-10",require = 1)
	private String date = "2014-10";

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}