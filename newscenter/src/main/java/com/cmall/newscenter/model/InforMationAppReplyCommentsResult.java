package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/***
 * 资讯回复评论输出类
 * @author shiyz
 * date 2014-7-4
 * @version 1.0
 */
public class InforMationAppReplyCommentsResult extends RootResultWeb {

	
	@ZapcomApi(value = "评论结果")
	private CommentdityApp reply = new CommentdityApp();


	public CommentdityApp getReply() {
		return reply;
	}

	public void setReply(CommentdityApp reply) {
		this.reply = reply;
	}
	
}
