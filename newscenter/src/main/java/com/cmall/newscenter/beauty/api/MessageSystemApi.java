package com.cmall.newscenter.beauty.api;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cmall.newscenter.beauty.model.MessageSystemInput;
import com.cmall.newscenter.beauty.model.MessageSystemResult;
import com.cmall.newscenter.beauty.model.SystemMessage;
import com.cmall.newscenter.model.PageResults;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
/**
 * 消息 - 系统消息
 * @author houwen
 * date 2014-9-15
 * @version 1.0
 */
public class MessageSystemApi extends RootApiForToken<MessageSystemResult, MessageSystemInput> {

	public MessageSystemResult Process(MessageSystemInput inputParam,
			MDataMap mRequestMap) {
		MessageSystemResult result = new MessageSystemResult();
		
		if(result.upFlagTrue()){
			
			MDataMap mWhereMap=new MDataMap();
			
			//对应小编提醒 ,例通知
			//String message_type = "449746910001,449746910002";
			//查出系统消息列表          
			List<Map<String, Object>> list = null ;
			String sql = "select * from nc_system_message n where n.is_delete='"+0+"' and n.manage_code ='"+getManageCode()+"'  and n.message_type in ('449746910001','449746910002' ) and n.member_send in ('','"+getUserCode()+"') and date_format(n.send_time,'%Y-%m-%d %H:%i:%s')<date_format(NOW(),'%Y-%m-%d %H:%i:%s') order by is_read asc,send_time desc";
			list = DbUp.upTable("nc_system_message").dataSqlList(sql,mWhereMap);
			
			if(list!=null){
				int totalNum = list.size();
				int offset = inputParam.getPaging().getOffset();//起始页
				int limit = inputParam.getPaging().getLimit();//每页条数
				int startNum = limit*offset;//开始条数
				int endNum = startNum+limit;//结束条数
				int more = 1;//有更多数据
				Boolean flag = true;
				if(startNum<totalNum){
					flag = false;
				}
				if(endNum>=totalNum){
					if(0==totalNum){
						startNum = 0;
					}
					endNum = totalNum;
					more = 0;
				}
				
				//分页信息
				PageResults pageResults = new PageResults();
				pageResults.setTotal(totalNum);
				pageResults.setCount(endNum-startNum);
				pageResults.setMore(more);
				result.setPaged(pageResults);
				if(!flag){
			if(list.size()!=0){
				
				List<Map<String, Object>> subList = list.subList(startNum, endNum);
				//是查询向所有人发送的系统消息和单独向登录人发送的系统消息
				for(int i = 0;i<subList.size();i++){
					
					SystemMessage message = new SystemMessage();
					message.setMessage_code( subList.get(i).get("message_code").toString());
					message.setMessage_type( subList.get(i).get("message_type").toString());
					message.setMessage_info( subList.get(i).get("message_info").toString());
					PostListApi pApi = new PostListApi();
					SimpleDateFormat sf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
					String time = pApi.transform(subList.get(i).get("send_time").toString(), sf.format(new Date()));
					message.setSend_time(time);
					message.setIs_read(subList.get(i).get("is_read").toString());
					result.getMessages().add(message);
								
					}
			}
				}
			}
		}
		
		return result;
	}

}
