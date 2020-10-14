package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.newscenter.model.PageResults;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 我的试用输出类
 * @author houwen	
 * date: 2014-10-13
 * @version1.0
 */
public class MyTryOutCenterResult extends RootResultWeb{
	
	@ZapcomApi(value = "试用商品LIST")
	private List<MyTryOutGood> TryOutGoods = new ArrayList<MyTryOutGood>();

	@ZapcomApi(value = "翻页结果")
	private PageResults paged = new PageResults();

	public List<MyTryOutGood> getTryOutGoods() {
		return TryOutGoods;
	}

	public void setTryOutGoods(List<MyTryOutGood> tryOutGoods) {
		TryOutGoods = tryOutGoods;
	}

	public PageResults getPaged() {
		return paged;
	}

	public void setPaged(PageResults paged) {
		this.paged = paged;
	}
	
	
}
