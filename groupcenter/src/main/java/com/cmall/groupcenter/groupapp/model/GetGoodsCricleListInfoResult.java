package com.cmall.groupcenter.groupapp.model;

import java.util.List;

import com.cmall.groupcenter.model.PageResults;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class GetGoodsCricleListInfoResult extends RootResultWeb {

	@ZapcomApi(value = "商品信息")
	List<GoodsCricleInfo> goodsContentList;
	
	@ZapcomApi(value="翻页结果")
	PageResults paged;

	
	public List<GoodsCricleInfo> getGoodsContentList() {
		return goodsContentList;
	}
	public void setGoodsContentList(List<GoodsCricleInfo> goodsContentList) {
		this.goodsContentList = goodsContentList;
	}
	public PageResults getPaged() {
		return paged;
	}
	public void setPaged(PageResults paged) {
		this.paged = paged;
	}
	
}
