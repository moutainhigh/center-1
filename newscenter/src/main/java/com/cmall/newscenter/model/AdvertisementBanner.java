package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 广告类
 * @author shiyz
 * date 2014-7-7
 * @version 1.0
 */
public class AdvertisementBanner {
	
	@ZapcomApi(value = "品牌故事")
	private String title = "";
	
	@ZapcomApi(value = "封面图片")
	private CommentdityAppPhotos photo = new CommentdityAppPhotos();
	
	@ZapcomApi(value = "链接地址")
	private String url = "";
	
	@ZapcomApi(value = "创建时间")
	private String created_at = "";
	
	@ZapcomApi(value="类型")
	private int urlType = 0;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public CommentdityAppPhotos getPhoto() {
		return photo;
	}

	public void setPhoto(CommentdityAppPhotos photo) {
		this.photo = photo;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public int getUrlType() {
		return urlType;
	}

	public void setUrlType(int urlType) {
		this.urlType = urlType;
	}


}
