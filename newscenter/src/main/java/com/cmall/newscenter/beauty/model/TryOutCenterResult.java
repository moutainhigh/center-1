package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 试用中心输出类
 * @author yangrong
 * date: 2014-09-16
 * @version1.0
 */
public class TryOutCenterResult extends RootResultWeb{
	
	@ZapcomApi(value = "试用商品LIST")
	private List<TryOutGood> TryOutGoods = new ArrayList<TryOutGood>();

	/*@ZapcomApi(value = "翻页结果")
	private PageResults paged = new PageResults();*/
	
	public List<TryOutGood> getTryOutGoods() {
		return TryOutGoods;
	}

	public void setTryOutGoods(List<TryOutGood> tryOutGoods) {
		TryOutGoods = tryOutGoods;
	}
}
