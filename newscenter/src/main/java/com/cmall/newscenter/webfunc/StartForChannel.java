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
public class StartForChannel extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		String tplUid = mDataMap.get("zw_f_uid");
		String channelUse = mDataMap.get("zw_f_channel_use");

		if ("449747170002".equals(channelUse)) {
			mResult.setResultCode(934205152);
			mResult.setResultMessage(bInfo(934205152));
			return mResult;
		} else {
			MDataMap whereDataMap = new MDataMap();
			whereDataMap.put("channel_use", "449747170002");
			whereDataMap.put("uid", tplUid);
			DbUp.upTable("nc_video_channel").dataUpdate(whereDataMap,
					"channel_use", "uid");
		}

		return mResult;

	}
}
