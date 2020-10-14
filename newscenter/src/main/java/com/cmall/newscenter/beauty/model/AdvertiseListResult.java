package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.newscenter.model.PageResults;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 广告列表输出类
 * @author houwen
 * date 2014-08-25
 * @version 1.0
 */
public class AdvertiseListResult extends RootResultWeb {

	@ZapcomApi(value = "广告列表")
	private List<Advertise> advertise = new ArrayList<Advertise>();
	
	public List<Advertise> getAdvertise() {
		return advertise;
	}

	public void setAdvertise(List<Advertise> advertise) {
		this.advertise = advertise;
	}

}
