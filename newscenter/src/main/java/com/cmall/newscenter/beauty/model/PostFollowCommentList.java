package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 回复追帖列表类输出
 * @author houwen
 * date 2014-08-26
 * @version 1.0
 */
public class PostFollowCommentList {
	

	@ZapcomApi(value="评论人昵称")
	private String comment_nickname  = "";

	@ZapcomApi(value="被评论人昵称")
	private String publisher_nickname = "";

	@ZapcomApi(value="内容")
	private String comment_content  = "";

	public String getComment_nickname() {
		return comment_nickname;
	}

	public void setComment_nickname(String comment_nickname) {
		this.comment_nickname = comment_nickname;
	}

	public String getPublisher_nickname() {
		return publisher_nickname;
	}

	public void setPublisher_nickname(String publisher_nickname) {
		this.publisher_nickname = publisher_nickname;
	}

	public String getComment_content() {
		return comment_content;
	}

	public void setComment_content(String comment_content) {
		this.comment_content = comment_content;
	}

}
