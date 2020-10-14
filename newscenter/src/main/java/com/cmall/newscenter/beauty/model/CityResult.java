package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 惠美丽-获取城市输出类
 * @author yangrong
 * date 2014-9-11
 * @version 1.0
 */
public class CityResult extends RootResultWeb {
	
	@ZapcomApi(value = "城市List")
	private List<City> city = new ArrayList<City>();

	public List<City> getCity() {
		return city;
	}

	public void setCity(List<City> city) {
		this.city = city;
	}
	
	

}
