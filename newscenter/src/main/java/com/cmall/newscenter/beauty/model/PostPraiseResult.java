package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 帖子点赞输出类
 * @author houwen
 * date 2014-09-10
 * @version 1.0
 */
public class PostPraiseResult extends RootResultWeb {

	@ZapcomApi(value="点赞数")
	private int post_praise  ;

	public int getPost_praise() {
		return post_praise;
	}

	public void setPost_praise(int post_praise) {
		this.post_praise = post_praise;
	}
	
}
