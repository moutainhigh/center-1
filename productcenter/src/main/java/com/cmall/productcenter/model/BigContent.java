package com.cmall.productcenter.model;

import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class BigContent {

	@ZapcomApi(value = "大标题",  remark = "大标题")
	private String title = "";

	@ZapcomApi(value = "标题下的内容明细",  remark = "标题下的内容明细")
	private List<DetailContent> contents;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<DetailContent> getContents() {
		return contents;
	}

	public void setContents(List<DetailContent> contents) {
		this.contents = contents;
	}

	
}
