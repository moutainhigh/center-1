package com.cmall.newscenter.api;

import java.math.BigInteger;
import java.util.Map;

import com.cmall.membercenter.model.MemberInfo;
import com.cmall.newscenter.model.ActivitySendCommentsInput;
import com.cmall.newscenter.model.ActivitySendCommentsResult;
import com.cmall.newscenter.model.CommentdityApp;
import com.cmall.systemcenter.model.AppPhoto;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webapi.RootApiForToken;
/**
 * 活动-发送评论列表Api
 * @author yangrong
 * date: 2014-07-04
 * @version1.0
 */
public class ActivitySendCommentApi extends RootApiForToken<ActivitySendCommentsResult, ActivitySendCommentsInput> {

	public ActivitySendCommentsResult Process(
			ActivitySendCommentsInput inputParam, MDataMap mRequestMap) {
		
		ActivitySendCommentsResult result = new ActivitySendCommentsResult();
		
		// 设置相关信息
		if (result.upFlagTrue()) {
			
			
			MDataMap  mDataMap = new MDataMap();
			
			/*评论编号*/
			String comment_code = WebHelper.upCode("PL");
			
			mDataMap.inAllValues("info_code",inputParam.getInfo_code(),"comment_code",comment_code,"comment_info",inputParam.getText()
					,"create_member",getUserCode(),"create_time",FormatHelper.upDateTime(),"flag_show","4497172100030001","manage_code",getManageCode());
			
			/*将评论内容插入到评论表中*/
			DbUp.upTable("nc_comment").dataInsert(mDataMap);
			
			MDataMap ncMap =  DbUp.upTable("nc_info").one("info_code",inputParam.getInfo_code(),"manage_code",getManageCode());
			
			if(ncMap != null){
				
				Integer num_comment =0;
				
				if(null !=ncMap.get("num_comment") && !"".equals(ncMap.get("num_comment"))){
					
					num_comment = Integer.valueOf(ncMap.get("num_comment"));
				}
				
				Integer newNum_comment = num_comment + 1;
				
				ncMap.put("num_comment", String.valueOf(newNum_comment));
				
				DbUp.upTable("nc_info").dataUpdate(ncMap, "num_comment", "zid");
				
			}
			
			MDataMap plmDataMap = DbUp.upTable("nc_comment").one("comment_code",comment_code,"manage_code",getManageCode());
			

			/**将返回信息放入评论实体类中返回**/
			CommentdityApp comment = new CommentdityApp();
			
			if(plmDataMap!=null){
				
			/*返回创建时间*/
			comment.setCreated_at(plmDataMap.get("create_time"));
		
			/*评论编号*/
			comment.setId(plmDataMap.get("comment_code"));
			
			/*评论内容*/
			comment.setText(inputParam.getText());
			
			comment.setState(BigInteger.valueOf(Long.valueOf(plmDataMap.get("flag_show"))));
			
			}
			/*查询用户信息*/
			String sSql = "select * from mc_extend_info_star ms ,mc_member_level mg where ms.member_code =:member_code and ms.member_level = mg.level_code";
			
			MDataMap  mWhereMap = new MDataMap();
			
			mWhereMap.put("member_code", getUserCode());
		
		Map<String, Object> mMemberMap = DbUp.upTable("mc_extend_info_star").dataSqlOne(sSql, mWhereMap);
		
			
			
			MemberInfo user = new MemberInfo();
			
			if(mMemberMap!=null&&!mMemberMap.isEmpty()){
				
				user.setMember_code(String.valueOf(mMemberMap.get("member_code").toString()));
				
				user.setNickname(String.valueOf(mMemberMap.get("nickname").toString()));
				
				user.setGroup(BigInteger.valueOf(Long.valueOf(mMemberMap.get("member_group").toString())));
				
				user.setGender(BigInteger.valueOf(Long.valueOf(mMemberMap.get("member_sex").toString())));
				
				user.setLevel(Integer.valueOf(mMemberMap.get("member_level").toString().substring(mMemberMap.get("member_level").toString().length()-4, mMemberMap.get("member_level").toString().length())));
				
				user.setLevel_name(String.valueOf(mMemberMap.get("level_name").toString()));
				
				user.setScore(Integer.valueOf(mMemberMap.get("member_score").toString()));
				
				user.setMobile(mMemberMap.get("mobile_phone").toString());
				
				String Score_unit = bConfig("newscenter.Score_unit");
				
				user.setScore_unit(Score_unit);
				
				user.setCreate_time(String.valueOf(mMemberMap.get("create_time").toString()));
				
				AppPhoto avatar = new AppPhoto();
				
				avatar.setLarge(String.valueOf(mMemberMap.get("member_avatar").toString()));
				
				user.setAvatar(avatar);
				
				comment.setUser(user);
			}
			
			result.setComment(comment);
			
		}
		
		return result;
	}
		
}
