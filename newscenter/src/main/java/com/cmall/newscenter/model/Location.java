package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;


/**
 * 位置
 * @author shiyz
 * date 2014-8-11
 * @version 1.0
 */
public class Location {

	@ZapcomApi(value="名称",require=1)
	private String name = "";
	
	@ZapcomApi(value="经度")
	private Double lat = 0.00;
	
	@ZapcomApi(value="纬度")
	private Double lon = 0.00;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}

	
	
	
}
