package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.newscenter.model.PageResults;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 帖子评论列表输出类
 * @author houwen
 * date 2014-08-26
 * @version 1.0
 */
public class PostCommentListResult extends RootResultWeb {

	@ZapcomApi(value = "帖子评论列表")
	private List<PostCommentList> postsCommentLists = new ArrayList<PostCommentList>();
	
	@ZapcomApi(value = "翻页结果")
	private PageResults paged = new PageResults();

	public List<PostCommentList> getPostsCommentLists() {
		return postsCommentLists;
	}

	public void setPostsCommentLists(List<PostCommentList> postsCommentLists) {
		this.postsCommentLists = postsCommentLists;
	}

	public PageResults getPaged() {
		return paged;
	}

	public void setPaged(PageResults paged) {
		this.paged = paged;
	}
	
}
