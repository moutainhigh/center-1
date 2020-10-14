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
public class UserPostActivityTableResult extends RootResultWeb{
	@ZapcomApi(value = "翻页结果")
	private PageResults paged = new PageResults();
	
	@ZapcomApi(value = "我发布的活动列表信息")
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
