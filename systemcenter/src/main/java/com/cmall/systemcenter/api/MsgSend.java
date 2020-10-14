package com.cmall.systemcenter.api;

import org.apache.commons.lang.StringUtils;

import com.cmall.systemcenter.enumer.EVerifyCodeTypeEnumer;
import com.cmall.systemcenter.message.SendMessageBase;
import com.cmall.systemcenter.message.SendMessageForApp;
import com.cmall.systemcenter.model.MsgSendInput;
import com.cmall.systemcenter.model.MsgSendResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basemodel.MStringMap;
import com.srnpr.zapcom.topcache.CacheTempConfigStringMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebSessionHelper;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.webmodel.MWebResult;
import com.srnpr.zapweb.websupport.VerifyCodeSupport;

/**
 * @author srnpr 短信发送API
 */
public class MsgSend extends RootApiForManage<MsgSendResult, MsgSendInput> {

	public MsgSendResult Process(MsgSendInput inputParam, MDataMap mRequestMap) {
		MsgSendResult msgSendResult = new MsgSendResult();

		// 家有汇增加图片验证码校验api_input={"mobile":"18667042582","send_type":"reginster","isCallbackFunc":true,"verifyCode":"2ssdsd"}
		if (getManageCode().equals("SI2009")
				&& (inputParam.getSend_type().equals("reginster") || inputParam
						.getSend_type().equals("forgetpassword"))) {

			msgSendResult.inOtherResult(new VerifyCodeSupport()
					.checkVerifyCode(inputParam.getWater_code(),
							inputParam.getVerify_code()));
		}

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
					msgSendResult.inOtherResult(new SendMessageBase()
							.sendVerifyCode(
									EVerifyCodeTypeEnumer.MemberReginster,
									inputParam.getMobile(), getManageCode(),getManageCode().equals("SI2003") ? 8 : 6));
				}

			} else if (inputParam.getSend_type().equals("login")) {
				MDataMap mDataMap = DbUp.upTable("mc_login_info").one(
						"manage_code", getManageCode(), "login_name",
						inputParam.getMobile());
				if (mDataMap != null) {
					msgSendResult.inOtherResult(new SendMessageBase()
							.sendVerifyCode(EVerifyCodeTypeEnumer.MemberLogin,
									inputParam.getMobile(), getManageCode(),
									getManageCode().equals("SI2003") ? 8 : 6));
				} else {
					msgSendResult.inErrorMessage(949702106);
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
							.inOtherResult(new SendMessageBase()
									.sendVerifyCode(
											EVerifyCodeTypeEnumer.ForgetPassword,
											inputParam.getMobile(),
											getManageCode(), getManageCode().equals("SI2003") ? 8 : 6));

				} else {
					msgSendResult.inErrorMessage(949702106);
				}

			} else if (inputParam.getSend_type().equals("updateMemInfor")) {
				msgSendResult.inOtherResult(new SendMessageBase()
						.sendVerifyCode(EVerifyCodeTypeEnumer.UpdateMemInfor,
								inputParam.getMobile(), getManageCode()));
			} else if (inputParam.getSend_type().equals("binding")) {
				msgSendResult.inOtherResult(new SendMessageBase()
						.sendVerifyCode(EVerifyCodeTypeEnumer.Binding,
								inputParam.getMobile(), getManageCode()));
			} else if (inputParam.getSend_type().equals("weixinbind")) {
				msgSendResult.inOtherResult(new SendMessageBase()
						.sendVerifyCode(EVerifyCodeTypeEnumer.WeiXinBind,
								inputParam.getMobile(), getManageCode()));
			}  
			else if (inputParam.getSend_type().equals("verifyCodeLogin")) {
				VerifyCodeSupport codeSupport = new VerifyCodeSupport();
				String waterCode = inputParam.getWater_code();
				String verifyCode=inputParam.getVerify_code();
				String mobile = inputParam.getMobile();
				if(!StringUtils.isBlank(verifyCode)){
					//如果输入图片验证码正确 ，可以发送短信验证码了
					mobile=mobile+"_"+EVerifyCodeTypeEnumer.verifyCodeLogin;
					MWebResult webResult =codeSupport.checkVerifyCode(waterCode, verifyCode);
					if(webResult.getResultCode()!=1){
						msgSendResult.inOtherResult(webResult);
						return msgSendResult;
					}
				}
				
				msgSendResult.inOtherResult(new SendMessageBase()
				.sendVerifyCode(EVerifyCodeTypeEnumer.verifyCodeLogin,
						mobile, getManageCode()));
				
			} else if(inputParam.getSend_type().equals("agentPassWord")){
				//嘉玲代理商发送短信
				VerifyCodeSupport codeSupport = new VerifyCodeSupport();
				String waterCode = inputParam.getWater_code();
				String verifyCode=inputParam.getVerify_code();
				MDataMap mDataMap = DbUp.upTable("nc_agency").one(
						"agent_mobilephone", inputParam.getMobile());
				if (mDataMap != null) {
					msgSendResult
							.inOtherResult(new SendMessageBase()
									.sendVerifyCode(
											EVerifyCodeTypeEnumer.agentPassWord,
											inputParam.getMobile(),
											getManageCode(), 6));

				} else {
					msgSendResult.inErrorMessage(949702115);
				}
				
			}
			else {
				msgSendResult.inErrorMessage(949702104);
			}
			
			

		}

		return msgSendResult;
	}

}
