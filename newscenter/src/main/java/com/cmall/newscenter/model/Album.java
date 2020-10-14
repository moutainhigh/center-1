package com.cmall.newscenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
/***
 * 相册
 * @author liqiang
 * date 2014-7-11
 * @version 1.0
 */
public class Album {

	@ZapcomApi(value = "限制条件")
	private CommentdityAppLimt limit= new CommentdityAppLimt();
	
	@ZapcomApi(value = "标题",remark="珠宝个人秀")
	private String  title = ""; 
	
	@ZapcomApi(value = "单图或多图")
	private List<CommentdityAppPhotos>  photos = new ArrayList<CommentdityAppPhotos>();
	/**
	 * shiyz
	 */
	@ZapcomApi(value = "内容编号")
	private String id = "";

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public CommentdityAppLimt getLimit() {
		return limit;
	}

	public void setLimit(CommentdityAppLimt limit) {
		this.limit = limit;
	}

	public List<CommentdityAppPhotos> getPhotos() {
		return photos;
	}

	public void setPhotos(List<CommentdityAppPhotos> photos) {
		this.photos = photos;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
