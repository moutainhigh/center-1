package com.cmall.membercenter.api;

import org.apache.commons.lang.StringUtils;

import com.cmall.membercenter.model.UserLoginInput;
import com.cmall.membercenter.model.UserLoginResult;
import com.cmall.membercenter.support.MemberLoginSupport;
import com.cmall.membercenter.support.ScoredSupport;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;

public class UserLogin extends
		RootApiForManage<UserLoginResult, UserLoginInput> {

	@ZapcomApi(remark = "用户登陆")
	public UserLoginResult Process(UserLoginInput inputParam,
			MDataMap mRequestMap) {
		UserLoginResult userRegResult = new UserLoginResult();
		
		/*判断昵称是否存在*/
		if(userRegResult.upFlagTrue()){
			
			ScoredSupport scoredSupport = new ScoredSupport();
			
			userRegResult.inOtherResult(scoredSupport.FreezeAccounts(inputParam.getLogin_name(),getManageCode()));
			
		}
		
		if(userRegResult.upFlagTrue()){
			
			UserLoginResult userLoginResult = new MemberLoginSupport()
			.doStarLogin(inputParam, getManageCode());
			
			
			
			if (StringUtils.isEmpty(userLoginResult.getUser().getNickname())) {

				String sLoginName = inputParam.getLogin_name();

				userLoginResult.getUser().setNickname(StringUtils.substring(sLoginName, 0, 3)
						+ "*****"
						+ StringUtils.substring(sLoginName, 8, 11));
			}
			
			userRegResult.setUser(userLoginResult.getUser());
			
			userRegResult.setUser_token(userLoginResult.getUser_token());
			
			userRegResult.setConfig(userLoginResult.getConfig());
			
			userRegResult.setsChange(userLoginResult.getsChange());
			
			userRegResult.setResultCode(userLoginResult.getResultCode());
			
			userRegResult.setResultMessage(userLoginResult.getResultMessage());
			
		}
		
		return userRegResult;
	}

}
