package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 帖子列表输出类
 * @author houwen
 * date 2014-08-26
 * @version 1.0
 */
public class PostLabelListResult extends RootResultWeb {

	@ZapcomApi(value = "帖子标签列表")
	private List<PostLabelList> postlabel = new ArrayList<PostLabelList>();

	public List<PostLabelList> getPostlabel() {
		return postlabel;
	}

	public void setPostlabel(List<PostLabelList> postlabel) {
		this.postlabel = postlabel;
	}
}
