package com.cmall.groupcenter.third.api;

import com.cmall.membercenter.model.ForgetVerifyInput;
import com.cmall.systemcenter.enumer.EVerifyCodeTypeEnumer;
import com.cmall.systemcenter.support.VerifySupport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class GroupForgetVerifyPasswordApi extends
		RootApiForManage<RootResultWeb, ForgetVerifyInput> {

	public RootResultWeb Process(ForgetVerifyInput inputParam, MDataMap mRequestMap) {
		RootResultWeb rootResultWeb = new RootResultWeb();

		// 判断验证码是否正确
		if (rootResultWeb.upFlagTrue()) {
			VerifySupport verifySupport = new VerifySupport();
			EVerifyCodeTypeEnumer eVerifyCodeTypeEnumer = EVerifyCodeTypeEnumer.ForgetPassword;
			if (inputParam.getSend_type().equals("reginster")) {
				eVerifyCodeTypeEnumer = EVerifyCodeTypeEnumer.MemberReginster;
			} else if (inputParam.getSend_type().equals("login")) {
				eVerifyCodeTypeEnumer = EVerifyCodeTypeEnumer.MemberLogin;
			} else if (inputParam.getSend_type().equals("forgetpassword")) {
				eVerifyCodeTypeEnumer = EVerifyCodeTypeEnumer.ForgetPassword;
			} else if (inputParam.getSend_type().equals("updateMemInfor")) {
				eVerifyCodeTypeEnumer = EVerifyCodeTypeEnumer.UpdateMemInfor;
			} else if (inputParam.getSend_type().equals("binding")) {
				eVerifyCodeTypeEnumer = EVerifyCodeTypeEnumer.Binding;
			} else if (inputParam.getSend_type().equals("weixinbind")) {
				eVerifyCodeTypeEnumer = EVerifyCodeTypeEnumer.WeiXinBind;
			} else {
				eVerifyCodeTypeEnumer = EVerifyCodeTypeEnumer.ForgetPassword;
			}
			rootResultWeb.inOtherResult(verifySupport.checkVerifyCodeByType(
					eVerifyCodeTypeEnumer,
					inputParam.getLogin_name(), inputParam.getVerify_code(),inputParam.getClient_source())

			);
		}

		return rootResultWeb;
	}

}
