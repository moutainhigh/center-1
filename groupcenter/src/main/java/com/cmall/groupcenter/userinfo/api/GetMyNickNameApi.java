package com.cmall.groupcenter.userinfo.api;

import com.cmall.groupcenter.userinfo.model.UserInfoResult;
import com.cmall.groupcenter.userinfo.model.UserInfoResult.UserInfo;
import com.cmall.membercenter.helper.NickNameHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 获取我自己的用户昵称 
 * 
 * @author wangzx
 *
 */
public class GetMyNickNameApi extends
    RootApiForToken<UserInfoResult, RootInput> {

	public UserInfoResult Process(RootInput inputParam, MDataMap mRequestMap) {
		UserInfoResult userInfoResult = new UserInfoResult();
		//String nickName = NickNameHelper.getMyNickName(getUserCode(), this.getManageCode());
		UserInfo  userInfo = userInfoResult.new UserInfo();
		userInfo.setNickName(this.getOauthInfo().getLoginName());
		userInfoResult.setUserInfo(userInfo);
		return userInfoResult;
	}
	
	
}
