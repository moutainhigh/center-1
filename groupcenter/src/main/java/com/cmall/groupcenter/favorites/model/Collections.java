package com.cmall.groupcenter.favorites.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
/**
 * 帖子收藏bean
 * @author yuwyn
 *
 */
public class Collections {
	
	@ZapcomApi(value="收藏ID")
	private String collection_id = "";
	
	@ZapcomApi(value="账户编号")
	private String member_code = "";
	
	@ZapcomApi(value="帖子ID")
	private String post_id = "";
	
	@ZapcomApi(value="帖子标题")
	private String post_title = "";
	
	@ZapcomApi(value="收藏时间")
	private String collection_time = "";
	
	@ZapcomApi(value="状态")
	private String flag ="";
	
	@ZapcomApi(value="所属app")
	private String app_code ="";
	
	@ZapcomApi(value="图片")
	private String img_url ="";
	
	@ZapcomApi(value="帖子是否失效",remark="0:失效 1:有效")
	private int isValid =1;

	@ZapcomApi(value="帖子时间标签",remark="0代表当天即新鲜，1为昨天，2是当年的 long ago,3为long long ago")
	private String timeLable = "0";

	public String getCollection_id() {
		return collection_id;
	}

	public void setCollection_id(String collection_id) {
		this.collection_id = collection_id;
	}

	public String getMember_code() {
		return member_code;
	}

	public void setMember_code(String member_code) {
		this.member_code = member_code;
	}

	public String getPost_id() {
		return post_id;
	}

	public void setPost_id(String post_id) {
		this.post_id = post_id;
	}

	public String getPost_title() {
		return post_title;
	}

	public void setPost_title(String post_title) {
		this.post_title = post_title;
	}

	public String getCollection_time() {
		return collection_time;
	}

	public void setCollection_time(String collection_time) {
		this.collection_time = collection_time;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getApp_code() {
		return app_code;
	}

	public void setApp_code(String app_code) {
		this.app_code = app_code;
	}

	public String getImg_url() {
		return img_url;
	}

	public void setImg_url(String img_url) {
		this.img_url = img_url;
	}

	public int getIsValid() {
		return isValid;
	}

	public void setIsValid(int isValid) {
		this.isValid = isValid;
	}

	public String getTimeLable() {
		return timeLable;
	}

	public void setTimeLable(String timeLable) {
		this.timeLable = timeLable;
	}
	
}
