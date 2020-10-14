package com.cmall.newscenter.webfunc;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 * 修改风采介绍
 * @author shiyz
 * date 2014-7-23
 * @version 1.0
 */
public class TalentIntroductionEdit extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		
		
		/*系统当前时间*/
		String update_time = com.cmall.newscenter.util.DateUtil.getNowTime();
		
		mAddMaps.put("update_time", update_time);
		try{
			if (mResult.upFlagTrue()) {
				/**将嘉玲信息插入nc_info表中*/
				DbUp.upTable("nc_introduction").update(mAddMaps);
			}
		}catch (Exception e) {
			mResult.inErrorMessage(959701033);
		}
	
	return mResult;
	}
}
