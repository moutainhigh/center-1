package com.cmall.groupcenter.func.wonderfuldiscovery.api;

import java.util.List;

import com.cmall.groupcenter.func.wonderfuldiscovery.model.WonderfulDiscoveryInput;
import com.cmall.groupcenter.func.wonderfuldiscovery.model.WonderfulDiscoveryListResult;
import com.cmall.groupcenter.func.wonderfuldiscovery.model.WonderfulDiscoveryResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 模块:精彩发现
 * 功能:提供移动终端显示列表
 * 排序：添加时设置。按照数值倒序在前台显示，数值相同按修改时间倒序
 * @author LHY
 * 2015年1月14日 下午4:06:50
 */
public class ApiWonderfulDiscoveryList extends RootApiForManage<WonderfulDiscoveryResult, WonderfulDiscoveryInput> {

	
	public WonderfulDiscoveryResult Process(WonderfulDiscoveryInput inputParam, MDataMap mRequestMap) {
		WonderfulDiscoveryResult result = new WonderfulDiscoveryResult();
		MDataMap mDataMap = new MDataMap();
		mDataMap.put("is_hidden", "4497465200160001");
		String sSqlWhere = "is_hidden=:is_hidden";
		if(inputParam.getIsHidden().equals("1")){
			sSqlWhere += " and app_code !=:app_code";
			mDataMap.put("app_code", this.getManageCode());
		}
		List<MDataMap> list = DbUp.upTable("gc_wonderful_discovery").query("*", "sorted desc,create_time desc", sSqlWhere, mDataMap, -1, 0);
		for(MDataMap map: list) {
			WonderfulDiscoveryListResult wonderful = new WonderfulDiscoveryListResult();
			wonderful.setTitle(map.get("title"));
			wonderful.setPicUrl(map.get("pic_url"));
			wonderful.setDescription(map.get("description"));
			wonderful.setIosUrl(map.get("ios_url"));
			wonderful.setAndroidUrl(map.get("android_url"));
			wonderful.setIosPackage(map.get("ios_package"));
			wonderful.setAndroidPackage(map.get("android_package"));
			wonderful.setUpdateTime(map.get("update_time"));
			wonderful.setAppCode(map.get("app_code"));
			wonderful.setAndroidVersion(map.get("android_version"));
			wonderful.setPicCircleUrl(map.get("pic_circle_url"));
			wonderful.setCompareVersion(map.get("compare_version"));
			result.getList().add(wonderful);
			
		}
		
		return result;
	}

}
