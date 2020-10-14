package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/***
 * 资讯发送评论输出类
 * @author shiyz
 * date 2014-7-4
 * @version 1.0
 */
public class InforMationAppSendCommentsResult extends RootResultWeb {

	
	@ZapcomApi(value = "评论结果")
	private  CommentdityApp comment = new CommentdityApp();


	public CommentdityApp getComment() {
		return comment;
	}

	public void setComment(CommentdityApp comment) {
		this.comment = comment;
	}
	
}
