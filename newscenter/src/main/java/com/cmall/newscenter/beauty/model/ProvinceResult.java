package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 惠美丽-获取省输出类
 * @author yangrong
 * date 2014-9-11
 * @version 1.0
 */
public class ProvinceResult  extends RootResultWeb {
	
	@ZapcomApi(value = "城市List")
	private List<Province> provinces = new ArrayList<Province>();

	public List<Province> getProvinces() {
		return provinces;
	}

	public void setProvinces(List<Province> provinces) {
		this.provinces = provinces;
	}
	
	

}
