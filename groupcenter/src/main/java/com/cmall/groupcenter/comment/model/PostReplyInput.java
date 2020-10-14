package com.cmall.groupcenter.comment.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 回复帖子输入类
 * @author LHY
 * 2015年4月27日 上午10:30:37
 */
public class PostReplyInput extends RootInput {

	@ZapcomApi(value="帖子ID",remark="帖子ID",demo="HML140630100001",require=1)
	private String post_code  = "";

	@ZapcomApi(value="正文",remark="正文",demo="XX化妆品太好用了",require=1)
	private String comment_content = "";
	
	public String getComment_content() {
		return comment_content;
	}

	public void setComment_content(String comment_content) {
		this.comment_content = comment_content;
	}

	public String getPost_code() {
		return post_code;
	}

	public void setPost_code(String post_code) {
		this.post_code = post_code;
	}
}