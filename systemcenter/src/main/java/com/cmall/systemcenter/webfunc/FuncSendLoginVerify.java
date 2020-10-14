package com.cmall.systemcenter.webfunc;

import org.apache.commons.lang.StringUtils;

import com.cmall.systemcenter.common.AppConst;
import com.cmall.systemcenter.enumer.EVerifyCodeTypeEnumer;
import com.cmall.systemcenter.message.SendMessageBase;
import com.cmall.systemcenter.support.VerifySupport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 发送登陆验证码
 * 
 * @author lgij
 * 
 */
public class FuncSendLoginVerify extends RootFunc {

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
		if (mResult.upFlagTrue()) {
			mResult = UserFactory.INSTANCE.userLogin(
					sLoginName, sPassword);
		}
		// 发送验证码
//		String mobile = "";
		String weChatOpenid = "";		//微信号的openid
		if (mResult.upFlagTrue()) {
			MDataMap mUserInfo = DbUp.upTable("za_userinfo").one("user_name",sLoginName);
//			mobile = mUserInfo.get("mobile");
//			if (StringUtils.isNotBlank(mobile)) {
//				mResult = new SendMessageBase()
//						.sendVerifyCode(EVerifyCodeTypeEnumer.MemberLogin,
//								mobile, AppConst.MANAGE_CODE_HOMEHAS,
//								6,600);
//			}else{
				//请联系管理员维护此账号的手机号
//				mResult.inErrorMessage(969913312);
//			}
			/**
			 * 2016/07/15 blw要求通过微信发送登录验证码
			 */
			
			weChatOpenid = mUserInfo.get("wechat_openid");
			if (StringUtils.isNotBlank(weChatOpenid)) {
				mResult = new VerifySupport().sendVerifyCodeForWeChat(VerifySupport.upVerifyTypeByEnumer(EVerifyCodeTypeEnumer.MemberLogin),weChatOpenid,300,6);
			}else{
//				请联系管理员维护此账号绑定的微信号
				mResult.inErrorMessage(969913313);
			}
		}
		
		// 最后给用户提示发送已经成功
//		if (mResult.upFlagTrue()) {
//			if (mobile.length() > 4) {
//				mResult.inErrorMessage(969913311,mobile.substring(mobile.length()-4, mobile.length()));
//			}else{
//				mResult.inErrorMessage(969913311,mobile);
//			}
//		}
		if (mResult.upFlagTrue()) {
			mResult.inErrorMessage(969913314);
		}
		return mResult;

	}
}
