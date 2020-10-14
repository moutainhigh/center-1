package com.cmall.newscenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 活动 -报名列表输出类
 * @author yangrong
 * date 2014-8-21
 * @version 1.0
 */
public class ActivityApplyResult extends RootResultWeb{
	
	@ZapcomApi(value = "翻页结果")
	private PageResults paged = new PageResults();

	@ZapcomApi(value = "报名列表")
	private List<ApplyUser> users = new ArrayList<ApplyUser>();
	
	@ZapcomApi(value="报名人数")
	private int unread_count = 0;

	public List<ApplyUser> getUsers() {
		return users;
	}

	public void setUsers(List<ApplyUser> users) {
		this.users = users;
	}

	public PageResults getPaged() {
		return paged;
	}

	public void setPaged(PageResults paged) {
		this.paged = paged;
	}

	public int getUnread_count() {
		return unread_count;
	}

	public void setUnread_count(int unread_count) {
		this.unread_count = unread_count;
	}

	
}
