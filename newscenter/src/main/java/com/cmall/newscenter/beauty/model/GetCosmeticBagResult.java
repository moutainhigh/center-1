package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.newscenter.model.PageResults;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 惠美丽—获取化妆包中的妆品输出类
 * 
 * @author yangrong date: 2015-01-25
 * @version1.3.2
 */
public class GetCosmeticBagResult extends RootResultWeb {

	@ZapcomApi(value = "化妆包中的妆品列表")
	private List<CosmeticBag> cosmetic = new ArrayList<CosmeticBag>();
	
	@ZapcomApi(value = "翻页结果")
	private PageResults paged = new PageResults();

	public List<CosmeticBag> getCosmetic() {
		return cosmetic;
	}

	public void setCosmetic(List<CosmeticBag> cosmetic) {
		this.cosmetic = cosmetic;
	}

	public PageResults getPaged() {
		return paged;
	}

	public void setPaged(PageResults paged) {
		this.paged = paged;
	}
	
	

}
