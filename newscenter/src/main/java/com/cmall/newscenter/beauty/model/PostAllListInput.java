package com.cmall.newscenter.beauty.model;

import com.cmall.newscenter.model.PageOption;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 帖子全部列表输入类
 * @author houwen
 * date 2014-08-26
 * @version 1.0
 */
public class PostAllListInput extends RootInput {


	@ZapcomApi(value = "翻页选项",remark = "输入起始页码和每页10条" ,demo= "5,10",require = 1)
	private PageOption paging = new PageOption();
	
	@ZapcomApi(value="图片宽度",remark = "图片宽度" ,demo= "500")
	private Integer picWidth = 0 ;
	
	@ZapcomApi(value="帖子分类",remark = "帖子分类" ,demo= "补水")
	private String post_category  = "" ;

	public PageOption getPaging() {
		return paging;
	}

	public void setPaging(PageOption paging) {
		this.paging = paging;
	}

	public Integer getPicWidth() {
		return picWidth;
	}

	public void setPicWidth(Integer picWidth) {
		this.picWidth = picWidth;
	}

	public String getPost_category() {
		return post_category;
	}

	public void setPost_category(String post_category) {
		this.post_category = post_category;
	}
}
