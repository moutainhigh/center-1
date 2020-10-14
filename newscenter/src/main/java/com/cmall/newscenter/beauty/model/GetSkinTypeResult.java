package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 惠美丽—获取皮肤类型输出类
 * 
 * @author yangrong date: 2014-12-05
 * @version1.3.0
 */
public class GetSkinTypeResult extends RootResultWeb {

	@ZapcomApi(value = "皮肤类型列表")
	private List<SkinType> skinList = new ArrayList<SkinType>();

	public List<SkinType> getSkinList() {
		return skinList;
	}

	public void setSkinList(List<SkinType> skinList) {
		this.skinList = skinList;
	}

}
