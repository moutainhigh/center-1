package com.cmall.newscenter.webfunc;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 用户启用
 * 
 * @author lijx
 *
 */

public class StartForRecreation extends RootFunc {
	
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		String tplUid = mDataMap.get("zw_f_uid");
		String recreationUse = mDataMap.get("zw_f_recreation_use");

		if ("449747170002".equals(recreationUse)) {
			mResult.setResultCode(934205152);
			mResult.setResultMessage(bInfo(934205152));
			return mResult;
		} else {
			MDataMap whereDataMap = new MDataMap();
			whereDataMap.put("recreation_use", "449747170002");
			whereDataMap.put("uid", tplUid);
			DbUp.upTable("nc_recreation").dataUpdate(whereDataMap,
					"recreation_use", "uid");
		}

		return mResult;
	}
}
