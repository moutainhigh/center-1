package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 资讯评价列表输入类
 * @author shiyz	
 * date 2014-7-4
 * @version 1.0
 */
public class InforMationCommentInput extends RootInput {

	@ZapcomApi(value = "翻页选项",remark = "输入起始页码和每页10条" ,demo= "5,10",require = 1)
	private PageOption paging = new PageOption();
	@ZapcomApi(value = "内容编号",remark = "内容编号",demo = "JL140721900005",require = 1)
	private String feed = "";

	public PageOption getPaging() {
		return paging;
	}

	public void setPaging(PageOption paging) {
		this.paging = paging;
	}

	public String getFeed() {
		return feed;
	}

	public void setFeed(String feed) {
		this.feed = feed;
	}
	
}
