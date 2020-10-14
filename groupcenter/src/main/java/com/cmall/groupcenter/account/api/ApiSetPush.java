package com.cmall.groupcenter.account.api;
import com.cmall.groupcenter.account.model.SetPushInput;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class ApiSetPush extends RootApiForToken<RootResultWeb, SetPushInput>{

	public RootResultWeb Process(SetPushInput inputParam, MDataMap mRequestMap) {
		RootResultWeb rootResultWeb = new RootResultWeb();
		MDataMap isExistMap =	DbUp.upTable("sc_user_push_info").one("user_code",getUserCode(),"app_code",getManageCode());
		if(isExistMap ==null){
			DbUp.upTable("sc_user_push_info").insert("user_code",getUserCode(),
					"app_code",getManageCode()
					,"create_time",FormatHelper.upDateTime()
					,"update_time",FormatHelper.upDateTime()
					,"is_send",inputParam.getIsSend());
		}else{
			MDataMap mDataMap = new MDataMap();
			mDataMap.put("zid", isExistMap.get("zid"));
			mDataMap.put("uid", isExistMap.get("uid"));
			mDataMap.put("user_code", getUserCode());
			mDataMap.put("app_code", getManageCode());
			mDataMap.put("update_time", FormatHelper.upDateTime());
			mDataMap.put("is_send", inputParam.getIsSend());
			DbUp.upTable("sc_user_push_info").update(mDataMap);
		}
		return rootResultWeb;
	}
	
}
