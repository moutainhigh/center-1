package com.cmall.newscenter.young.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.newscenter.beauty.model.PostsList;
import com.cmall.newscenter.model.PageResults;
import com.cmall.newscenter.young.model.VideoChannel;
import com.cmall.newscenter.young.model.VideoList;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 帖子列表输出类
 * @author houwen
 * date 2014-08-26
 * @version 1.0
 */
public class HomeChannelListResult extends RootResultWeb {

	@ZapcomApi(value="频道列表")
	private List<VideoChannel> channel = new ArrayList<VideoChannel>();
	
	@ZapcomApi(value = "翻页结果")
	private PageResults paged = new PageResults();
	

	public PageResults getPaged() {
		return paged;
	}

	public void setPaged(PageResults paged) {
		this.paged = paged;
	}

	public List<VideoChannel> getChannel() {
		return channel;
	}

	public void setChannel(List<VideoChannel> channel) {
		this.channel = channel;
	}

}
