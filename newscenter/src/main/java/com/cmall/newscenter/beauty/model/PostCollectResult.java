package com.cmall.newscenter.beauty.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 帖子收藏输出类
 * @author houwen
 * date 2014-09-10
 * @version 1.0
 */
public class PostCollectResult extends RootResultWeb {

	@ZapcomApi(value="收藏数")
	private int post_collect  ;

	public int getPost_collect() {
		return post_collect;
	}

	public void setPost_collect(int post_collect) {
		this.post_collect = post_collect;
	}
}
