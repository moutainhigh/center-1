package com.cmall.newscenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 粉丝头 - 我发布的活动列表输出类
 * 
 * @author gz date 2014-8-26
 * @version 1.0
 */
public class UserPostActivityTableDetailsResult extends RootResultWeb{
	
	
	@ZapcomApi(value = "我发布的活动列表信息")
	Activity activitie = new Activity();

	public Activity getActivitie() {
		return activitie;
	}

	public void setActivitie(Activity activitie) {
		this.activitie = activitie;
	}
	

}
