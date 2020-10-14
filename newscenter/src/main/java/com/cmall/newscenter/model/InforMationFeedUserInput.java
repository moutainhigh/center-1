package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 用户-资讯列表输入类
 * @author shiyz
 * date 2014-7-16
 * @version 1.0
 */
public class InforMationFeedUserInput extends RootInput {
	
	@ZapcomApi(value = "资讯信息")
	private  InforMationFeed feed = new InforMationFeed();

	public InforMationFeed getFeed() {
		return feed;
	}

	public void setFeed(InforMationFeed feed) {
		this.feed = feed;
	}

}
