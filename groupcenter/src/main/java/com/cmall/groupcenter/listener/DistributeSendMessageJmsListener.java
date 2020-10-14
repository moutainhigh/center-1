package com.cmall.groupcenter.listener;

import com.cmall.groupcenter.groupapp.model.RongYunSingleChatBean;
import com.cmall.groupcenter.groupapp.service.RongYunService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootResultWeb;
import com.srnpr.zapzero.root.RootJmsListenser;

/**
 * 微公社下单，退货时发送单聊消息的监听
 * @author GaoYang
 *
 */
public class DistributeSendMessageJmsListener extends RootJmsListenser{

	@Override
	public boolean onReceiveText(String sMessage, MDataMap mPropMap) {
		boolean ret = true;
		RongYunSingleChatBean bean=new RongYunSingleChatBean();
		RongYunService rongYunService=new RongYunService();
		
		String fromUseId = mPropMap.get("fromUserId");
		String toUseId = mPropMap.get("toUserId");
		String objectName = mPropMap.get("objectName");
		String content = mPropMap.get("content");
		
		bean.setFromUserId(fromUseId);
		bean.setToUserId(toUseId);
		bean.setObjectName(objectName);
		bean.setContent(content);
		//给上级发消息
		RootResultWeb resultWeb = rongYunService.singleChatMessageSend(bean);
		if(resultWeb.getResultCode() != 1){
			ret = false;//发送不成功
		}
		
		return ret;
	}

}
