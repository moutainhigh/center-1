package com.cmall.newscenter.model;

import com.cmall.membercenter.model.ScoredChange;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 资讯收藏输出类
 * @author shiyz	
 * date 2014-7-4
 * @version 1.0
 */
public class InforMationStoreResult extends RootResultWeb {

	@ZapcomApi(value = "获得积分")
	private ScoredChange scored = new ScoredChange();
	
	@ZapcomApi(value = "是否收藏过",remark = "0-否，1-是")
	private int faved ;
	
	@ZapcomApi(value = "多少人收藏过")
	private int fav_count = 0;

	public ScoredChange getScored() {
		return scored;
	}

	public void setScored(ScoredChange scored) {
		this.scored = scored;
	}


	public int getFaved() {
		return faved;
	}

	public void setFaved(int faved) {
		this.faved = faved;
	}

	public int getFav_count() {
		return fav_count;
	}

	public void setFav_count(int fav_count) {
		this.fav_count = fav_count;
	}
	
}
