package com.cmall.groupcenter.homehas.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询取消销退订单返回接口
 * 
 * @author srnpr
 * 
 */
public class RsyncResponseSyncRegularToNew extends RsyncResponseBase {


    private List<RsyncModelRegularToNew> eventList = new ArrayList<RsyncModelRegularToNew>();

	public List<RsyncModelRegularToNew> getEventList() {
		return eventList;
	}

	public void setEventList(List<RsyncModelRegularToNew> eventList) {
		this.eventList = eventList;
	}

}
