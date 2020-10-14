package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.newscenter.model.PageResults;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 购物车列表输出类
 * @author yangrong
 * date: 2014-09-16
 * @version1.0
 */
public class ShoppingCarListResult extends RootResultWeb {
	
	@ZapcomApi(value = "翻页结果")
	private PageResults paged = new PageResults();
	
	@ZapcomApi(value = "购物车商品List")
	private List<ShoppingCarGood> goods = new ArrayList<ShoppingCarGood>();
	
	@ZapcomApi(value = "有货商品数目")
	private String count = "";

	public PageResults getPaged() {
		return paged;
	}

	public void setPaged(PageResults paged) {
		this.paged = paged;
	}

	public List<ShoppingCarGood> getGoods() {
		return goods;
	}

	public void setGoods(List<ShoppingCarGood> goods) {
		this.goods = goods;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}
	
	

}
