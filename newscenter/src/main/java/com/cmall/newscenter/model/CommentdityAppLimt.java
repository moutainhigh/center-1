package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 积分限制条件类
 * @author shiyz
 * date 2014-07-04
 * @version 1.0
 *
 */
public class CommentdityAppLimt {

	@ZapcomApi(value = "获得积分")
	private int score = 0;

	@ZapcomApi(value = "积分单位")
	private String score_unit = "";

	@ZapcomApi(value = "等级编号")
	private String level = "";

	@ZapcomApi(value = "等级名称")
	private String level_name = "";

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getScore_unit() {
		return score_unit;
	}

	public void setScore_unit(String score_unit) {
		this.score_unit = score_unit;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getLevel_name() {
		return level_name;
	}

	public void setLevel_name(String level_name) {
		this.level_name = level_name;
	}

}
