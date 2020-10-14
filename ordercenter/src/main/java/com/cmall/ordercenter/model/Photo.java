package com.cmall.ordercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
/**
 * 试用商品 图片对象
 * @author jl
 *
 */
public class Photo {
	@ZapcomApi(value = "原图宽",demo="100")
	private float width=0f;
	@ZapcomApi(value = "原图高",demo="100")
	private float height=0f;
	@ZapcomApi(value = "原图",demo="http://")
	private String large="";
	@ZapcomApi(value = "缩略图",demo="http://")
	private String thumb="";
	public float getWidth() {
		return width;
	}
	public void setWidth(float width) {
		this.width = width;
	}
	public float getHeight() {
		return height;
	}
	public void setHeight(float height) {
		this.height = height;
	}
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
