package com.cmall.groupcenter.third.api;

import com.cmall.systemcenter.enumer.EVerifyCodeTypeEnumer;
import com.cmall.systemcenter.message.SendMessageBase;
import com.cmall.systemcenter.message.SendMessageForApp;
import com.cmall.systemcenter.model.MsgSendInput;
import com.cmall.systemcenter.model.MsgSendResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * @author srnpr 短信发送API
 */
public class GroupMsgSendApi extends RootApiForManage<MsgSendResult, MsgSendInput> {

	public MsgSendResult Process(MsgSendInput inputParam, MDataMap mRequestMap) {
		MsgSendResult msgSendResult = new MsgSendResult();

		if (inputParam.getSend_type().equals("reginster")) {

			MDataMap mDataMap = DbUp.upTable("mc_login_info").one(
					"manage_code", getManageCode(), "login_name",
					inputParam.getMobile());
			if (mDataMap != null) {

				msgSendResult.inErrorMessage(949702105);

			} else {
				msgSendResult.inOtherResult(new SendMessageBase()
						.sendVerifyCode(EVerifyCodeTypeEnumer.MemberReginster,
								inputParam.getMobile(), getManageCode()));
			}

		} else if (inputParam.getSend_type().equals("login")) {
			MDataMap mDataMap = DbUp.upTable("mc_login_info").one(
					"manage_code", getManageCode(), "login_name",
					inputParam.getMobile());
			if (mDataMap != null) {
				msgSendResult.inOtherResult(new SendMessageBase()
						.sendVerifyCode(EVerifyCodeTypeEnumer.MemberLogin,
								inputParam.getMobile(), getManageCode(),
								getManageCode().equals("SI2003") ? 4 : 6));
			} else {
				msgSendResult.inErrorMessage(949702106);
			}
		} else if (inputParam.getSend_type().equals("forgetpassword")) {

			MDataMap mDataMap = DbUp.upTable("mc_login_info").one(
					 "login_name",
					inputParam.getMobile());
			if (mDataMap != null) {
				msgSendResult.inOtherResult(new SendMessageBase()
						.sendVerifyCode(EVerifyCodeTypeEnumer.ForgetPassword,
								inputParam.getMobile(), getManageCode(),
								6));

			} else {
				msgSendResult.inErrorMessage(949702106);
			}

		} else if (inputParam.getSend_type().equals("updateMemInfor")) {
			msgSendResult.inOtherResult(new SendMessageBase().sendVerifyCode(
					EVerifyCodeTypeEnumer.UpdateMemInfor,
					inputParam.getMobile(), getManageCode()));
		} else if (inputParam.getSend_type().equals("binding")) {
			msgSendResult.inOtherResult(new SendMessageBase().sendVerifyCode(
					EVerifyCodeTypeEnumer.Binding,
					inputParam.getMobile(), getManageCode()));
		} else if (inputParam.getSend_type().equals("weixinbind")) {
			msgSendResult.inOtherResult(new SendMessageBase().sendVerifyCode(
					EVerifyCodeTypeEnumer.WeiXinBind,
					inputParam.getMobile(), getManageCode()));
		} else {
			msgSendResult.inErrorMessage(949702104);
		}

		return msgSendResult;
	}

}
