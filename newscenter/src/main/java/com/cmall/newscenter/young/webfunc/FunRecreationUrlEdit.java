package com.cmall.newscenter.young.webfunc;

import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 * 修改视频链接
 * 
 * @author lijx
 * 
 */

public class FunRecreationUrlEdit extends RootFunc{
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap){
		MWebResult mResult = new MWebResult();

		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		
		String creater = UserFactory.INSTANCE.create().getLoginName();
		
		String create_time = FormatHelper.upDateTime();
		
		mAddMaps.put("update_time",create_time);
		
		mAddMaps.put("update_user",creater);
		
		DbUp.upTable("nc_recreation_url").update(mAddMaps);
		return mResult;
		
	}
}
