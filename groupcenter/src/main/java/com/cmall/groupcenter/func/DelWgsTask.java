package com.cmall.groupcenter.func;

import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 * 微公社删除任务
 * @author dyc
 * @version 1.0
 **/
public class DelWgsTask extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		
		if (mResult.upFlagTrue()) {			
			mAddMaps.put("is_delete","449747110002");//默认未删除
			mAddMaps.put("update_time",DateUtil.getNowTime());
			mAddMaps.put("creator",UserFactory.INSTANCE.create().getUserCode());/*获取当前登录人*/
			DbUp.upTable("nc_wgs_task").dataUpdate(mAddMaps, "", "uid");						
		}
		return mResult;
	}
		
}
