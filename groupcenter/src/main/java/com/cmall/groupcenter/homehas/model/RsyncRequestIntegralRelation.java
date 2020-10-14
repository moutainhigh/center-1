package com.cmall.groupcenter.homehas.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncRequest;

public class RsyncRequestIntegralRelation implements IRsyncRequest {
	
	/**
	 * 积分共享关系列表
	 */
	private List<IntegralRelation> paramList = new ArrayList<IntegralRelation>();

	public List<IntegralRelation> getParamList() {
		return paramList;
	}

	public void setParamList(List<IntegralRelation> paramList) {
		this.paramList = paramList;
	}

	
}
