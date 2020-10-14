package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 惠美丽-获取区输出类
 * @author yangrong
 * date 2014-9-11
 * @version 1.0
 */
public class AreaResult extends RootResultWeb {

	@ZapcomApi(value = "城市List")
	private List<Area> area = new ArrayList<Area>();

	public List<Area> getArea() {
		return area;
	}

	public void setArea(List<Area> area) {
		this.area = area;
	}

	

	
	
}
