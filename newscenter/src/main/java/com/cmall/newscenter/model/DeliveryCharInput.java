package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 发货输入类
 * @author shiyz
 * date 2014-09-20
 */
public class DeliveryCharInput extends RootInput {

	@ZapcomApi(value="搜索",demo="张三")
	private String search = "";
	@ZapcomApi(value = "翻页选项",remark = "输入起始页码和每页10条" ,demo= "5,10",require = 1)
	private PageOption paging = new PageOption();
	public PageOption getPaging() {
		return paging;
	}
	public void setPaging(PageOption paging) {
		this.paging = paging;
	}
	public String getSearch() {
		return search;
	}
	public void setSearch(String search) {
		this.search = search;
	}
	
}
