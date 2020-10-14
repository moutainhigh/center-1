package com.cmall.groupcenter.alternickname.model;

import java.util.HashMap;
import java.util.Map;

import com.srnpr.zapweb.webapi.RootResultWeb;

public class AlterNickNameResult extends RootResultWeb {
	private Map<String, Object> oneMap = new HashMap<String, Object>();

	public Map<String, Object> getOneMap() {
		return oneMap;
	}

	public void setOneMap(Map<String, Object> oneMap) {
		this.oneMap = oneMap;
	}
}