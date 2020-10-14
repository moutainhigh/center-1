package com.cmall.newscenter.api;

import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.MessageReadAllInput;
import com.cmall.newscenter.model.MessageReadAllResult;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.util.DataPaging;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
/**
 * 消息 -全部已读
 * @author yangrong
 * date 2014-7-21
 * @version 1.0
 */
public class MessageReadAllApi extends RootApiForToken<MessageReadAllResult, MessageReadAllInput> {

	public MessageReadAllResult Process(MessageReadAllInput inputParam,
			MDataMap mRequestMap) {
		MessageReadAllResult result = new MessageReadAllResult();
		if(result.upFlagTrue()){
			
			
			MDataMap mWhereMap=new MDataMap();
			
			mWhereMap.put("member_send",getUserCode());
			mWhereMap.put("manage_code",getManageCode());
			
			if(inputParam.getMessage_type()==0){
				
				mWhereMap.put("message_type","449746640001");
				
				//根据类型查出消息列表
				MPageData mPageData=DataPaging.upPageData("nc_system_message", "", "", mWhereMap, new PageOption());
				
				for( MDataMap mDataMap: mPageData.getListData()){
					
					//判断是否已读
					if(mDataMap.get("is_read").equals("0")){
						
						 mDataMap.put("message_type", "449746640001");
						 
						 mDataMap.put("member_send",getUserCode());
						 
						 mDataMap.put("manage_code",getManageCode());
						
						 mDataMap.put("is_read","1");
						 
						 DbUp.upTable("nc_system_message").dataUpdate(mDataMap, "is_read", "zid");
						 
					}
				}
				
				
				
			}else if(inputParam.getMessage_type()==1){
				
				mWhereMap.put("message_type","449746640002");
				
				//根据类型查出消息列表
				MPageData mPageData=DataPaging.upPageData("nc_message_info", "", "", mWhereMap, new PageOption());
				
				for( MDataMap mDataMap: mPageData.getListData()){
					
					//判断是否已读
					if(mDataMap.get("is_read").equals("0")){
						
						 mDataMap.put("is_read","1");
						 DbUp.upTable("nc_message_info").dataUpdate(mDataMap, "is_read", "zid");
						 
					}
				}
				
			}else if(inputParam.getMessage_type()==2){
				
				mWhereMap.put("message_type","449746640003");
				
				//根据类型查出消息列表
				MPageData mPageData=DataPaging.upPageData("nc_message_info", "", "", mWhereMap, new PageOption());
				
				for( MDataMap mDataMap: mPageData.getListData()){
					
					//判断是否已读
					if(mDataMap.get("is_read").equals("0")){
						
						 mDataMap.put("is_read","1");
						 DbUp.upTable("nc_message_info").dataUpdate(mDataMap, "is_read", "zid");
						 
					}
				}
				
			}
			
			
		}
		return result;
	}

}
