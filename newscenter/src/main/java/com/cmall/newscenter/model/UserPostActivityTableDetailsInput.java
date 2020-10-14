package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 粉丝头 - 我发布的活动列表输入类
 * 
 * @author gz date 2014-8-26
 * @version 1.0
 */
public class UserPostActivityTableDetailsInput extends RootInput{
	@ZapcomApi(value = "活动编号",remark = "活动编号" ,demo= "JL100000",require = 1)
	private String activity = "";

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

}
