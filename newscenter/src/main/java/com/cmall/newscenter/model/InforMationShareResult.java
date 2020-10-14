package com.cmall.newscenter.model;

import com.cmall.membercenter.model.ScoredChange;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 资讯分享输出类
 * @author shiyz
 * date 2014-7-4
 * @version 1.0
 */
public class InforMationShareResult extends RootResultWeb {

	@ZapcomApi(value = "获得积分")
	private ScoredChange scored = new ScoredChange();
	
	@ZapcomApi(value = "是否分享过")
	private int shared ;
	
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
