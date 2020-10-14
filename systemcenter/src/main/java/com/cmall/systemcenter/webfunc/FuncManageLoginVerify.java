package com.cmall.systemcenter.webfunc;

import org.apache.commons.lang.StringUtils;

import com.cmall.systemcenter.enumer.EVerifyCodeTypeEnumer;
import com.cmall.systemcenter.support.VerifySupport;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 超级管理员登陆(需验证手机验证码)
 * 
 * @author lgij
 * 
 */
public class FuncManageLoginVerify extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		if (mResult.upFlagTrue()) {

			// 开始验证用户名密码
			if (mSubMap.containsKey("login_name")
					&& mSubMap.containsKey("login_pass")
					&& StringUtils.isNotEmpty(mSubMap.get("login_name"))
					&& StringUtils.isNotEmpty(mSubMap.get("login_pass"))) {
				
			} else {
				mResult.inErrorMessage(969905013);
			}
		}

		String sLoginName = mSubMap.get("login_name");

		String sPassword = mSubMap.get("login_pass");
		
		String mobileVerify = mSubMap.get("mobile_verify");	//手机验证码
		
		if (mResult.upFlagTrue()) {
			mResult = UserFactory.INSTANCE.userLogin(
					sLoginName, sPassword);
		}
			// 校验验证码
			if (mResult.upFlagTrue()) {
				MDataMap mUserInfo = DbUp.upTable("za_userinfo").one("user_name",
						sLoginName);
				//449747110002:是，449747110001：否
//				if ("449747110002".equals(mUserInfo.get("switch_mobile"))) {
//					
//					//开始验证手机验证码是否空
//					if (mSubMap.containsKey("mobile_verify")
//							&& StringUtils.isNotEmpty(mSubMap.get("mobile_verify"))) {
//					} else {
//						mResult.inErrorMessage(969913310);
//					}
//					if (mResult.upFlagTrue()) {
//						if (StringUtils.isNotBlank(mUserInfo.get("mobile"))) {
//							String verifyType=VerifySupport.upVerifyTypeByEnumer(EVerifyCodeTypeEnumer.MemberLogin);
//							mResult = new VerifySupport().checkVerifyCodeByMoreType(
//									verifyType, mUserInfo.get("mobile"),mobileVerify);
//						}else{
//							//请联系管理员维护此账号的手机号
//							mResult.inErrorMessage(969913312);
//						}
//					}
//				}
				
				//改为微信验证
				if ("449747110002".equals(mUserInfo.get("switch_wechat"))) {
//					
					//开始验证验证码是否空
					if (mSubMap.containsKey("mobile_verify")
							&& StringUtils.isNotEmpty(mSubMap.get("mobile_verify"))) {
					} else {
						mResult.inErrorMessage(969913310);
					}
					if (mResult.upFlagTrue()) {
						if (StringUtils.isNotBlank(mUserInfo.get("wechat_openid"))) {
							String verifyType=VerifySupport.upVerifyTypeByEnumer(EVerifyCodeTypeEnumer.MemberLogin);
							mResult = new VerifySupport().checkVerifyCodeByMoreTypeForWeChat(
									verifyType, mUserInfo.get("wechat_openid"),mobileVerify);
						}else{
							//请联系管理员维护此账号绑定的微信号
							mResult.inErrorMessage(969913313);
						}
					}
				}
		}
		
		// 获取用户信息
		if (mResult.upFlagTrue()) {
				mResult.setResultType("116018010");

				JsonHelper<MUserInfo> userJsonHelper = new JsonHelper<MUserInfo>();

				String sUserInfoString = userJsonHelper
						.ObjToString(UserFactory.INSTANCE.create());

				mResult.setResultObject(FormatHelper.formatString(
						bConfig("zapweb.login_success_js"), sUserInfoString));

			} 

			/*
			 * MDataMap mUserInfo = DbUp.upTable("za_userinfo").one("user_name",
			 * sLoginName,"flag_enable", "1");
			 * 
			 * if (mUserInfo != null &&
			 * mUserInfo.get("flag_enable").equals("1")) {
			 * 
			 * } else { mResult.inErrorMessage(969905014); }
			 */

//		}

		return mResult;

	}
}
