package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class Product_Category {

	@ZapcomApi(value = "分类Id",remark="100")
	private String id="";
	
	@ZapcomApi(value = "名称",remark="分类名称")
	private String name="";
	
	@ZapcomApi(value = "图标")
	private CommentdityAppPhotos icon = new CommentdityAppPhotos();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CommentdityAppPhotos getIcon() {
		return icon;
	}

	public void setIcon(CommentdityAppPhotos icon) {
		this.icon = icon;
	}
	
	
}
