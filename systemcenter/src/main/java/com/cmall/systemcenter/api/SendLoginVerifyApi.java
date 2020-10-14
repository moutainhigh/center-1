package com.cmall.systemcenter.api;

import com.cmall.systemcenter.model.UserLoginVerifyInput;
import com.cmall.systemcenter.webfunc.FuncSendLoginVerify;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 惠家有用户登录获取手机验证码
 * @author ligj
 *
 */
public class SendLoginVerifyApi extends RootApi<MWebResult, UserLoginVerifyInput> {

	public MWebResult Process(UserLoginVerifyInput inputParam, MDataMap mRequestMap) {
		// TODO Auto-generated method stub
		MDataMap mFuncMap = new MDataMap();
		mFuncMap.put(WebConst.CONST_WEB_FIELD_NAME + "login_name",
				inputParam.getLoginName());
		mFuncMap.put(WebConst.CONST_WEB_FIELD_NAME + "login_pass",
				inputParam.getLoginPass());
		mFuncMap.put(WebConst.CONST_WEB_FIELD_NAME + "mobile_verify",
				inputParam.getMobileVerify());
		return new FuncSendLoginVerify().funcDo("", mFuncMap);

	}

}
