package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 资讯列表输入类
 * @author shiyz
 * date 2014-7-7
 * @version 1.0
 *
 */
public class InforMationFeedInput extends RootInput {

    @ZapcomApi(value = "翻页选项")
	private PageOption paging = new PageOption();
    
	public PageOption getPaging() {
		return paging;
	}

	public void setPaging(PageOption paging) {
		this.paging = paging;
	}
    
}
