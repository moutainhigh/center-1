package com.cmall.productcenter.model.api;

import java.util.ArrayList;
import java.util.List;

import com.cmall.productcenter.model.HotWord;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 热门排行
 * @author zhouguohui
 * date 2014-11-21
 * @version 1.0
 */
public class ApiSearchHotwordResult extends RootResultWeb {
	
	@ZapcomApi(value = "排行")
	private List<HotWord> hotwordList=new ArrayList<HotWord>();

	/**
	 * @return the hotwordList
	 */
	public List<HotWord> getHotwordList() {
		return hotwordList;
	}

	/**
	 * @param hotwordList the hotwordList to set
	 */
	public void setHotwordList(List<HotWord> hotwordList) {
		this.hotwordList = hotwordList;
	}

	
	

}
