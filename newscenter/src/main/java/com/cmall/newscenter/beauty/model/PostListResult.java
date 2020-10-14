package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.newscenter.model.PageResults;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 帖子列表输出类
 * @author houwen
 * date 2014-08-26
 * @version 1.0
 */
public class PostListResult extends RootResultWeb {

	@ZapcomApi(value = "帖子列表")
	private List<PostsList> posts = new ArrayList<PostsList>();
	
	@ZapcomApi(value = "翻页结果")
	private PageResults paged = new PageResults();
	
	public List<PostsList> getPosts() {
		return posts;
	}

	public void setPosts(List<PostsList> posts) {
		this.posts = posts;
	}

	public PageResults getPaged() {
		return paged;
	}

	public void setPaged(PageResults paged) {
		this.paged = paged;
	}


}
