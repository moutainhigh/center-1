package com.cmall.newscenter.webfunc;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 用户禁用
 * 
 * @author lijx
 */
public class ForbiddenForChannel extends RootFunc{
	
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();
		
		String tplUid = mDataMap.get("zw_f_uid");
		String channelUse = mDataMap.get("zw_f_channel_use");
		
		if("449747170001".equals(channelUse)){
			mResult.setResultCode(934205116);
			mResult.setResultMessage(bInfo(934205116));
			return mResult;
		}else{
			MDataMap whereDataMap  = new MDataMap();
			whereDataMap.put("channel_use", "449747170001");
			whereDataMap.put("uid", tplUid);
				DbUp.upTable("nc_video_channel").dataUpdate(whereDataMap, "channel_use", "uid");
		}
		
		return mResult;
	    
	}
}
