package com.cmall.groupcenter.func;

import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 * 微公社举报原因维护
 * @author panwei
 **/
public class ReportReasonAdd extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		
		if (mResult.upFlagTrue()) {
			
			/*系统当前时间*/
			String create_time = DateUtil.getNowTime();
			/*获取当前登录人*/
			String create_user = UserFactory.INSTANCE.create().getUserCode();
			String reason = mAddMaps.get("report_reason");
			
			if(null != reason && !"".equals(reason) 
					&& (reason.length() < 1 || reason.length() > 20)) {
				mResult.setResultCode(-1);
				mResult.setResultMessage("举报原因字数只能是1-20字！");
				return mResult;	
			}
			
			//举报原因表插入参数
			mAddMaps.put("create_time",create_time);
			mAddMaps.put("create_user",create_user);
			mAddMaps.put("report_reason",reason);
			mAddMaps.put("is_delete","4497472000070002");
			DbUp.upTable("gc_report_reason").dataInsert(mAddMaps);
			
		}
		return mResult;
	}

}
