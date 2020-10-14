package com.cmall.newscenter.api;

import com.cmall.newscenter.model.UserChangePasswordInput;
import com.cmall.newscenter.model.UserChangePasswordResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;
/**
 * 用户 - 修改密码
 * @author liqiang
 * date 2014-7-23
 * @version 1.0
 */
public class UserChangePasswordApi extends RootApiForManage<UserChangePasswordResult, UserChangePasswordInput> {

	public UserChangePasswordResult Process(UserChangePasswordInput inputParam,
			MDataMap mRequestMap) {
		UserChangePasswordResult result = new UserChangePasswordResult();
		if(result.upFlagTrue()){
		}
		return result;
	}

}
