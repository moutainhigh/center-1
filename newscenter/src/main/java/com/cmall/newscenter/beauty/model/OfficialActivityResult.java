package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.newscenter.model.PageResults;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 官方活动输出类
 * @author yangrong
 * date: 2014-09-16
 * @version1.0
 */
public class OfficialActivityResult extends RootResultWeb {
	
	@ZapcomApi(value = "翻页结果")
	private PageResults paged = new PageResults();
	
	@ZapcomApi(value = "活动列表")
	private List<Activity> Activities = new ArrayList<Activity>();
	
	public PageResults getPaged() {
		return paged;
	}

	public void setPaged(PageResults paged) {
		this.paged = paged;
	}

	public List<Activity> getActivities() {
		return Activities;
	}

	public void setActivities(List<Activity> activities) {
		Activities = activities;
	}

}
