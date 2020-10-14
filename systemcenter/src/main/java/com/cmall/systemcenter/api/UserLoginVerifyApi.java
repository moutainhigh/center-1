package com.cmall.systemcenter.api;

import com.cmall.systemcenter.model.UserLoginVerifyInput;
import com.cmall.systemcenter.webfunc.FuncManageLoginVerify;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 惠家有用户登录API（手机验证码）
 * @author ligj
 *
 */
public class UserLoginVerifyApi extends RootApi<MWebResult, UserLoginVerifyInput> {

	public MWebResult Process(UserLoginVerifyInput inputParam, MDataMap mRequestMap) {
		// TODO Auto-generated method stub
		MDataMap mFuncMap = new MDataMap();
		mFuncMap.put(WebConst.CONST_WEB_FIELD_NAME + "login_name",
				inputParam.getLoginName());
		mFuncMap.put(WebConst.CONST_WEB_FIELD_NAME + "login_pass",
				inputParam.getLoginPass());
		mFuncMap.put(WebConst.CONST_WEB_FIELD_NAME + "mobile_verify",
				inputParam.getMobileVerify());
		
		if(DbUp.upTable("za_userinfo").count("user_name",""+inputParam.getLoginName()) == 0){
			MWebResult mResult = new MWebResult();		
			mResult.setResultCode(0);
			mResult.setResultMessage("用户名不存在！");
			return mResult;
		}
		
		return new FuncManageLoginVerify().funcDo("", mFuncMap);

	}

}
