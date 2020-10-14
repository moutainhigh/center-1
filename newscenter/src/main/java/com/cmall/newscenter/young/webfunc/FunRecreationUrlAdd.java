package com.cmall.newscenter.young.webfunc;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 * 新增视频链接
 * 
 * @author lijx
 * 
 */

public class FunRecreationUrlAdd extends RootFunc{
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap){
		MWebResult mResult = new MWebResult();

		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		
		String create_time = FormatHelper.upDateTime();
		
		String update_time = "";
		
		String creater = UserFactory.INSTANCE.create().getLoginName();
		
		if(StringUtils.isBlank(update_time)){
			
			mAddMaps.put("update_time",create_time);
		}
		
		mAddMaps.put("create_time", create_time);

		mAddMaps.put("creater", creater);
		
		mAddMaps.put("update_user", creater);
		
		DbUp.upTable("nc_recreation_url").dataInsert(mAddMaps);

		return mResult;
	}
}
