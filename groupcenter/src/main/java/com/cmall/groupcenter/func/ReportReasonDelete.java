package com.cmall.groupcenter.func;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 * 微公社举报原因删除
 * @author panwei
 **/
public class ReportReasonDelete extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		
		if (mResult.upFlagTrue()) {
			String uid = mAddMaps.get("uid");
			MDataMap dataMap = DbUp.upTable("gc_report_reason").one("uid",uid);
			//举报原因删除
			dataMap.put("is_delete","4497472000070001");
			DbUp.upTable("gc_report_reason").dataUpdate(dataMap, "", "uid");
			
		}
		return mResult;
	}

}
