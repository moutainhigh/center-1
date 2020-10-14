package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;


/**
 * 行程列表
 * @author shiyz
 * date 2014-8-11
 * @version 1.0
 */
public class Schedule {

	@ZapcomApi(value="标题")
	private String title = "";
	
	@ZapcomApi(value="开始时间")
	private String begin_at = "";

	@ZapcomApi(value="结束时间")
	private String end_at = "";
	
	@ZapcomApi(value="图标")
	private CommentdityAppPhotos icon = new CommentdityAppPhotos();
	
	@ZapcomApi(value="位置")
	private Location location = new Location();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBegin_at() {
		return begin_at;
	}

	public void setBegin_at(String begin_at) {
		this.begin_at = begin_at;
	}

	public String getEnd_at() {
		return end_at;
	}

	public void setEnd_at(String end_at) {
		this.end_at = end_at;
	}

	public CommentdityAppPhotos getIcon() {
		return icon;
	}

	public void setIcon(CommentdityAppPhotos icon) {
		this.icon = icon;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	
}
