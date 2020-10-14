package com.cmall.newscenter.api;

import java.math.BigInteger;

import com.cmall.newscenter.model.ActivityReplyCommentsInput;
import com.cmall.newscenter.model.ActivityReplyCommentsResult;
import com.cmall.newscenter.model.CommentdityApp;
import com.cmall.systemcenter.model.AppPhoto;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webapi.RootApiForToken;
/**
 * 活动-回复评论列表Api
 * @author yangrong
 * date: 2014-07-04
 * @version1.0
 */
public class ActivityReplyCommentApi extends RootApiForToken<ActivityReplyCommentsResult, ActivityReplyCommentsInput> {

	public ActivityReplyCommentsResult Process(
			ActivityReplyCommentsInput inputParam, MDataMap mRequestMap) {
		
		ActivityReplyCommentsResult result = new ActivityReplyCommentsResult();
		
		if(result.upFlagTrue()){
			
			/**将回复评论内容放入评论实体类中**/
			CommentdityApp reply = new CommentdityApp();
			
			MDataMap mDataMap = new MDataMap();
			
			/*回复评论编号*/
			String info_code = WebHelper.upCode("PL");
			
			mDataMap.inAllValues("info_code",inputParam.getInfo_code(),"comment_code",info_code,"comment_info",
					inputParam.getText(),"create_member",getUserCode(),"replay_code",inputParam.getComment(),"flag_show","4497172100030001",
					"create_time",FormatHelper.upDateTime(),"manage_code",getManageCode());
			/*将回复评论的内容插入表中*/
			DbUp.upTable("nc_comment").dataInsert(mDataMap);
			
			reply.setId(mDataMap.get("comment_code"));
			
			reply.setText(mDataMap.get("comment_info"));
			
			reply.setCreated_at(mDataMap.get("create_time"));
			
			reply.setState(BigInteger.valueOf(Long.valueOf(mDataMap.get("flag_show"))));
			
			/*if(!("").equals(mDataMap.get("comment_photos"))){
				
				reply.getPhotos().get(0).setLarge(mDataMap.get("comment_photos"));
				reply.getPhotos().get(0).setThumb(mDataMap.get("comment_photos"));
			}*/
			
			//查出回复评论用户 信息                           
			MDataMap mUserMap = DbUp.upTable("mc_extend_info_star").one("member_code",mDataMap.get("create_member"));
			
			if(mUserMap!=null&&!mUserMap.isEmpty()){
				
				reply.getUser().setMember_code(String.valueOf(mUserMap.get("member_code").toString()));
				
				reply.getUser().setNickname(String.valueOf(mUserMap.get("nickname").toString()));
				
				reply.getUser().setGroup(BigInteger.valueOf(Long.valueOf(mUserMap.get("member_group").toString())));
				
				reply.getUser().setGender(BigInteger.valueOf(Long.valueOf(mUserMap.get("member_sex").toString())));
				
				reply.getUser().setLevel(Integer.valueOf(mUserMap.get("member_level").substring(mUserMap.get("member_level").length()-4, mUserMap.get("member_level").length())));
				
				//根据等级编号查出等级名称
				MDataMap mLevelMap = DbUp.upTable("mc_member_level").one("level_code",mUserMap.get("member_level").toString(),"manage_code",getManageCode());
				if(mLevelMap!=null){
					
					reply.getUser().setLevel_name(mLevelMap.get("level_name"));                                 //等级名称
				}
				
				reply.getUser().setScore(Integer.valueOf(mUserMap.get("member_score").toString()));
				
				reply.getUser().setMobile(mUserMap.get("mobile_phone").toString());
				
				String Score_unit = bConfig("newscenter.Score_unit");
				
				reply.getUser().setScore_unit(Score_unit);
				
				reply.getUser().setCreate_time(String.valueOf(mUserMap.get("create_time").toString()));
				
				AppPhoto avatar = new AppPhoto();
				
				avatar.setLarge(String.valueOf(mUserMap.get("member_avatar").toString()));
				
				avatar.setThumb(String.valueOf(mUserMap.get("member_avatar").toString()));
				
				reply.getUser().setAvatar(avatar);
				
			}
			
			//查出评论用户编号
			MDataMap Map = DbUp.upTable("nc_comment").one("comment_code",inputParam.getComment());
			
			if(Map!=null){
				
				//查出回复用户 信息                           
				MDataMap mReplyUserMap = DbUp.upTable("mc_extend_info_star").one("member_code",Map.get("create_member"));
				
				if(mReplyUserMap!=null&&!mReplyUserMap.isEmpty()){
					
					reply.getReply().setMember_code(String.valueOf(mReplyUserMap.get("member_code").toString()));
					
					reply.getReply().setNickname(String.valueOf(mReplyUserMap.get("nickname").toString()));
					
					reply.getReply().setGroup(BigInteger.valueOf(Long.valueOf(mReplyUserMap.get("member_group").toString())));
					
					reply.getReply().setGender(BigInteger.valueOf(Long.valueOf(mReplyUserMap.get("member_sex").toString())));
					
					reply.getReply().setLevel(Integer.valueOf(mReplyUserMap.get("member_level").substring(mReplyUserMap.get("member_level").length()-4, mReplyUserMap.get("member_level").length())));
					
					//根据等级编号查出等级名称
					MDataMap mLevelMap = DbUp.upTable("mc_member_level").one("level_code",mReplyUserMap.get("member_level").toString());
					if(mLevelMap!=null){
						
						reply.getReply().setLevel_name(mLevelMap.get("level_name"));                                 //等级名称
					}
					
					reply.getReply().setScore(Integer.valueOf(mReplyUserMap.get("member_score").toString()));
					
					reply.getReply().setMobile(mReplyUserMap.get("mobile_phone").toString());
					
					String Score_unit = bConfig("newscenter.Score_unit");
					
					reply.getReply().setScore_unit(Score_unit);
					
					reply.getReply().setCreate_time(String.valueOf(mReplyUserMap.get("create_time").toString()));
					
					AppPhoto avatar = new AppPhoto();
					
					avatar.setLarge(String.valueOf(mReplyUserMap.get("member_avatar").toString()));
					
					avatar.setThumb(String.valueOf(mReplyUserMap.get("member_avatar").toString()));
					
					reply.getReply().setAvatar(avatar);
					
				}
			}
			
			//查出评论用户 信息                           
			MDataMap myMap = DbUp.upTable("nc_comment").one("comment_code",inputParam.getComment());
			
			//插入消息表    参数  ————回复用户 编号     评论用户编号   内容   评论来源
			MessageRule.MessageReplyTemplate(getUserCode(),myMap.get("create_member"),inputParam.getText(),myMap.get("comment_info"),getManageCode(),mDataMap.get("create_time"),5,inputParam.getInfo_code());
			
			/**返回评论内容**/
			result.setReply(reply);
			
		}
		return result;
	}
		
}
