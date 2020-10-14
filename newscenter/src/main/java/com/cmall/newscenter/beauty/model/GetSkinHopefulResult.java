package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 惠美丽—获取护肤需求输出类
 * 
 * @author yangrong date: 2014-12-05
 * @version1.3.0
 */
public class GetSkinHopefulResult extends RootResultWeb {

	@ZapcomApi(value = "护肤需求列表")
	private List<SkinHopeful> hopefulList = new ArrayList<SkinHopeful>();

	public List<SkinHopeful> getHopefulList() {
		return hopefulList;
	}

	public void setHopefulList(List<SkinHopeful> hopefulList) {
		this.hopefulList = hopefulList;
	}

}
