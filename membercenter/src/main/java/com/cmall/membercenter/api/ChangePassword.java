package com.cmall.membercenter.api;

import com.cmall.membercenter.model.ChangePasswordInput;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basehelper.SecrurityHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapweb.webapi.RootApiForToken;
import com.srnpr.zapweb.webapi.RootResultWeb;
import com.srnpr.zapweb.webdo.WebConst;

/**
 * 修改密码
 * 
 * @author srnpr
 * 
 */
public class ChangePassword extends
		RootApiForToken<RootResultWeb, ChangePasswordInput> {

	public RootResultWeb Process(ChangePasswordInput inputParam,
			MDataMap mRequestMap) {

		RootResultWeb result = new RootResultWeb();
		
		String address = bConfig("xmassystem.group_pay_url");
		String sTarget = "com.cmall.membercenter.api.ChangePassword";
		String sApiKey = bConfig("xmassystem.group_pay_key");
		String sApiPass = bConfig("xmassystem.group_pay_pass");
		String sApiToken = getOauthInfo().getAccessToken();
		
		if (address.endsWith("/")) {
			address = address + sTarget;
		}
		
		JsonHelper<ChangePasswordInput> jsonHelper = new JsonHelper<ChangePasswordInput>();
		String sInputString = jsonHelper.ObjToString(inputParam);
		String sTimeSpan = FormatHelper.upDateTime();
		String sSource = sTarget + sApiKey + sInputString + sTimeSpan + sApiPass;
		String sSec = SecrurityHelper.MD5(sSource);
		
		MDataMap mDataMap = new MDataMap();
		mDataMap.put("api_key", sApiKey);
		mDataMap.put("api_input", sInputString);
		mDataMap.put("api_target", sTarget);
		mDataMap.put("api_secret", sSec);
		mDataMap.put("api_timespan", sTimeSpan);
		mDataMap.put("api_token", sApiToken);
		
		mDataMap.put(WebConst.CONST_APIFACTORY_FOR_PARAM + "api_manage_code", getManageCode());
		mDataMap.put(WebConst.CONST_APIFACTORY_FOR_PARAM + "api_user_code", getUserCode());
		mDataMap.put(WebConst.CONST_APIFACTORY_FOR_PARAM + "api_access_token", sApiToken);
		
		try {
			String sCallString = WebClientSupport.upPost(address, mDataMap);
			bLogDebug(0, sCallString);
			
			JsonHelper<RootResultWeb> jsonHelperResult = new JsonHelper<RootResultWeb>();
			result = jsonHelperResult.StringToObjExp(sCallString, result);
		} catch (Exception e) {
			e.printStackTrace();
			result.inErrorMessage(0, "接口调用失败["+e+"]");
		}
		
		return result;
	}

}
