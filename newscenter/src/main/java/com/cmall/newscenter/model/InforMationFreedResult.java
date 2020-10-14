package com.cmall.newscenter.model;

import java.util.*;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 资讯列表输出类
 * @author shiyz
 * date 2014-7-7
 * @version 1.0
 */
public class InforMationFreedResult extends RootResultWeb {
 
	@ZapcomApi(value = "翻页结果")
	private PageResults paged = new PageResults();

	
	@ZapcomApi(value = "资讯信息")
	private List<InforMationFeed> feeds = new ArrayList<InforMationFeed>();
	
	public PageResults getPaged() {
		return paged;
	}

	public void setPaged(PageResults paged) {
		this.paged = paged;
	}

	public List<InforMationFeed> getFeeds() {
		return feeds;
	}

	public void setFeeds(List<InforMationFeed> feeds) {
		this.feeds = feeds;
	}

}
