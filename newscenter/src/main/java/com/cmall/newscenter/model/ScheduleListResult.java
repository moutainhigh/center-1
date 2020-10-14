package com.cmall.newscenter.model;


import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 栏目-行程列表输出类
 * @author shiyz
 * date 2014-8-11
 * @version 1.0
 */
public class ScheduleListResult extends RootResultWeb{
	
	@ZapcomApi(value = "分类列表")
	private List<Schedule> schedules = new ArrayList<Schedule>();
	
	@ZapcomApi(value = "翻页结果")
	private PageResults paged = new PageResults();

	public List<Schedule> getSchedules() {
		return schedules;
	}

	public void setSchedules(List<Schedule> schedules) {
		this.schedules = schedules;
	}

	public PageResults getPaged() {
		return paged;
	}

	public void setPaged(PageResults paged) {
		this.paged = paged;
	}

}
