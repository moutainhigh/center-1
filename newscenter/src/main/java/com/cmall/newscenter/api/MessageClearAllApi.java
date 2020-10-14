package com.cmall.newscenter.api;

import com.cmall.newscenter.model.MessageClearAllInput;
import com.cmall.newscenter.model.MessageClearAllResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
/**
 * 消息 - 清空
 *@author yangrong
 * date 2014-7-21
 * @version 1.0
 */
public class MessageClearAllApi extends RootApiForToken<MessageClearAllResult, MessageClearAllInput> {

	public MessageClearAllResult Process(MessageClearAllInput inputParam,
			MDataMap mRequestMap) {
		
		MessageClearAllResult result = new MessageClearAllResult();
		
		if(result.upFlagTrue()){
			
			if(inputParam.getMessage_type()==0){
				
				MDataMap mWhereMap=new MDataMap();
				
				if(!"".equals(inputParam.getMessage_code())&&inputParam.getMessage_code()!=null){
					
					mWhereMap.put("message_code", inputParam.getMessage_code());
					
					mWhereMap.put("message_type","449746640001");
					
					mWhereMap.put("member_send",getUserCode());
					
					mWhereMap.put("is_delete","1");//以清空
					
					DbUp.upTable("nc_system_message").dataUpdate(mWhereMap, "is_delete","message_code,member_send");
				}else {
					
					mWhereMap.put("message_type","449746640001");
					
					mWhereMap.put("member_send",getUserCode());
					
					mWhereMap.put("is_delete","1");//以清空
					
					DbUp.upTable("nc_system_message").dataUpdate(mWhereMap, "is_delete","member_send");
				}
				
				
				
				
				
			}else if(inputParam.getMessage_type()==1){
				
				MDataMap mWhereMap=new MDataMap();
				
				if(!"".equals(inputParam.getMessage_code())&&inputParam.getMessage_code()!=null){
					
					mWhereMap.put("message_code", inputParam.getMessage_code());
					
                    mWhereMap.put("member_send",getUserCode());
					
					mWhereMap.put("message_type","449746640002");
					
					mWhereMap.put("is_delete","1");
					
					DbUp.upTable("nc_message_info").dataUpdate(mWhereMap, "is_delete","message_code,member_send");
					
				}else {
					
					mWhereMap.put("member_send",getUserCode());
					
					mWhereMap.put("message_type","449746640002");
					
					mWhereMap.put("is_delete","1");
					
					DbUp.upTable("nc_message_info").dataUpdate(mWhereMap, "is_delete","member_send");
				}
				
				
			}else if(inputParam.getMessage_type()==2){
				
				MDataMap mWhereMap=new MDataMap();
				
				if(!"".equals(inputParam.getMessage_code())&&inputParam.getMessage_code()!=null){
					
					mWhereMap.put("message_code", inputParam.getMessage_code());
					
                    mWhereMap.put("member_send",getUserCode());
					
					mWhereMap.put("message_type","449746640003");
					
					mWhereMap.put("is_delete","1");
					
					DbUp.upTable("nc_message_info").dataUpdate(mWhereMap, "is_delete","message_code,member_send");	
					
				}else {
					
					mWhereMap.put("member_send",getUserCode());
					
					mWhereMap.put("message_type","449746640003");
					
					mWhereMap.put("is_delete","1");
					
					DbUp.upTable("nc_message_info").dataUpdate(mWhereMap, "is_delete","member_send");	
				}
				
			}
			
		}
		return result;
	}

}
