package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 资讯详情输入类
 * @author shiyz
 * date 2014-10-21
 * @version 1.0
 *
 */
public class InforMationFeedDetailsInput extends RootInput {

    @ZapcomApi(value = "资讯ID",require=1,demo="JL10000")
	private String feed = "";

	public String getFeed() {
		return feed;
	}

	public void setFeed(String feed) {
		this.feed = feed;
	}
    
}
