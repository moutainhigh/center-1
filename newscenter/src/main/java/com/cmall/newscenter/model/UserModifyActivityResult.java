package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;


/**
 * 用户 - 修改活动输出类
 * @author yangrong
 * date 2014-8-22
 * @version 1.0
 */
public class UserModifyActivityResult extends RootResultWeb{
	
	@ZapcomApi(value = "活动信息")
	private Activity activity = new Activity();

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}
	
	

}
