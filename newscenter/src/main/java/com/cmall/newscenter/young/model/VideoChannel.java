package com.cmall.newscenter.young.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.newscenter.beauty.model.PostsList;
import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class VideoChannel {

	@ZapcomApi(value = "频道编号")
	private String channel_code = "";
  
	@ZapcomApi(value = "频道名称")
	private String channel_name = "";
  
	@ZapcomApi(value="视频列表")
	private List<VideoList> videoList = new ArrayList<VideoList>();
	
	@ZapcomApi(value = "帖子列表")
	private List<PostsList> posts = new ArrayList<PostsList>();
	  
	@ZapcomApi(value = "频道权值")
	private String channel_page = "";
	
	
  
	public String getChannel_code() {
		return channel_code;
	}

	public void setChannel_code(String channel_code) {
		this.channel_code = channel_code;
	}

	public String getChannel_name() {
		return channel_name;
	}

	public void setChannel_name(String channel_name) {
		this.channel_name = channel_name;
	}

	public String getChannel_page() {
		return channel_page;
	}

	public void setChannel_page(String channel_page) {
		this.channel_page = channel_page;
	}

	public List<VideoList> getVideoList() {
		return videoList;
	}

	public void setVideoList(List<VideoList> videoList) {
		this.videoList = videoList;
	}

	public List<PostsList> getPosts() {
		return posts;
	}

	public void setPosts(List<PostsList> posts) {
		this.posts = posts;
	}


	
	
	

}
