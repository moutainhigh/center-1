package com.cmall.newscenter.webfunc;

import com.cmall.newscenter.util.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FuncAddForAppLinkAddress extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult result = new MWebResult();
		MDataMap params = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		if(result.upFlagTrue()){
			params.put("link_time", DateUtil.getNowTime());
			DbUp.upTable("nc_Link_address").dataInsert(params);
		}
		return result;
	}

}
