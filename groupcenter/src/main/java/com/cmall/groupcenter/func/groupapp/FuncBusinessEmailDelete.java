package com.cmall.groupcenter.func.groupapp;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FuncBusinessEmailDelete  extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();
		
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		MDataMap mWhereMap=new MDataMap();
		String uid = mAddMaps.get("uid");
		mWhereMap.put("uid", uid);


		mWhereMap.put("flag_delete", "0");

		DbUp.upTable("gc_business_email_config").dataUpdate(mWhereMap,"","uid");

		
		return mResult;
	}
}
