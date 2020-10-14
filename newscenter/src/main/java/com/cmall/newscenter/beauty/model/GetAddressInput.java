package com.cmall.newscenter.beauty.model;

import com.cmall.newscenter.model.PageOption;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 惠美丽-获取收货地址输入类
 * @author yangrong
 * date 2014-8-20
 * @version 1.0
 */
public class GetAddressInput extends RootInput {
	
	@ZapcomApi(value = "分页")
	private PageOption paging = new PageOption();

	public PageOption getPaging() {
		return paging;
	}

	public void setPaging(PageOption paging) {
		this.paging = paging;
	}
	
	
}
