package com.cmall.newscenter.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 用户 - 发布资讯
 * @author liqiang
 * date 2014-7-23
 * @version 1.0
 */
public class UserPostFeedResult extends RootResultWeb{
	
	@ZapcomApi(value = "资讯信息")
	private InforMationFeed feed = new InforMationFeed();

	public InforMationFeed getFeed() {
		return feed;
	}

	public void setFeed(InforMationFeed feed) {
		this.feed = feed;
	}
	
}
