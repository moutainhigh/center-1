package com.cmall.newscenter.beauty.api;

import com.cmall.newscenter.beauty.model.MessageStatusInput;
import com.cmall.newscenter.beauty.model.MessageStatusResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;


/**
 * 惠美丽—修改消息状态-》由未读改为已读
 * @author houwen
 * date: 2014-09-29
 * @version1.0
 */
public class UpdateMessageStatusApi extends RootApiForToken<MessageStatusResult,MessageStatusInput > {

	public MessageStatusResult Process(MessageStatusInput inputParam,
			MDataMap mRequestMap) {
		
		MessageStatusResult result = new MessageStatusResult();
		MDataMap mDataMap = new MDataMap();
		// 设置相关信息
		if (result.upFlagTrue()) {
		
		String message_code[] = inputParam.getMessage_code().split(",");
		for(int i =0;i<message_code.length;i++){
			
		//	int count =  DbUp.upTable("nc_system_message").count("message_code",message_code[i]);
			
			MDataMap map = DbUp.upTable("nc_message_info").one("message_code", message_code[i]);
			
			if(map!=null){
				
				if(map.get("message_type").equals("449746910001")||map.get("message_type").equals("449746910002")){
					mDataMap.put("message_code", message_code[i]);
					mDataMap.put("is_read", "1");
					mDataMap.put("member_send", getUserCode());
					
					DbUp.upTable("nc_system_message").dataUpdate(mDataMap, "is_read", "message_code,member_send");
				}else {
					
					mDataMap.put("message_code", message_code[i]);
					mDataMap.put("is_read", "1");
					mDataMap.put("member_send", getUserCode());
					DbUp.upTable("nc_message_info").dataUpdate(mDataMap, "is_read", "message_code,member_send");
					
				}
			}
			
		}
		}
		return result;
	}

}

