package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 帖子点赞输入类
 * @author houwen
 * date 2014-09-10
 * @version 1.0
 */
public class CommentPraiseInput extends RootInput {


	@ZapcomApi(value="评论Id",remark="评论Id",demo="112222",require=1)
	private String comment_code = "";

	public String getComment_code() {
		return comment_code;
	}

	public void setComment_code(String comment_code) {
		this.comment_code = comment_code;
	}
	
}
