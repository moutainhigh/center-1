package com.cmall.groupcenter.favorites.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.model.PageResults;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 *	帖子收藏输出类
 * @author yuwyn
 *
 */
public class ApiCollectionsListResult extends RootResultWeb{
 
	@ZapcomApi(value = "收藏帖子列表")
	private List<Collections> apiCollections = new ArrayList<Collections>();
	
	@ZapcomApi(value = "翻页结果")
	private PageResults paged = new PageResults();
	
	public List<Collections> getApiCollections(){
		return apiCollections;
	}

	public void setApiCollections(List<Collections> apiCollections) {
		this.apiCollections = apiCollections;
	}

	public PageResults getPaged() {
		return paged;
	}

	public void setPaged(PageResults paged) {
		this.paged = paged;
	}
}
