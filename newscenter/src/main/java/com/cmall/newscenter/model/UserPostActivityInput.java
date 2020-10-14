package com.cmall.newscenter.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 用户 - 发布活动
 * @author liqiang
 * date 2014-7-23
 * @version 1.0
 */
public class UserPostActivityInput extends RootInput{
	
	@ZapcomApi(value = "活动信息")
	private Activity activity = new Activity();

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}
	
}
