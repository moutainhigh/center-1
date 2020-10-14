package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 活动 -详情输入类
 * @author yangrong
 * date 2014-8-22
 * @version 1.0
 */
public class ActivityInfoInput  extends RootInput{

	@ZapcomApi(value="活动编码",remark="活动编码",demo="1234567890",require=1,verify = "minlength=10")
	private String activity = "";

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}
	
	
}
