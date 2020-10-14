package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/***
 * 资讯回复评论输入类
 * @author shiyz
 * date 2014-7-4
 * @version 1.0
 */
public class InforMationAppReplyCommentsInput extends RootInput {

	@ZapcomApi(value="资讯id",remark="资讯id",demo="123456",require=1,verify="minlength=6")
	private String feed = "";
	
	@ZapcomApi(value="评论id",remark="评论id",demo="123456",require=1,verify="minlength=6")
	private String comment = "";
	
	@ZapcomApi(value="评论内容",remark="评论内容",demo="adfasdfasdf",require=1,verify={"minlength=10","maxlength=2000"})
	private String text = "";

	public String getFeed() {
		return feed;
	}

	public void setFeed(String feed) {
		this.feed = feed;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
}
