package com.cmall.groupcenter.comment.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.model.PageResults;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class PostCommentListResult extends RootResultWeb {
	@ZapcomApi(value="信息列表",remark="信息列表")
	private List<PostCommentList> list = new ArrayList<PostCommentList>();
	@ZapcomApi(value = "翻页结果")
	private PageResults paged = new PageResults();
	@ZapcomApi(value = "评论总人数")
	private String num;

	public List<PostCommentList> getList() {
		return list;
	}

	public void setList(List<PostCommentList> list) {
		this.list = list;
	}

	public PageResults getPaged() {
		return paged;
	}

	public void setPaged(PageResults paged) {
		this.paged = paged;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}
}