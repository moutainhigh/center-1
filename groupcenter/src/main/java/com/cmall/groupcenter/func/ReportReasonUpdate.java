package com.cmall.groupcenter.func;

import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 * 微公社举报原因修改
 * @author panwei
 **/
public class ReportReasonUpdate extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		
		if (mResult.upFlagTrue()) {
			String uid = mAddMaps.get("uid");
			/*系统当前时间*/
			String update_time = DateUtil.getNowTime();
			/*获取当前登录人*/
			String update_user = UserFactory.INSTANCE.create().getUserCode();
			String reason = mAddMaps.get("report_reason");
			
			if(null != reason && !"".equals(reason) 
					&& (reason.length() < 1 || reason.length() > 20)) {
				mResult.setResultCode(-1);
				mResult.setResultMessage("举报原因字数只能是1-20字！");
				return mResult;	
			}
			MDataMap dataMap = DbUp.upTable("gc_report_reason").one("uid",uid);
			//举报原因表修改参数
			dataMap.put("update_time",update_time);
			dataMap.put("update_user",update_user);
			dataMap.put("report_reason",reason);
			DbUp.upTable("gc_report_reason").dataUpdate(dataMap, "", "uid");
			
		}
		return mResult;
	}

}
