package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 帖子列表输入类
 * @author houwen
 * date 2014-08-26
 * @version 1.0
 */
public class PostDetailListInput extends RootInput {

	@ZapcomApi(value="帖子ID",remark="帖子ID",demo="4497465000020001")
	private String post_code = "";

	@ZapcomApi(value="图片宽度")
	private  Integer  picWidth = 0 ;

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
