package com.cmall.newscenter.api;



import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.MessageUnReadInput;
import com.cmall.newscenter.model.MessageUnReadResult;
import com.cmall.newscenter.util.DataPaging;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
/**
 * 消息 - 未读消息数
 * @author yangrong
 * date 2014-7-21
 * @version 1.0
 */
public class MessageUnReadApi extends RootApiForToken<MessageUnReadResult, MessageUnReadInput> {

	public MessageUnReadResult Process(MessageUnReadInput inputParam,
			MDataMap mRequestMap) {
		MessageUnReadResult result = new MessageUnReadResult();
		if(result.upFlagTrue()){
			
			
			MDataMap mWhereMap=new MDataMap();
			
			mWhereMap.put("member_send",getUserCode());
			mWhereMap.put("manage_code",getManageCode());
			mWhereMap.put("is_read","0");//未读
			mWhereMap.put("is_delete","0");//未删除
			//查出消息列表
			MPageData mPageData=DataPaging.upPageData("nc_message_info", "", "", mWhereMap, inputParam.getPaging());
			
			Integer count = 0;
			
			for( MDataMap mDataMap: mPageData.getListData()){
				
				if(mDataMap.get("message_type").equals("449746640001")){
					
					mWhereMap.put("message_type", "449746640001");
					
					count = DbUp.upTable("nc_system_message").dataCount("", mWhereMap);
					
				}else {
					
					count++;
				}
				
			}
			
				result.setCount(count.toString());
			}
		
		return result;
	}
}
