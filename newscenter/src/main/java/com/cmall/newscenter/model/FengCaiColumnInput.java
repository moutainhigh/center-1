package com.cmall.newscenter.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 风采 - 栏目列表输入类
 * @author liqiang
 * date 2014-7-10
 * @version 1.0
 */
public class FengCaiColumnInput extends RootInput {
	/**
	 * @author yangrong
	 */
	@ZapcomApi(value = "翻页选项",remark = "输入起始页码和每页10条" ,demo= "5,10",require = 1)
	private PageOption paging = new PageOption();
	
	@ZapcomApi(value = "所属分类",remark = "所属分类" ,demo= "0")
	private String infoCategory = "";
	
	public PageOption getPaging() {
		return paging;
	}

	public void setPaging(PageOption paging) {
		this.paging = paging;
	}
    
	public String getInfoCategory() {
		return infoCategory;
	}

	public void setInfoCategory(String infoCategory) {
		this.infoCategory = infoCategory;
	}

	
}
