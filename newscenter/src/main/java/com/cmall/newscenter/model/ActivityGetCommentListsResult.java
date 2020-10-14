package com.cmall.newscenter.model;


import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 *活动- 获取评价列表输出类
 * @author liqiang
 * date 2014-7-4
 * @version 1.0
 */
public class ActivityGetCommentListsResult extends RootResultWeb{
	
	@ZapcomApi(value = "评论相关")
	private List<CommentdityApp> comments = new ArrayList<CommentdityApp>();

	@ZapcomApi(value = "翻页结果")
	private PageResults paged = new PageResults();

	public List<CommentdityApp> getComments() {
		return comments;
	}

	public void setComments(List<CommentdityApp> comments) {
		this.comments = comments;
	}

	public PageResults getPaged() {
		return paged;
	}

	public void setPaged(PageResults paged) {
		this.paged = paged;
	}

}
