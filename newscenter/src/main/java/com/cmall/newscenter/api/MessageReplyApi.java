package com.cmall.newscenter.api;

import java.math.BigInteger;

import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.MessageReplyInput;
import com.cmall.newscenter.model.MessageReplyResult;
import com.cmall.newscenter.model.ReplyMessage;
import com.cmall.newscenter.util.DataPaging;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
/**
 * 消息 - 回复消息
 * @author yangrong
 * date 2014-7-21
 * @version 1.0
 */
public class MessageReplyApi extends RootApiForToken<MessageReplyResult, MessageReplyInput> {

	public MessageReplyResult Process(MessageReplyInput inputParam,
			MDataMap mRequestMap) {
		MessageReplyResult result = new MessageReplyResult();
		if(result.upFlagTrue()){
			
			MDataMap mWhereMap=new MDataMap();
			
			mWhereMap.put("message_type","449746640002");
			mWhereMap.put("member_send",getUserCode());
			mWhereMap.put("is_delete","0");
			
			//查出消息列表
			MPageData mPageData=DataPaging.upPageData("nc_message_info", "", "-create_time", mWhereMap, inputParam.getPaging());
			
			if(mPageData!=null){
				int unread_count = 0;
				for( MDataMap mDataMap: mPageData.getListData()){
					if("0".equals(mDataMap.get("is_read"))){
						//统计未读消息
						unread_count++;
					}
					ReplyMessage message = new ReplyMessage();
					
					message.setId(mDataMap.get("message_code"));
					message.setText(mDataMap.get("message_info"));
					message.setCreated_at(mDataMap.get("create_time"));
					message.setOrig_comment(mDataMap.get("old_comment"));
					message.setRead(Integer.valueOf(mDataMap.get("is_read")));
					
					message.setUrl(mDataMap.get("url_id"));
					
					message.setReply_type(Integer.valueOf("".equals(mDataMap.get("url_type"))?"0":mDataMap.get("url_type")));
					
					MDataMap mWhereMap2=new MDataMap();
					mWhereMap2.put("member_code",mDataMap.get("member_send"));
					
					
					//查出用户信息                           
					MPageData mPageData2=DataPaging.upPageData("mc_extend_info_star", "", "", mWhereMap2, inputParam.getPaging());
					
					//发送人信息
					for( MDataMap mDataMap2: mPageData2.getListData()){
						
						message.getUser().setMember_code(mDataMap.get("member_send"));
						message.getUser().setNickname(mDataMap2.get("nickname"));
						message.getUser().setGroup(BigInteger.valueOf(Long.valueOf(mDataMap2.get("member_group"))));
						message.getUser().setGender(BigInteger.valueOf(Long.valueOf(mDataMap2.get("member_sex"))));
						message.getUser().setMobile(mDataMap2.get("mobile_phone"));
						message.getUser().getAvatar().setLarge(mDataMap2.get("member_avatar"));
						message.getUser().getAvatar().setThumb(mDataMap2.get("member_avatar"));
						message.getUser().setScore(Integer.parseInt(mDataMap2.get("member_score")));
						message.getUser().setScore_unit(bConfig("newscenter.Score_unit"));
						message.getUser().setLevel(Integer.valueOf(mDataMap2.get("member_level").substring(mDataMap2.get("member_level").length()-4, mDataMap2.get("member_level").length())));
						
						//根据等级编号查出等级名称
						MDataMap mLevelMap = DbUp.upTable("mc_member_level").one("level_code",mDataMap2.get("member_level"),"manage_code",getManageCode());
						if(mLevelMap!=null){
							
							message.getUser().setLevel_name(mLevelMap.get("level_name"));                                 //等级名称
						}
						
						message.getUser().setCreate_time(mDataMap2.get("create_time"));
					}
					
					MDataMap mWhereMap3=new MDataMap();
					mWhereMap3.put("member_code",mDataMap.get("member_code"));
					//我的信息                           
					MPageData myData=DataPaging.upPageData("mc_extend_info_star", "", "", mWhereMap3, inputParam.getPaging());
					
					for( MDataMap myDataMap: myData.getListData()){
						
						message.getReply().setMember_code(mDataMap.get("member_code"));
						message.getReply().setNickname(myDataMap.get("nickname"));
						message.getReply().setMobile(myDataMap.get("mobile_phone"));
						message.getReply().setGroup(BigInteger.valueOf(Long.valueOf(myDataMap.get("member_group"))));
						message.getReply().setGender(BigInteger.valueOf(Long.valueOf(myDataMap.get("member_sex"))));
						message.getReply().getAvatar().setLarge(myDataMap.get("member_avatar"));
						message.getReply().getAvatar().setThumb(myDataMap.get("member_avatar"));
						message.getReply().setScore(Integer.parseInt(myDataMap.get("member_score")));
						message.getReply().setScore_unit(bConfig("newscenter.Score_unit"));
						message.getReply().setLevel(Integer.valueOf(myDataMap.get("member_level").substring(myDataMap.get("member_level").length()-4, myDataMap.get("member_level").length())));
						
						//根据等级编号查出等级名称
						MDataMap mLevelMap = DbUp.upTable("mc_member_level").one("level_code",myDataMap.get("member_level"),"manage_code",getManageCode());
						if(mLevelMap!=null){
							
							message.getReply().setLevel_name(mLevelMap.get("level_name"));                                 //等级名称
						}
						message.getReply().setCreate_time(myDataMap.get("create_time"));
						
					}
					result.getMessages().add(message);
					
				}
				result.setUnread_count(unread_count);
			}
		
			result.setPaged(mPageData.getPageResults());
		}
		return result;
	}

}
