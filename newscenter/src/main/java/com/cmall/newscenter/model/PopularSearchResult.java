package com.cmall.newscenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 热门搜索
 * @author shiyz
 * date 2014-7-18
 * @version 1.0
 */
public class PopularSearchResult extends RootResultWeb {
	
	
	@ZapcomApi(value = "资讯信息")
	private List<InforMationFeed> feeds = new ArrayList<InforMationFeed>();

	public List<InforMationFeed> getFeeds() {
		return feeds;
	}

	public void setFeeds(List<InforMationFeed> feeds) {
		this.feeds = feeds;
	}
	
}
