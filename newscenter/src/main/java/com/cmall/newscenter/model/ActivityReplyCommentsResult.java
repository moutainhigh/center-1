package com.cmall.newscenter.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 活动- 回复评价输出类
 * @author liqiang
 * date 2014-7-4
 * @version 1.0
 */
public class ActivityReplyCommentsResult extends RootResultWeb{
	
	
	@ZapcomApi(value = "评论结果")
	private CommentdityApp reply = new CommentdityApp();

	public CommentdityApp getReply() {
		return reply;
	}

	public void setReply(CommentdityApp reply) {
		this.reply = reply;
	}

}
