package com.cmall.newscenter.api;

import java.math.BigInteger;
import java.util.List;

import com.cmall.newscenter.model.MessageSystemInput;
import com.cmall.newscenter.model.MessageSystemResult;
import com.cmall.newscenter.model.PageResults;
import com.cmall.newscenter.model.SystemMessage;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
/**
 * 消息 - 系统消息
 * @author yangrong
 * date 2014-7-21
 * @version 1.0
 */
public class MessageSystemApi extends RootApiForToken<MessageSystemResult, MessageSystemInput> {

	public MessageSystemResult Process(MessageSystemInput inputParam,
			MDataMap mRequestMap) {
		MessageSystemResult result = new MessageSystemResult();
		
		if(result.upFlagTrue()){
			
			MDataMap mWhereMap=new MDataMap();
			
			mWhereMap.put("message_type","449746640001");
			
			mWhereMap.put("member_send",getUserCode());
			
			mWhereMap.put("is_delete","0");
			
			mWhereMap.put("manage_code",getManageCode());
			
			/*获取当前时间*/
			String currentTime = FormatHelper.upDateTime();
			
			List<MDataMap> mDataMaps = DbUp.upTable("nc_system_message")
			.queryAll("", "-create_time", "send_time<='"+currentTime+"' and message_type =:message_type and member_send=:member_send and is_delete=:is_delete and manage_code=:manage_code ", mWhereMap);
			
			 
				int totalNum = mDataMaps.size();
				int offset = inputParam.getPaging().getOffset();//起始页
				int limit = inputParam.getPaging().getLimit();//每页条数
				int startNum = limit*offset;//开始条数
				int endNum = startNum+limit;//结束条数
				int more = 1;//有更多数据
				if(endNum>totalNum){
					endNum = totalNum;
					more = 0;
				}
				//如果起始条件大于总数则返回0条数据
				if(startNum>totalNum){
					startNum = 0;
					endNum = 0;
					more = 0;
				}
				//分页信息
				PageResults pageResults = new PageResults();
				pageResults.setTotal(totalNum);
				pageResults.setCount(endNum-startNum);
				pageResults.setMore(more);
				result.setPaged(pageResults);
				//返回系统消息列表
				List<MDataMap> subList = mDataMaps.subList(startNum, endNum);
			 
			if(subList.size()!=0){
				int unread_count = 0;
				for( MDataMap mDataMap: subList){
					if("0".equals(mDataMap.get("is_read"))){
						//统计未读消息
						unread_count++;
					}
							
						SystemMessage message = new SystemMessage();
						
						message.setId(mDataMap.get("message_code"));
						message.setText(mDataMap.get("message_info"));
						message.setLink(mDataMap.get("url"));
						message.setCreated_at(mDataMap.get("create_time"));
						message.setRead(Integer.valueOf(mDataMap.get("is_read")));
						
						
						//查出用户信息                           
						MDataMap mUserMap = DbUp.upTable("mc_extend_info_star").one("member_code",mDataMap.get("member_send"));
						
						//用户信息
						message.getUser().setMember_code(mDataMap.get("member_send"));
						message.getUser().setNickname(mUserMap.get("nickname"));
						message.getUser().setGroup(BigInteger.valueOf(Long.valueOf(mUserMap.get("member_group"))));
						message.getUser().setGender(BigInteger.valueOf(Long.valueOf(mUserMap.get("member_sex"))));
						message.getUser().setMobile(mUserMap.get("mobile_phone"));
						message.getUser().setScore(Integer.parseInt(mUserMap.get("member_score")));
						message.getUser().setLevel(Integer.valueOf(mUserMap.get("member_level").substring(mUserMap.get("member_level").length()-4, mUserMap.get("member_level").length())));
						message.getUser().setCreate_time(mUserMap.get("create_time"));
						
						result.getMessages().add(message);
						
						}
							
				result.setUnread_count(unread_count);
			}
			
		}
		return result;
	}

}
