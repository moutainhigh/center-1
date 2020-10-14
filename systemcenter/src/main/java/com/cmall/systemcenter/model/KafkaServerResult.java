package com.cmall.systemcenter.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * storm查询返回值
 * @author zhouguohui
 * @version 1.0
 */
public class KafkaServerResult extends RootResultWeb{
	private List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
	private Map<String, Object> map  =  new HashMap<String,Object>();
	/**
	 * @return the list
	 */
	public List<Map<String, Object>> getList() {
		return list;
	}

	/**
	 * @param list the list to set
	 */
	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}

	/**
	 * @return the map
	 */
	public Map<String, Object> getMap() {
		return map;
	}

	/**
	 * @param map the map to set
	 */
	public void setMap(Map<String, Object> map) {
		this.map = map;
	}
	
	
}
