package com.cmall.newscenter.api;

import java.util.List;

import com.cmall.newscenter.model.MessageReadInput;
import com.cmall.newscenter.model.MessageReadResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.webapi.RootApiForToken;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 消息 - 标记已读
 * @author yangrong
 * date 2014-7-21
 * @version 1.0
 */
public class MessageReadApi extends RootApiForToken<MessageReadResult, MessageReadInput> {

	public MessageReadResult Process(MessageReadInput inputParam,
			MDataMap mRequestMap) {
		
		MessageReadResult result = new MessageReadResult();
		
		RootResultWeb rootResultWeb = new RootResultWeb();
		
		if(result.upFlagTrue()){
			
			/*根据信息编号查询信息*/
			
			MDataMap dataMap= DbUp.upTable("nc_message_info").one("message_code",inputParam.getMessage(),"manage_code",getManageCode());
				
				if(dataMap!=null){
					
					if(dataMap.get("message_type").equals("449746640001")){
						
						MDataMap messMap = DbUp.upTable("nc_system_message").one("message_code",inputParam.getMessage(),"manage_code",getManageCode(),"member_send",getUserCode());
							
							if(messMap!=null){
							
								messMap.put("is_read", "1");
								
								DbUp.upTable("nc_system_message").dataUpdate(messMap, "is_read", "zid");
								
							}else{
								
								rootResultWeb.inErrorMessage(934205102);
								
							}
							
						}else {
						
						//改成已读
						dataMap.put("is_read","1");
						
						/*更新数据*/
						DbUp.upTable("nc_message_info").dataUpdate(dataMap, "is_read", "zid");
					}
					
				}else{
					
					rootResultWeb.inErrorMessage(934205102);
					
				}
				
		}
			
		return result;
	}

}
