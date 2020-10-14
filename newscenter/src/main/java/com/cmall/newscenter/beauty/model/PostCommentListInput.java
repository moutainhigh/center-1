package com.cmall.newscenter.beauty.model;

import com.cmall.newscenter.model.PageOption;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 帖子列表输入类
 * @author houwen
 * date 2014-08-26
 * @version 1.0
 */
public class PostCommentListInput extends RootInput {

	@ZapcomApi(value="帖子ID",remark="帖子ID",demo="4497465000020001")
	private String post_code = "";
	
	@ZapcomApi(value="图片宽度")
	private  Integer picWidth = 0 ;

	@ZapcomApi(value = "翻页选项",remark = "输入起始页码和每页10条" ,demo= "5,10",require = 1)
	private PageOption paging = new PageOption();

	public PageOption getPaging() {
		return paging;
	}

	public void setPaging(PageOption paging) {
		this.paging = paging;
	}

	public String getPost_code() {
		return post_code;
	}

	public void setPost_code(String post_code) {
		this.post_code = post_code;
	}

	public Integer getPicWidth() {
		return picWidth;
	}

	public void setPicWidth(Integer picWidth) {
		this.picWidth = picWidth;
	}

}
