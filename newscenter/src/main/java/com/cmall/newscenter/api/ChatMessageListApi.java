package com.cmall.newscenter.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cmall.newscenter.model.ChartMessageListResult;
import com.cmall.newscenter.model.ChatMessageInput;
import com.cmall.newscenter.model.ChatMessageQueryInput;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 聊天-查询Api
 * 
 * @author wangzx 
 * @version1.0
 */
public class ChatMessageListApi extends
		RootApiForToken<ChartMessageListResult, ChatMessageQueryInput> {
	
	public ChartMessageListResult Process(ChatMessageQueryInput inputParam,
			MDataMap mRequestMap) {
		List<ChatMessageInput> list = new ArrayList<ChatMessageInput>();
		ChatMessageInput cm=null;
		StringBuffer buf = new StringBuffer();
		buf.append("SELECT * FROM nc_chat_message where sender_id=:sender_id  and receiver_id=:receiver_id and unix_timestamp(send_time) <= '"+inputParam.getTimestamp()+"' limit "+inputParam.getQsize() );
		List<Map<String, Object>> messageList = DbUp.upTable("nc_chat_message").dataSqlList(buf.toString(), new MDataMap("sender_id",inputParam.getSenderId(),"receiver_id",inputParam.getReceiverId()));
		
		if(messageList!=null  && messageList.size()>0) 
		for(Map<String, Object> message:messageList){
			cm= new  ChatMessageInput();
			cm.setSenderId(String.valueOf(message.get("sender_id")));
			cm.setReceiverId(String.valueOf(message.get("receiver_id")));
			cm.setMessType(String.valueOf(message.get("mess_type")));
			cm.setSendTime(String.valueOf(message.get("send_time")));
			cm.setReceiverTime(String.valueOf(message.get("receiver_time")));
			cm.setChatContent(String.valueOf(message.get("chat_content")));
			cm.setSendStatus(Integer.parseInt(message.get("send_status").toString()));
			cm.setReceiverStatus(Integer.parseInt(message.get("receiver_status").toString()));
			cm.setMessDirection(Integer.parseInt(message.get("mess_direction").toString()));
			cm.setMessOccasion(Integer.parseInt(message.get("mess_occasion").toString()));
			list.add(cm);
		}
		ChartMessageListResult result = new ChartMessageListResult();
		result.setList(list);
		return result;
	}

	
	

}
