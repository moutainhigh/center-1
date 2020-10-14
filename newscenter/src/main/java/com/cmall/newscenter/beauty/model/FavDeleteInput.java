package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 惠美丽—删除我的收藏输入类
 * @author yangrong
 * date: 2014-09-11
 * @version1.0
 */
public class FavDeleteInput extends RootInput {
	
	@ZapcomApi(value="删除的商品sku编码，中间以逗号分隔",demo="PL14100212132，PL37281379823",remark="不是全部删除的时候传入sku编码")
	private String ids ="";
	
	@ZapcomApi(value="是否全部删除",demo="1",require=1,remark="1为是   其他为否")
	private String isAll ="";

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getIsAll() {
		return isAll;
	}

	public void setIsAll(String isAll) {
		this.isAll = isAll;
	}
	
	
	

}
