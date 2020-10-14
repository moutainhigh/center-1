package com.cmall.newscenter.model;


import com.cmall.membercenter.model.ScoredChange;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 商品 - 收藏输出类
 * @author liqiang
 * date 2014-7-10
 * @version 1.0
 */
public class ProductUnFavResult extends RootResultWeb{
	
	@ZapcomApi(value = "我是否收藏过，0-否，1-是",remark="1")
	private String faved="";
	
	@ZapcomApi(value = "多少人收藏过",remark="1000")
	private int fav_count;
	
	@ZapcomApi(value= "获取积分")
	private ScoredChange scored = new ScoredChange();

	public String getFaved() {
		return faved;
	}

	public void setFaved(String faved) {
		this.faved = faved;
	}
	
	public int getFav_count() {
		return fav_count;
	}

	public void setFav_count(int fav_count) {
		this.fav_count = fav_count;
	}

	public ScoredChange getScored() {
		return scored;
	}

	public void setScored(ScoredChange scored) {
		this.scored = scored;
	}
	
}
