package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 活动-报名输入参数
 * @author yangrong
 * date:2014-08-05
 * @version 1.0
 * @param <ScordChange>
 *
 */
public class ActivityRegisterInput extends RootInput{
	
	@ZapcomApi(value="活动id",remark="活动id",demo="123456",require=1,verify="minlength=6")

	private String activity = "";

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	
}
