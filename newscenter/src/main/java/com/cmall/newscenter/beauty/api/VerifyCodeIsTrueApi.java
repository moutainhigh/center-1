package com.cmall.newscenter.beauty.api;

import com.cmall.systemcenter.enumer.EVerifyCodeTypeEnumer;
import com.cmall.systemcenter.support.VerifySupport;
import com.cmall.newscenter.beauty.model.VerifyCodeIsTrueInput;
import com.cmall.newscenter.beauty.model.VerifyCodeIsTrueResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 判断验证码是否正确Api
 * 
 * @author yangrong date: 2014-09-19
 * @version1.0
 */
public class VerifyCodeIsTrueApi extends RootApiForManage<VerifyCodeIsTrueResult, VerifyCodeIsTrueInput> {

	public VerifyCodeIsTrueResult Process(VerifyCodeIsTrueInput inputParam,MDataMap mRequestMap) {

		VerifyCodeIsTrueResult result = new VerifyCodeIsTrueResult();

		// 设置相关信息
		if (result.upFlagTrue()) {

			VerifySupport support = new VerifySupport();

			String verrfy = "";

			if (inputParam.getType().equals("reginster")) {

				verrfy = support.upLastVerifyCode(EVerifyCodeTypeEnumer.MemberReginster,inputParam.getPhone());

			} else if (inputParam.getType().equals("forgetpassword")) {

				verrfy = support.upLastVerifyCode(EVerifyCodeTypeEnumer.ForgetPassword,inputParam.getPhone());

			} else {
				result.setResultCode(934205141);
				result.setResultMessage(bInfo(934205141));
			}

			if (!verrfy.equals(inputParam.getVerify())) {

				result.setResultCode(934205142);
				result.setResultMessage(bInfo(934205142));
			}

		}
		return result;
	}
}
