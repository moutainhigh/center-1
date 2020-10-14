package com.cmall.ordercenter.model;

import java.util.List;

import com.srnpr.zapcom.topapi.RootResult;

/**
 * 获得商品到某区域的运费
 * @author huoqiangshou
 *
 */
public class GetPtAreaFreightResult extends RootResult{
	
	/**
	 * 商品列表
	 */
	private List<PtAreaFreight> ptList;

	public List<PtAreaFreight> getPtList() {
		return ptList;
	}

	public void setPtList(List<PtAreaFreight> ptList) {
		this.ptList = ptList;
	}
	
	
	
}
