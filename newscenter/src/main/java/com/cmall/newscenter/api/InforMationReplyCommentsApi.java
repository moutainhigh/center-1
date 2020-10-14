package com.cmall.newscenter.api;

import java.math.BigInteger;
import java.util.Map;

import com.cmall.membercenter.model.MemberInfo;
import com.cmall.newscenter.model.CommentdityApp;
import com.cmall.newscenter.model.InforMationAppReplyCommentsInput;
import com.cmall.newscenter.model.InforMationAppReplyCommentsResult;
import com.cmall.systemcenter.model.AppPhoto;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webapi.RootApiForToken;
/***
 * 资讯评论回复api类
 * @author shiyz
 * date 2014-7-4
 * @version 1.0
 */
public class InforMationReplyCommentsApi extends RootApiForToken<InforMationAppReplyCommentsResult, InforMationAppReplyCommentsInput> {

	public InforMationAppReplyCommentsResult Process(
			InforMationAppReplyCommentsInput inputParam, MDataMap mRequestMap) {
		
		InforMationAppReplyCommentsResult result = new InforMationAppReplyCommentsResult();
		
		if(result.upFlagTrue()){
			
			/**将回复评论内容放入评论实体类中**/
			CommentdityApp commentReply = new CommentdityApp();
			
		    MDataMap  plmDataMap = DbUp.upTable("nc_comment").one("comment_code",inputParam.getComment(),"manage_code",getManageCode());
			
			if(plmDataMap!=null){
				String sql = "select * from mc_extend_info_star ms ,mc_member_level mg where ms.member_code =:member_code and ms.member_level = mg.level_code and ms.app_code = mg.manage_code and ms.app_code =:app_code";
				
				MDataMap  plWhereMap = new MDataMap();
				
				plWhereMap.put("member_code", plmDataMap.get("create_member"));
				plWhereMap.put("app_code", getManageCode());
			
				Map<String, Object> plMemberMap = DbUp.upTable("mc_extend_info_star").dataSqlOne(sql, plWhereMap);	
				
				MemberInfo reply = new MemberInfo();
				
				reply.setMember_code(String.valueOf(plMemberMap.get("member_code").toString()));
				
				reply.setNickname(String.valueOf(plMemberMap.get("nickname").toString()));
				
				reply.setGroup(BigInteger.valueOf(Long.valueOf(plMemberMap.get("member_group").toString())));
				
				reply.setGender(BigInteger.valueOf(Long.valueOf(plMemberMap.get("member_sex").toString())));
				
				reply.setLevel(Integer.valueOf(plMemberMap.get("member_level").toString().substring(plMemberMap.get("member_level").toString().length()-4, plMemberMap.get("member_level").toString().length())));
				
				reply.setLevel_name(String.valueOf(plMemberMap.get("level_name").toString()));
				
				reply.setScore(Integer.valueOf(plMemberMap.get("member_score").toString()));
				
				reply.setMobile(String.valueOf(plMemberMap.get("mobile_phone").toString()));
				
	            String Score_unit = bConfig("newscenter.Score_unit");
				
	            reply.setScore_unit(Score_unit);
				
	            reply.setCreate_time(String.valueOf(plMemberMap.get("create_time").toString()));	
	            
	            AppPhoto avatar = new AppPhoto();
				
				avatar.setLarge(String.valueOf(plMemberMap.get("member_avatar").toString()));
				avatar.setThumb(String.valueOf(plMemberMap.get("member_avatar").toString()));
				reply.setAvatar(avatar);
				
				commentReply.setReply(reply);
				
			}
			
			MDataMap mDataMap = new MDataMap();
			
			/*回复评论编号*/
			String replay_code = WebHelper.upCode("PL");
			
			mDataMap.inAllValues("info_code",inputParam.getFeed(),"comment_code",replay_code,"comment_info",
					inputParam.getText(),"create_member",getUserCode(),"replay_code",inputParam.getComment(),"flag_show","4497172100030001",
					"create_time",FormatHelper.upDateTime(),"manage_code",getManageCode());
			/*将回复评论的内容插入表中*/
			DbUp.upTable("nc_comment").dataInsert(mDataMap);
			
			/*查询回复评论通过审核的内容*/
			MDataMap hfmDataMap = DbUp.upTable("nc_comment").one("comment_code",replay_code,"manage_code",getManageCode());
			
			if(hfmDataMap!=null){				
				
				commentReply.setCreated_at(hfmDataMap.get("create_time"));	
				
				commentReply.setId(hfmDataMap.get("replay_code"));		
				
				commentReply.setState(BigInteger.valueOf(Long.valueOf(hfmDataMap.get("flag_show"))));
				
				commentReply.setText(hfmDataMap.get("comment_info"));
				
				
			}
			
			
			
			String sSql = "select * from mc_extend_info_star ms ,mc_member_level mg where ms.member_code =:member_code and ms.member_level = mg.level_code and ms.app_code = mg.manage_code and ms.app_code =:app_code";
			
			MDataMap  mWhereMap = new MDataMap();
			
			mWhereMap.put("member_code", getUserCode());
			
			mWhereMap.put("app_code", getManageCode());
			
			Map<String, Object> mMemberMap = DbUp.upTable("mc_extend_info_star").dataSqlOne(sSql, mWhereMap);
			
			
			
			MemberInfo user = new MemberInfo();
			
			user.setMember_code(String.valueOf(mMemberMap.get("member_code").toString()));
			
			user.setNickname(String.valueOf(mMemberMap.get("nickname").toString()));
			
			user.setGroup(BigInteger.valueOf(Long.valueOf(mMemberMap.get("member_group").toString())));
			
			user.setGender(BigInteger.valueOf(Long.valueOf(mMemberMap.get("member_sex").toString())));
			
			user.setLevel(Integer.valueOf(mMemberMap.get("member_level").toString().substring(mMemberMap.get("member_level").toString().length()-4, mMemberMap.get("member_level").toString().length())));
			
			user.setLevel_name(String.valueOf(mMemberMap.get("level_name").toString()));
			
			user.setScore(Integer.valueOf(mMemberMap.get("member_score").toString()));
			
			user.setMobile(String.valueOf(mMemberMap.get("mobile_phone").toString()));
			
			String Score_unit = bConfig("newscenter.Score_unit");
			
			user.setScore_unit(Score_unit);
			
			user.setCreate_time(String.valueOf(mMemberMap.get("create_time").toString()));
			
			AppPhoto avatar = new AppPhoto();
			
			avatar.setLarge(String.valueOf(mMemberMap.get("member_avatar").toString()));
			
			avatar.setThumb(String.valueOf(mMemberMap.get("member_avatar").toString()));
			
			user.setAvatar(avatar);
			
			commentReply.setUser(user);
			/**返回评论内容**/
			result.setReply(commentReply);
			/*回复消息*/
			MessageRule.MessageReplyTemplate(getUserCode(), plmDataMap.get("create_member"), inputParam.getText(), plmDataMap.get("comment_info"),getManageCode(),mDataMap.get("create_time"),2,inputParam.getFeed());
			
			
			
		}
		return result;
	}

}
