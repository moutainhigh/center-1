package com.cmall.newscenter.model;

import java.util.*;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/***
 * 资讯评价列表输出类
 * @author shiyz
 * date 2014-7-4
 * @version 1.0
 */
public class InforMationCommentResult extends RootResultWeb {

	@ZapcomApi(value = "翻页结果")
	private PageResults paged = new PageResults();
	
	@ZapcomApi(value = "评论相关")
	private List<CommentdityApp> comments = new ArrayList<CommentdityApp>();

	public PageResults getPaged() {
		return paged;
	}

	public void setPaged(PageResults paged) {
		this.paged = paged;
	}

	public List<CommentdityApp> getComments() {
		return comments;
	}

	public void setComments(List<CommentdityApp> comments) {
		this.comments = comments;
	}
	
}
