package com.cmall.newscenter.webfunc;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 * 将图文发布内容放入表中
 * @author shiyz	
 * date 2014-7-21
 * @version 1.0
 */
public class TopRankingFunEdit extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		

		/*系统当前时间*/
		String create_time = com.cmall.newscenter.util.DateUtil.getNowTime();
		
		/*获取当前登录人*/
		
		String create_user = UserFactory.INSTANCE.create().getLoginName();
		
		
		mAddMaps.put("top_time", create_time);
		
		
		mAddMaps.put("top_user", create_user);
		
		try{
			if (mResult.upFlagTrue()) {
				/**将嘉玲信息插入nc_info表中*/
				DbUp.upTable("nc_top_ranking").update(mAddMaps);
			}
		}catch (Exception e) {
			mResult.inErrorMessage(959701033);
		}
	
	return mResult;
	}
}
