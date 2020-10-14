package com.cmall.newscenter.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 活动- 发送评价输出类
 * @author liqiang
 * date 2014-7-4
 * @version 1.0
 */
public class ActivitySendCommentsResult extends RootResultWeb{
	
	
	@ZapcomApi(value = "评论结果")
	private  CommentdityApp comment = new CommentdityApp();


	public CommentdityApp getComment() {
		return comment;
	}

	public void setComment(CommentdityApp comment) {
		this.comment = comment;
	}

}
