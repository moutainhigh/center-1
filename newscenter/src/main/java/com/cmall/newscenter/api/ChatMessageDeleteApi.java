package com.cmall.newscenter.api;


import java.util.List;
import java.util.Map;

import com.cmall.newscenter.model.ChartMessageResult;
import com.cmall.newscenter.model.ChatMessageDeleteInput;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 聊天信息-删除api
 * 
 * @author wangzx 
 * @version1.0
 */
public class ChatMessageDeleteApi extends
		RootApiForToken<ChartMessageResult, ChatMessageDeleteInput> {
	
	public ChartMessageResult Process(ChatMessageDeleteInput inputParam,
			MDataMap mRequestMap) {
		String sDeleteSql = "sender_id='" + inputParam.getSenderId() +"' and receiver_id='" +inputParam.getReceiverId()+"' and unix_timestamp(send_time)='"+ inputParam.getTimestamp() +"'"  ;
		ChartMessageResult result = new ChartMessageResult();
		int count = DbUp.upTable("nc_chat_message").dataDelete(sDeleteSql, null, null);
		if(count==0){
			result.setResultCode(934205153);
			result.setResultMessage(bInfo(934205153));
		}
		return result;
	}

	
	

}
