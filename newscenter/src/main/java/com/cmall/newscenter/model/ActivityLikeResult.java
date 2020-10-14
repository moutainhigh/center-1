package com.cmall.newscenter.model;

import com.cmall.membercenter.model.ScoredChange;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 活动-喜欢输出参数
 * @author yangrong
 * date:2014-08-05
 * @version 1.0
 * @param <ScordChange>
 *
 */
public class ActivityLikeResult extends RootResultWeb {
	
	@ZapcomApi(value="活动-喜欢标志",remark="已喜欢-1，未喜欢-0")
	private int liked = 0;
	
	@ZapcomApi(value="多少人喜欢过")
	private int like_count = 0;
	
	@ZapcomApi(value = "获得积分")
	private ScoredChange scored = new ScoredChange();

	
	public int getLiked() {
		return liked;
	}

	public void setLiked(int liked) {
		this.liked = liked;
	}

	public ScoredChange getScored() {
		return scored;
	}
	
	public int getLike_count() {
		return like_count;
	}

	public void setLike_count(int like_count) {
		this.like_count = like_count;
	}

	public void setScored(ScoredChange scored) {
		this.scored = scored;
	}
}
