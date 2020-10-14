package com.cmall.newscenter.model;


import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 用户 - 积分记录
 * @author liqiang
 * date 2014-7-23
 * @version 1.0
 */
public class UserListScoreResult extends RootResultWeb{
	
	@ZapcomApi(value = "翻页结果")
	private PageResults paged = new PageResults();

	@ZapcomApi(value = "积分记录")
	private List<ScoreHistory> history = new ArrayList<ScoreHistory>();

	public PageResults getPaged() {
		return paged;
	}

	public void setPaged(PageResults paged) {
		this.paged = paged;
	}

	public List<ScoreHistory> getHistory() {
		return history;
	}

	public void setHistory(List<ScoreHistory> history) {
		this.history = history;
	}
}
