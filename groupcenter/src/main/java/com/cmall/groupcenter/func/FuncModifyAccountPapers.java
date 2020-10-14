package com.cmall.groupcenter.func;

import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 
 * 修改微公社账户证件
 * @author GaoYang
 * @CreateDate 2015年4月27日下午2:01:27
 *
 */
public class FuncModifyAccountPapers extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();

		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		
		String updateTime = FormatHelper.upDateTime();
		
		String updateUser = UserFactory.INSTANCE.create().getLoginName();
		
		mAddMaps.put("update_time", updateTime);

		mAddMaps.put("update_user", updateUser);
		
		DbUp.upTable("gc_member_papers_info").dataUpdate(mAddMaps,"user_name,papers_type,papers_code,update_time,update_user","uid");

		return mResult;
	}

}
