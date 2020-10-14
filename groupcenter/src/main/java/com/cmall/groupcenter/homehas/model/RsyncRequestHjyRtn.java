package com.cmall.groupcenter.homehas.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncRequest;

public class RsyncRequestHjyRtn implements IRsyncRequest {
	
	/**
	 * 积分共享关系列表
	 */
	private List<HjyRtnInfo> paramList = new ArrayList<HjyRtnInfo>();

	public List<HjyRtnInfo> getParamList() {
		return paramList;
	}

	public void setParamList(List<HjyRtnInfo> paramList) {
		this.paramList = paramList;
	}

	
}
