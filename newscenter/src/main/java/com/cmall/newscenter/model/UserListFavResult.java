package com.cmall.newscenter.model;


import java.util.ArrayList;
import java.util.List;

import org.aspectj.weaver.ArrayAnnotationValue;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 用户 - 我参与的活动
 * @author liqiang
 * date 2014-7-23
 * @version 1.0
 */
public class UserListFavResult extends RootResultWeb{
	
	@ZapcomApi(value = "翻页结果")
	private PageResults paged = new PageResults();

	@ZapcomApi(value = "资讯信息")
	private List<InforCollectionFeed> feeds = new ArrayList<InforCollectionFeed>();

	public PageResults getPaged() {
		return paged;
	}

	public void setPaged(PageResults paged) {
		this.paged = paged;
	}

	public List<InforCollectionFeed> getFeeds() {
		return feeds;
	}

	public void setFeeds(List<InforCollectionFeed> feeds) {
		this.feeds = feeds;
	}


}
