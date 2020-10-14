package com.cmall.newscenter.model;

import com.cmall.membercenter.model.ScoredChange;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 活动分享输出类
 * @author yangrong
 * date 2014-8-5
 * @version 1.0
 */
public class ActivityShareResult extends RootResultWeb{

	@ZapcomApi(value = "获得积分")
	private ScoredChange scored = new ScoredChange();
	
	@ZapcomApi(value = "是否分享过")
	private int shared = 0;
	
	@ZapcomApi(value = "分享人数")
	private int   share_count = 0;

	public ScoredChange getScored() {
		return scored;
	}

	public void setScored(ScoredChange scored) {
		this.scored = scored;
	}

	public int getShared() {
		return shared;
	}

	public void setShared(int shared) {
		this.shared = shared;
	}

	public int getShare_count() {
		return share_count;
	}

	public void setShare_count(int share_count) {
		this.share_count = share_count;
	}
}
