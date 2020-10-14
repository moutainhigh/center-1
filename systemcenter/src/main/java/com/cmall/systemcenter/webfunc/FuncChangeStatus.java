package com.cmall.systemcenter.webfunc;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FuncChangeStatus extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		String proclamationStatus = mDataMap.get("zw_f_proclamation_status");
		String uidString = mDataMap.get("zw_f_uid");
		MDataMap mDataMap2 = new MDataMap();
		mDataMap2.put("uid", uidString);
		if("4497477000010001".equals(proclamationStatus)){//如果是发布状态
			mDataMap2.put("proclamation_status", "4497477000010002");//变为禁用
		}else{
			mDataMap2.put("proclamation_status", "4497477000010001");
		}
		DbUp.upTable("fh_proclamation_manage").dataUpdate(mDataMap2, "proclamation_status", "uid");
		return mResult;
	}
}
