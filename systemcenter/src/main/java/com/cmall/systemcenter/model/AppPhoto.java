package com.cmall.systemcenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * APP的图片显示
 * 
 * @author srnpr
 * 
 */
public class AppPhoto {

	@ZapcomApi(value = "原图")
	private String large = "";
	@ZapcomApi(value = "缩略图")
	private String thumb = "";
	
	public String getLarge() {
		return large;
	}
	public void setLarge(String large) {
		this.large = large;
	}
	public String getThumb() {
		return thumb;
	}
	public void setThumb(String thumb) {
		this.thumb = thumb;
	}
}
