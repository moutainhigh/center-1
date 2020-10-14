package com.cmall.newscenter.api;

import java.math.BigInteger;

import com.cmall.newscenter.model.ApplyMessage;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.MessageApplyInput;
import com.cmall.newscenter.model.MessageApplyResult;
import com.cmall.newscenter.util.DataPaging;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
/**
 * 消息 -报名消息
 * @author yangrong
 * date 2014-7-21
 * @version 1.0
 */
public class MessageApplyApi extends RootApiForToken<MessageApplyResult, MessageApplyInput> {
	/**
	 * @author yangrong
	 */
	public MessageApplyResult Process(MessageApplyInput inputParam,
			MDataMap mRequestMap) {
		MessageApplyResult result = new MessageApplyResult();
		if(result.upFlagTrue()){
			
			MDataMap mWhereMap=new MDataMap();
			
			mWhereMap.put("message_type","449746640003");
			mWhereMap.put("member_send",getUserCode());
			mWhereMap.put("is_delete","0");//未删除
			
			//查出消息列表
			MPageData mPageData=DataPaging.upPageData("nc_message_info", "", "-create_time", mWhereMap, inputParam.getPaging());
			
			if(mPageData!=null){
				int unread_count = 0;
				for( MDataMap mDataMap: mPageData.getListData()){
					if("0".equals(mDataMap.get("is_read"))){
						//统计未读消息
						unread_count++;
					}
					//查出多少人报名过
					MDataMap mWhereMap3=new MDataMap();
					mWhereMap3.put("info_code",mDataMap.get("info_code"));
					MPageData mBmInfoMap =DataPaging.upPageData("nc_registration", "", "", mWhereMap3, inputParam.getPaging());
					
					
					ApplyMessage message = new ApplyMessage();
					 
					message.setApply_count(mBmInfoMap.getListData().size());
					if(mDataMap.get("activity_type").equals("4497466400030001")){
						
						message.setActivity_message_type(0);
					}else{
						
						message.setActivity_message_type(1);
					}
					
					message.setActivity(mDataMap.get("info_code"));
					message.setId(mDataMap.get("message_code"));
					message.setText(mDataMap.get("message_info"));
					message.setLink(mDataMap.get("url"));
					message.setCreated_at(mDataMap.get("create_time"));
					message.setRead(Integer.valueOf(mDataMap.get("is_read")));
					
					MDataMap map = DbUp.upTable("mc_extend_info_star").one("member_code",getUserCode());
					
					if(map.get("member_group").equals("4497465000020002")){
						
						
						MDataMap infoMap = DbUp.upTable("nc_info").one("info_code",mDataMap.get("info_code"));
						
						if(infoMap!=null){
							
						if(!infoMap.get("create_member").equals(getUserCode())){
							
							MDataMap mDataMap2 = DbUp.upTable("mc_extend_info_star").one("member_code",infoMap.get("create_member"));
							
							//用户信息
							if(mDataMap2!=null){
								
								message.getUser().setMember_code(mDataMap2.get("member_code"));
								message.getUser().setNickname(mDataMap2.get("nickname"));
								message.getUser().setGroup(BigInteger.valueOf(Long.valueOf(mDataMap2.get("member_group"))));
								message.getUser().setGender(BigInteger.valueOf(Long.valueOf(mDataMap2.get("member_sex"))));
								message.getUser().setScore(Integer.parseInt(mDataMap2.get("member_score")));
								message.getUser().setLevel(Integer.valueOf(mDataMap2.get("member_level").substring(mDataMap2.get("member_level").length()-4, mDataMap2.get("member_level").length())));
								//根据等级编号查出等级名称
								MDataMap mLevelMap = DbUp.upTable("mc_member_level").one("level_code",mDataMap2.get("member_level"),"manage_code",getManageCode());
								if(mLevelMap!=null){
									
									message.getUser().setLevel_name(mLevelMap.get("level_name"));                                 //等级名称
								}
								message.getUser().setMobile(mDataMap2.get("mobile_phone"));
								message.getUser().setCreate_time(mDataMap2.get("create_time"));
								
								result.getMessages().add(message);
								
							}	
							
						}else {
							
							
					MDataMap mWhereMap2=new MDataMap();
					
					mWhereMap2.put("member_code",mDataMap.get("member_send"));
					
					//查出用户信息                           
					MPageData mPageData2=DataPaging.upPageData("mc_extend_info_star", "", "", mWhereMap2, inputParam.getPaging());
					
					//用户信息
					for( MDataMap mDataMap2: mPageData2.getListData()){
						
						message.getUser().setMember_code(mDataMap2.get("member_code"));
						message.getUser().setNickname(mDataMap2.get("nickname"));
						message.getUser().setGroup(BigInteger.valueOf(Long.valueOf(mDataMap2.get("member_group"))));
						message.getUser().setGender(BigInteger.valueOf(Long.valueOf(mDataMap2.get("member_sex"))));
						message.getUser().setScore(Integer.parseInt(mDataMap2.get("member_score")));
						message.getUser().setLevel(Integer.valueOf(mDataMap2.get("member_level").substring(mDataMap2.get("member_level").length()-4, mDataMap2.get("member_level").length())));
						//根据等级编号查出等级名称
						MDataMap mLevelMap = DbUp.upTable("mc_member_level").one("level_code",mDataMap2.get("member_level"),"manage_code",getManageCode());
						if(mLevelMap!=null){
							
							message.getUser().setLevel_name(mLevelMap.get("level_name"));                                 //等级名称
						}
						message.getUser().setMobile(mDataMap2.get("mobile_phone"));
						message.getUser().setCreate_time(mDataMap2.get("create_time"));
						
						result.getMessages().add(message);
						
					}
						}
						}
				}else {
					
					MDataMap infoMap = DbUp.upTable("nc_info").one("info_code",mDataMap.get("info_code"));
					
					if(infoMap!=null){
						
					MDataMap mDataMap2 = DbUp.upTable("mc_extend_info_star").one("member_code",infoMap.get("create_member"));
					
					//用户信息
					if(mDataMap2!=null){
						
						message.getUser().setMember_code(mDataMap2.get("member_code"));
						message.getUser().setNickname(mDataMap2.get("nickname"));
						message.getUser().setGroup(BigInteger.valueOf(Long.valueOf(mDataMap2.get("member_group"))));
						message.getUser().setGender(BigInteger.valueOf(Long.valueOf(mDataMap2.get("member_sex"))));
						message.getUser().setScore(Integer.parseInt(mDataMap2.get("member_score")));
						message.getUser().setLevel(Integer.valueOf(mDataMap2.get("member_level").substring(mDataMap2.get("member_level").length()-4, mDataMap2.get("member_level").length())));
						//根据等级编号查出等级名称
						MDataMap mLevelMap = DbUp.upTable("mc_member_level").one("level_code",mDataMap2.get("member_level"),"manage_code",getManageCode());
						if(mLevelMap!=null){
							
							message.getUser().setLevel_name(mLevelMap.get("level_name"));                                 //等级名称
						}
						message.getUser().setMobile(mDataMap2.get("mobile_phone"));
						message.getUser().setCreate_time(mDataMap2.get("create_time"));
						
						result.getMessages().add(message);
						
					}
				}
					
				}
				}
				result.setUnread_count(unread_count);
			}
			
			result.setPaged(mPageData.getPageResults());
		}
		
		return result;
	}
	

}
