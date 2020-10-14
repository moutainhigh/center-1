package com.cmall.membercenter.api;

import com.cmall.membercenter.helper.VerifyCodeUtils;
import com.cmall.membercenter.model.ForgetVerifyInput;
import com.cmall.systemcenter.enumer.EVerifyCodeTypeEnumer;
import com.cmall.systemcenter.support.VerifySupport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.webapi.RootResultWeb;
import com.srnpr.zapweb.websupport.VerifyCodeSupport;

public class ForgetVerifyPassword extends
		RootApiForManage<RootResultWeb, ForgetVerifyInput> {

	public RootResultWeb Process(ForgetVerifyInput inputParam, MDataMap mRequestMap) {
		RootResultWeb rootResultWeb = new RootResultWeb();
		//验证图片验证码
		if(getManageCode().equals("SI2009")){
			String apiInput = mRequestMap.get("api_input");
			String verifyCodeInput = apiInput.substring(apiInput.indexOf("picVerifyCode")+16,apiInput.indexOf("water_code")-3);
			String water_codeInput = apiInput.substring(apiInput.indexOf("water_code")+13,apiInput.length()-2);
			rootResultWeb.inOtherResult(new VerifyCodeSupport().checkVerifyCode(water_codeInput,verifyCodeInput));			
		}
		
		String sendType = inputParam.getSend_type();
		EVerifyCodeTypeEnumer eVerifyCodeTypeEnumer;
		
		
		eVerifyCodeTypeEnumer =sendType!=null && sendType.equals("reginster")  ? EVerifyCodeTypeEnumer.MemberReginster :  EVerifyCodeTypeEnumer.ForgetPassword;
		if(sendType!=null && sendType.equals("verifyCodeLogin") ){
			eVerifyCodeTypeEnumer = EVerifyCodeTypeEnumer.verifyCodeLogin;
		}
		if(sendType!=null &&sendType.equals("agentPassWord")){
			eVerifyCodeTypeEnumer = EVerifyCodeTypeEnumer.agentPassWord;	
		}
		
		// 判断验证码是否正确
		if (rootResultWeb.upFlagTrue()) {
			VerifySupport verifySupport = new VerifySupport();
			rootResultWeb.inOtherResult(verifySupport.checkVerifyCodeByType(
					eVerifyCodeTypeEnumer,
					inputParam.getLogin_name(), inputParam.getVerify_code(),inputParam.getClient_source())

			);
		}

		return rootResultWeb;
	}

}
