package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 资讯收藏输入类
 * @author shiyz	
 * date 2014-7-4
 * @version 1.0
 */
public class InforMationStoreInput extends RootInput {

	@ZapcomApi(value="资讯id",remark="资讯id",demo="123456",require=1,verify="minlength=6")
	private String feed = "";

	public String getFeed() {
		return feed;
	}

	public void setFeed(String feed) {
		this.feed = feed;
	}
	
}
