package com.cmall.newscenter.model;

import com.cmall.membercenter.model.ScoredChange;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 刘嘉玲APP资讯-喜欢输出参数
 * @author shiyz
 * date:2014-07-04
 * @version 1.0
 * @param <ScordChange>
 *
 */
public class InforMationLikeResult extends RootResultWeb {
	@ZapcomApi(value="资讯-喜欢标志",remark="已喜欢-1，未喜欢-0")
	private int liked ;
	
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

	public void setScored(ScoredChange scored) {
		this.scored = scored;
	}
	
}
