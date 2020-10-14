package com.cmall.newscenter.young.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class VideoChannelResult extends RootResultWeb {
	
	@ZapcomApi(value="频道列表")
	private List<VideoChannel> channel = new ArrayList<VideoChannel>();

	public List<VideoChannel> getChannel() {
		return channel;
	}

	public void setChannel(List<VideoChannel> channel) {
		this.channel = channel;
	}
	
}
