package com.cmall.newscenter.beauty.api;


import java.util.List;
import java.util.Map;

import com.cmall.newscenter.beauty.model.MessageSystemNewInput;
import com.cmall.newscenter.beauty.model.MessageSystemNewResult;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.util.DataPaging;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
/**
 * 消息 - 姐妹圈首页获取是否有最新消息
 * @author houwen
 * date 2014-9-29
 * @version 1.0
 */
public class MessageSystemNewApi extends RootApiForToken<MessageSystemNewResult, MessageSystemNewInput> {

	public MessageSystemNewResult Process(MessageSystemNewInput inputParam,
			MDataMap mRequestMap) {
		MessageSystemNewResult result = new MessageSystemNewResult();
		
		if(result.upFlagTrue()){
			
			MDataMap mWhereMap=new MDataMap();
			
			MDataMap mSystemWhereMap=new MDataMap();
			
            mWhereMap.put("member_send",getUserCode());   
			
			mWhereMap.put("is_delete","0");
			mWhereMap.put("manage_code",getManageCode());
			int n = 0;
			//查询跟我相关的消息       
			
			String sql = "select * from nc_message_info n where n.is_delete='0' and n.manage_code ='"+getManageCode()+"' and n.message_type in ('449746920001','449746920002','449746920003','449746920004') and n.member_send in ('"+getUserCode()+ "') and n.member_code not in ('"+getUserCode()+"')";
			List<Map<String, Object>> list = DbUp.upTable("nc_message_info").dataSqlList(sql,mWhereMap);
			
			if(list.size()!=0){
				
				for( int i = 0;i<list.size();i++){
					
					if(list.get(i).get("is_read").toString().equals("0")){
							n++;
					}
					}
			}
			
		
			
			mSystemWhereMap.put("member_send",getUserCode());   
			
			mSystemWhereMap.put("is_delete","0");
			mSystemWhereMap.put("manage_code",getManageCode());
			
			String sqls = "select * from nc_system_message n where n.is_delete='0' and n.manage_code ='"+getManageCode()+"'  and n.message_type in ('449746910001','449746910002' ) and n.member_send in ('"+getUserCode()+ "')";
			List<Map<String, Object>> lists = DbUp.upTable("nc_system_message").dataSqlList(sqls,mSystemWhereMap);

			
			if(lists.size()!=0){
				for( int j =0;j<lists.size();j++){
						if("0".equals(lists.get(j).get("is_read").toString())){
							n++;
						}
				}
			}
			
			if(n>0){
				result.setIs_read("1"); //有最新消息
			}else{
				result.setIs_read("0");//没有最新消息
			}
			result.setCount(String.valueOf(n)); //新消息数量 
		}
		return result;
	}

}
