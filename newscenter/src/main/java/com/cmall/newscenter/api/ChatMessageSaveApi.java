package com.cmall.newscenter.api;


import com.cmall.newscenter.model.ChartMessageResult;
import com.cmall.newscenter.model.ChatMessageInput;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 聊天-保存Api
 * 
 * @author wangzx 
 * @version1.0
 */
public class ChatMessageSaveApi extends
		RootApiForToken<ChartMessageResult, ChatMessageInput> {


public ChartMessageResult Process(ChatMessageInput inputParam, MDataMap mRequestMap) {
		ChartMessageResult result = new ChartMessageResult();
		/*DbUp.upTable("nc_chat_message").insert("uid",WebHelper.upUuid(),
			        "sender_id",inputParam.getSenderId(),
			        "receiver_id",inputParam.getReceiverId(),
			        "member_code",this.getUserCode(),
			        "chat_content",inputParam.getChatContent()
		        );*/
		
		DbUp.upTable("nc_chat_message").dataInsert(
				new MDataMap("sender_id",inputParam.getSenderId(),
						"receiver_id",inputParam.getReceiverId(),
						"chat_content",inputParam.getChatContent(),
						"mess_type",inputParam.getMessType(),
						"receiver_time",inputParam.getReceiverTime(),
						"send_time",inputParam.getSendTime(),
						"send_status",String.valueOf(inputParam.getSendStatus()),
						"receiver_status",String.valueOf(inputParam.getReceiverStatus()),
						"mess_direction",String.valueOf(inputParam.getMessDirection()),
						"mess_occasion",String.valueOf(inputParam.getMessOccasion() )));
		
		return result;
	}


	

}
