package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 视频类
 * @author shiyz
 * date 2014-7-7
 * @version 1.0
 */
public class InforMationFeedVideo {

	@ZapcomApi(value = "限制条件")
	private  CommentdityAppLimt limit = new CommentdityAppLimt();
	
	@ZapcomApi(value = "封面宽")
	private String width = "";
	
	@ZapcomApi(value = "封面高")
	private String height = "";
	
	@ZapcomApi(value = "封装图")
	private String cover = "";
	
	@ZapcomApi(value = "高清地址")
	private String hd_url = "";
	
	@ZapcomApi(value = "普清地址")
	private String ld_url = "";

	public CommentdityAppLimt getLimit() {
		return limit;
	}

	public void setLimit(CommentdityAppLimt limit) {
		this.limit = limit;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public String getHd_url() {
		return hd_url;
	}

	public void setHd_url(String hd_url) {
		this.hd_url = hd_url;
	}

	public String getLd_url() {
		return ld_url;
	}

	public void setLd_url(String ld_url) {
		this.ld_url = ld_url;
	}
	
}
