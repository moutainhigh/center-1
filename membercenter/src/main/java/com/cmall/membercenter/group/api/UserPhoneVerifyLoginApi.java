package com.cmall.membercenter.group.api;

import org.apache.commons.lang.StringUtils;

import com.cmall.membercenter.enumer.ELoginType;
import com.cmall.membercenter.group.model.GroupLoginInput;
import com.cmall.membercenter.group.model.GroupLoginResult;
import com.cmall.membercenter.group.model.UserPhoneVerifyLoginInput;
import com.cmall.membercenter.memberdo.MemberConst;
import com.cmall.membercenter.model.MLoginInput;
import com.cmall.membercenter.model.MLoginResult;
import com.cmall.membercenter.support.MemberLoginSupport;
import com.cmall.systemcenter.enumer.EVerifyCodeTypeEnumer;
import com.cmall.systemcenter.support.VerifySupport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 微公社账户登陆
 * 
 * @author wangzx
 *
 */
public class UserPhoneVerifyLoginApi extends
		RootApiForManage<GroupLoginResult, UserPhoneVerifyLoginInput> {

	public GroupLoginResult Process(UserPhoneVerifyLoginInput inputParam,
			MDataMap mRequestMap) {
		
		GroupLoginInput input = new GroupLoginInput();
		input.setLoginName(inputParam.getLoginName());
		VerifySupport verifySupport = new VerifySupport();
		// 验证验证码是否正确
		MWebResult result = verifySupport
				.checkVerifyCodeByType(
						EVerifyCodeTypeEnumer.verifyCodeLogin,
						inputParam.getLoginName(),
						inputParam.getVerifyCode());
		if(result.upFlagTrue()){
			//如果用户不存在 直接创建一个无密码的用户
			MemberLoginSupport memberLoginSupport =new MemberLoginSupport();
			memberLoginSupport.checkOrCreateUserByMobile(inputParam.getLoginName(), "SI2011");
		}
		
		
		
		//获取用户信息
		MLoginInput mLoginInput = new MLoginInput();
		mLoginInput.setLoginName(inputParam.getLoginName());
		mLoginInput.setLoginGroup(MemberConst.LOGIN_GROUP_DEFAULT);
		mLoginInput.setManageCode(this.getManageCode());
		mLoginInput.setLoginType(ELoginType.LoginName);
		
		MLoginResult userInfo = new MemberLoginSupport().doLogin(mLoginInput);
		GroupLoginResult resultData = new GroupLoginResult();
		resultData.setUserToken(userInfo.getUserToken());
		resultData.setMemberCode(userInfo.getMemberCode());
		resultData.setResultCode(userInfo.getResultCode());
		resultData.setResultMessage(userInfo.getResultMessage());
		return resultData;
	}

}
