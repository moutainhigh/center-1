package com.cmall.newscenter.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 资讯详情输出类
 * @author shiyz
 * date 2014-10-21
 * @version 1.0
 */
public class InforMationFreedDetailsResult extends RootResultWeb {
 
	
	@ZapcomApi(value = "资讯信息")
	private InforMationFeed feed = new InforMationFeed();

	public InforMationFeed getFeed() {
		return feed;
	}

	public void setFeed(InforMationFeed feed) {
		this.feed = feed;
	}
	


}
