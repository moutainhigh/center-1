package com.cmall.groupcenter.behavior.model;

import java.util.List;

import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 百分点推荐信息
 * @author pang_jhui
 *
 */
public class BfdRecResultInfo extends RootResultWeb {
	
	/*推荐结果唯一标识id*/
	private String openDS = "";
	
	/*推荐商品信息列表*/
	private List<BfdRecProductInfo> recProductInfoList = null;
	
	public String getOpenDS() {
		return openDS;
	}

	public void setOpenDS(String openDS) {
		this.openDS = openDS;
	}

	public List<BfdRecProductInfo> getRecProductInfoList() {
		return recProductInfoList;
	}

	public void setRecProductInfoList(List<BfdRecProductInfo> recProductInfoList) {
		this.recProductInfoList = recProductInfoList;
	}
	
}
