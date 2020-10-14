package com.cmall.newscenter.model;


import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 用户 - 我参与的活动
 * @author liqiang
 * date 2014-7-23
 * @version 1.0
 */
public class UserListActivityResult extends RootResultWeb{
	
	@ZapcomApi(value = "翻页结果")
	private PageResults paged = new PageResults();

	@ZapcomApi(value = "活动信息")
	private List<Activity> activities = new ArrayList<Activity>();

	public PageResults getPaged() {
		return paged;
	}

	public void setPaged(PageResults paged) {
		this.paged = paged;
	}

	public List<Activity> getActivities() {
		return activities;
	}

	public void setActivities(List<Activity> activities) {
		this.activities = activities;
	}

}
