package com.cmall.groupcenter.func.wonderfuldiscovery;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 模块:精彩发现
 * 功能:启用/停用数据
 * @author LHY
 * 2015年1月13日 下午6:38:41
 */
public class FuncExecuteWonderfulDiscovery extends RootFunc{

	
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mWebResult = new MWebResult();
		String status = mDataMap.get("zw_f_is_hidden");
		String uid = mDataMap.get("zw_f_uid");
		MDataMap whereDataMap  = new MDataMap();
		if("4497465200160001".equals(status)) {
			whereDataMap.put("is_hidden", "4497465200160002");
			whereDataMap.put("uid", uid);
			DbUp.upTable("gc_wonderful_discovery").dataUpdate(whereDataMap, "is_hidden", "uid");
		} else if("4497465200160002".equals(status)) {
			whereDataMap.put("is_hidden", "4497465200160001");
			whereDataMap.put("uid", uid);
			DbUp.upTable("gc_wonderful_discovery").dataUpdate(whereDataMap, "is_hidden", "uid");
		}
		return mWebResult;
	}

}
