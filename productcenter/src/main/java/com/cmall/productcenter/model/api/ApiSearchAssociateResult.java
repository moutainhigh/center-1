package com.cmall.productcenter.model.api;

import java.util.ArrayList;
import java.util.List;

import com.cmall.productcenter.model.SearchAssociate;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 联想搜索结果词及数量
 * @author zhouguohui
 *
 */
public class ApiSearchAssociateResult extends RootResultWeb {
	@ZapcomApi(value="联想结果")
	private List<SearchAssociate> searchList = new ArrayList<SearchAssociate>();
	/**
	 * @return the searchList
	 */
	public List<SearchAssociate> getSearchList() {
		return searchList;
	}

	/**
	 * @param searchList the searchList to set
	 */
	public void setSearchList(List<SearchAssociate> searchList) {
		this.searchList = searchList;
	}

}
