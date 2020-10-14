package com.cmall.groupcenter.userinfo.api;

import com.cmall.groupcenter.voice.VoiceCodeUtil;
import com.cmall.systemcenter.enumer.EVerifyCodeTypeEnumer;
import com.cmall.systemcenter.model.MsgSendInput;
import com.cmall.systemcenter.model.MsgSendResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.webmodel.MWebResult;
import com.srnpr.zapweb.websupport.VerifyCodeSupport;

public class GetVoiceCodeApi extends RootApiForManage<MsgSendResult, MsgSendInput>{

	@Override
	public MsgSendResult Process(MsgSendInput inputParam, MDataMap mRequestMap) {
		MsgSendResult msgSendResult = new MsgSendResult();

		if (msgSendResult.upFlagTrue()) {
			if (inputParam.getSend_type().equals("reginster")) {
				

				VerifyCodeSupport codeSupport = new VerifyCodeSupport();
				String waterCode = inputParam.getWater_code();
				String verifyCode=inputParam.getVerify_code();
				MWebResult webResult =codeSupport.checkVerifyCode(waterCode, verifyCode);
				if(webResult.getResultCode()!=1){
					msgSendResult.inOtherResult(webResult);
					return msgSendResult;
				}

				MDataMap mDataMap = DbUp.upTable("mc_login_info").one(
						"manage_code", getManageCode(), "login_name",
						inputParam.getMobile());
				if (mDataMap != null) {

					msgSendResult.inErrorMessage(949702105);

				} else {
					msgSendResult.inOtherResult(new VoiceCodeUtil()
							.sendVerifyCode(
									EVerifyCodeTypeEnumer.voiceCodeMemberReginster,
									inputParam.getMobile(), getManageCode()));
				}

			} else if (inputParam.getSend_type().equals("forgetpassword")) {
				
				VerifyCodeSupport codeSupport = new VerifyCodeSupport();
				String waterCode = inputParam.getWater_code();
				String verifyCode=inputParam.getVerify_code();
				MWebResult webResult =codeSupport.checkVerifyCode(waterCode, verifyCode);
				if(webResult.getResultCode()!=1){
					msgSendResult.inOtherResult(webResult);
					return msgSendResult;
				}
				MDataMap mDataMap = DbUp.upTable("mc_login_info").one(
						"login_name", inputParam.getMobile());
				if (mDataMap != null) {
					msgSendResult
							.inOtherResult(new VoiceCodeUtil()
									.sendVerifyCode(
											EVerifyCodeTypeEnumer.voiceCodeForgetPassword,
											inputParam.getMobile(),
											getManageCode(), 6));

				} else {
					msgSendResult.inErrorMessage(949702106);
				}
			}  
			else {
				msgSendResult.inErrorMessage(949702104);
			}
			
			

		}

		return msgSendResult;
	}

}
