package com.cmall.newscenter.model;

import com.cmall.membercenter.model.ScoredChange;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 活动-报名输出参数
 * @author yangrong
 * date:2014-08-05
 * @version 1.0
 * @param <ScordChange>
 *
 */
public class ActivityRegisterResult  extends RootResultWeb {
	
	@ZapcomApi(value = "获得积分")
	private ScoredChange scored = new ScoredChange();

	public ScoredChange getScored() {
		return scored;
	}

	public void setScored(ScoredChange scored) {
		this.scored = scored;
	}
}
