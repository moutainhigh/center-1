package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class BeautyReportInput extends RootInput {

	@ZapcomApi(value="帖子ID",demo="123456",remark="123456")
	String post_code = "";
	
	@ZapcomApi(value="评论编号",demo="123456",remark="123456")
	String comment_code = "";

	public String getPost_code() {
		return post_code;
	}

	public void setPost_code(String post_code) {
		this.post_code = post_code;
	}

	public String getComment_code() {
		return comment_code;
	}

	public void setComment_code(String comment_code) {
		this.comment_code = comment_code;
	}
	
	
}
