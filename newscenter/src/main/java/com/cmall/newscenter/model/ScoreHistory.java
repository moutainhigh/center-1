package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
/**
 * 积分记录类
 * @author liqiang
 * date 2014-7-23
 * @version 1.0
 */
public class ScoreHistory {

	@ZapcomApi(value = "动作",demo="评价")
	private String action="";

	@ZapcomApi(value = "参加了XXX活动",demo="目标")
	private String target="";

	@ZapcomApi(value = "+88",demo="积分变化")
	private String score="";

	@ZapcomApi(value = "2009/07/07 21:51:22",demo="创建时间")
	private String created_at="";

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	
}
