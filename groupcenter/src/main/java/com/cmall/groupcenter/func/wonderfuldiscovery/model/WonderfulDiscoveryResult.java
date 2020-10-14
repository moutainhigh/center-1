package com.cmall.groupcenter.func.wonderfuldiscovery.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootResult;

/**
 * 模块:精彩发现
 * 功能:提供移动终端数据显示信息
 * @author LHY
 * 2015年1月14日 下午4:40:23
 */
public class WonderfulDiscoveryResult extends RootResult {
	@ZapcomApi(value="信息列表",remark="信息列表")
	private List<WonderfulDiscoveryListResult> list = new ArrayList<WonderfulDiscoveryListResult>();

	public List<WonderfulDiscoveryListResult> getList() {
		return list;
	}

	public void setList(List<WonderfulDiscoveryListResult> list) {
		this.list = list;
	}
}