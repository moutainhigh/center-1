package com.cmall.productcenter.webfunc;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FunEditForNotice extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		MDataMap map = new MDataMap();
		if (mResult.upFlagTrue()) {
			String uid = mSubMap.get("uid");
			String notice = mSubMap.get("notice");
			map.put("uid", uid);
			map.put("notice", notice);
			DbUp.upTable("oc_tryout_products").dataUpdate(map, "notice","uid");
		}
		return mResult;
	}

}
