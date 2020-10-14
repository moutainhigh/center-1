package com.cmall.membercenter.api;


import com.cmall.membercenter.model.ForgetInput;
import com.cmall.membercenter.support.MemberInfoSupport;
import com.cmall.systemcenter.enumer.EVerifyCodeTypeEnumer;
import com.cmall.systemcenter.support.VerifySupport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForMember;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class ForgetPassword extends
		RootApiForMember<RootResultWeb, ForgetInput> {

	public RootResultWeb Process(ForgetInput inputParam, MDataMap mRequestMap) {
		RootResultWeb rootResultWeb = new RootResultWeb();

		// 如果用户已登陆 则直接修改密码
		if (getFlagLogin()) {

			rootResultWeb.inOtherResult(new MemberInfoSupport().forgetPassword(
					inputParam.getLogin_name(), getManageCode(),
					inputParam.getPassword()));

		} else {

			// 判断验证码对不对
			if (rootResultWeb.upFlagTrue()) {
				VerifySupport verifySupport = new VerifySupport();
				rootResultWeb.inOtherResult(verifySupport
						.checkVerifyCodeByType(
								EVerifyCodeTypeEnumer.ForgetPassword,
								inputParam.getLogin_name(),
								inputParam.getVerify_code()));
				// 修改密码
				if (rootResultWeb.upFlagTrue()) {
					rootResultWeb.inOtherResult(new MemberInfoSupport()
							.forgetPassword(inputParam.getLogin_name(),
									getManageCode(), inputParam.getPassword()));
				}
			} 
		}

		return rootResultWeb;
	}

}
