package com.cmall.groupcenter.homehas.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncRequest;

public class RsyncRequestHjyOrders implements IRsyncRequest {
	
	/**
	 * 积分共享关系列表
	 */
	private List<HjyOrderInfo> paramList = new ArrayList<HjyOrderInfo>();

	public List<HjyOrderInfo> getParamList() {
		return paramList;
	}

	public void setParamList(List<HjyOrderInfo> paramList) {
		this.paramList = paramList;
	}

	
}
