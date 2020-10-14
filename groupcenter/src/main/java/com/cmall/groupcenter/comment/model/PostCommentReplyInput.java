package com.cmall.groupcenter.comment.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class PostCommentReplyInput extends RootInput {
	
	@ZapcomApi(value="帖子ID",remark="帖子ID",demo="HML140630100001",require=1)
	private String post_code  = "";

	@ZapcomApi(value="评论ID",remark="评论ID",demo="HML140630100001",require=1)
	private String comment_code  = "";

	@ZapcomApi(value="正文",remark="正文",demo="XX化妆品太好用了",require=1, verify={"minlength=1", "maxlength=200"})
	private String comment_content = "";

	public String getComment_content() {
		return comment_content;
	}

	public void setComment_content(String comment_content) {
		this.comment_content = comment_content;
	}

	public String getComment_code() {
		return comment_code;
	}

	public void setComment_code(String comment_code) {
		this.comment_code = comment_code;
	}

	public String getPost_code() {
		return post_code;
	}

	public void setPost_code(String post_code) {
		this.post_code = post_code;
	}
}