package com.cmall.newscenter.young.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.newscenter.model.PageResults;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * @date 2015-02-02
 * @author shiyz
 * 视频列表接口
 */
public class VideoListResult extends RootResultWeb {
  
  @ZapcomApi(value="频道名称")
  private String channel_name = "";	
	
  @ZapcomApi(value="视频列表")
  private List<VideoList> videoList = new ArrayList<VideoList>();
  
  @ZapcomApi(value = "翻页结果")
  private PageResults paged = new PageResults();
	
	public List<VideoList> getVideoList() {
		return videoList;
	}
	
	public void setVideoList(List<VideoList> videoList) {
		this.videoList = videoList;
	}

	public String getChannel_name() {
		return channel_name;
	}

	public void setChannel_name(String channel_name) {
		this.channel_name = channel_name;
	}

	public PageResults getPaged() {
		return paged;
	}

	public void setPaged(PageResults paged) {
		this.paged = paged;
	}

}
