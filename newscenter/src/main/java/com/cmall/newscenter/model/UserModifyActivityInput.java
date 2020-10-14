package com.cmall.newscenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;


/**
 * 用户 - 修改活动输入类
 * @author yangrong
 * date 2014-8-22
 * @version 1.0
 */
public class UserModifyActivityInput extends RootInput{

	@ZapcomApi(value="活动编码",remark="活动编码",demo="1234567890",require=1,verify = "minlength=10")
	private String activity = "";
	
	@ZapcomApi(value="活动标题",demo="型人示范潮流点晴饰物",require=1)
	private String title = "";
	
	@ZapcomApi(value="活动内容",demo="长项链搭配小吊带....",require=1)
	private String text = "";
	
	@ZapcomApi(value="照片",require=1)
	private List<CommentdityAppPhotos> photo = new ArrayList<CommentdityAppPhotos>();
	
	@ZapcomApi(value="最低级别")
	private String level = "";
	
	@ZapcomApi(value="位置信息",remark="1")
	private Location location = new Location();

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	
	public List<CommentdityAppPhotos> getPhoto() {
		return photo;
	}

	public void setPhoto(List<CommentdityAppPhotos> photo) {
		this.photo = photo;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	
	
}
