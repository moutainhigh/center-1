package com.cmall.systemcenter.api;

import com.cmall.systemcenter.message.SendMessageBase;
import com.cmall.systemcenter.model.MsgSendNewInput;
import com.cmall.systemcenter.model.MsgSendNewResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;

/***
 * 发送短信信息
 * @author jlin
 *
 */
public class MsgSendNew extends RootApiForManage<MsgSendNewResult, MsgSendNewInput> {

	public MsgSendNewResult Process(MsgSendNewInput inputParam, MDataMap mRequestMap) {
		MsgSendNewResult msgSendResult = new MsgSendNewResult();
		String mobile = inputParam.getMobile();
		String content = inputParam.getContent();
		String send_source = inputParam.getSend_source();
		
		//发送短信
		SendMessageBase messageBase=new SendMessageBase();
		messageBase.sendMessage(mobile, content,send_source);

		return msgSendResult;
	}

}
