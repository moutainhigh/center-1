package com.cmall.newscenter.api;

import java.math.BigInteger;
import java.util.Map;

import com.cmall.membercenter.model.MemberInfo;
import com.cmall.membercenter.model.ScoredChange;
import com.cmall.membercenter.support.ScoredSupport;
import com.cmall.newscenter.model.CommentdityApp;
import com.cmall.newscenter.model.InforMationAppSendCommentsInput;
import com.cmall.newscenter.model.InforMationAppSendCommentsResult;
import com.cmall.systemcenter.model.AppPhoto;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webapi.RootApiForToken;
/***
 * 资讯发送评论API
 * @author shiyz	
 * date 2014-7-4
 * @version 1.0
 */
public class InforMationSendCommentsApi extends
		RootApiForToken<InforMationAppSendCommentsResult, InforMationAppSendCommentsInput> {

	public InforMationAppSendCommentsResult Process(
			InforMationAppSendCommentsInput inputParam, MDataMap mRequestMap) {
		
		InforMationAppSendCommentsResult result = new InforMationAppSendCommentsResult();
		
		// 设置相关信息
		if (result.upFlagTrue()) {
			
			/*获得积分*/
            ScoredChange scored = new ScoredChange();
            
           
			MDataMap  mDataMap = new MDataMap();
			
			/*评论编号*/
			String comment_code = WebHelper.upCode("PL");
			
			mDataMap.inAllValues("info_code",inputParam.getFeed(),"comment_code",comment_code,"comment_info",inputParam.getText()
					,"create_member",getUserCode(),"create_time",FormatHelper.upDateTime(),"flag_show","4497172100030001","manage_code",getManageCode());
			
			/*将评论内容插入到评论表中*/
			DbUp.upTable("nc_comment").dataInsert(mDataMap);
			
			 MDataMap ncDataMap =  DbUp.upTable("nc_info").one("info_code",inputParam.getFeed(),"manage_code",getManageCode());
	            //更新评论数量
	            if(ncDataMap!=null){
	            	
	            	 ncDataMap.put("num_comment", String.valueOf(Integer.valueOf(ncDataMap.get("num_comment"))+1));
	            	 
		   			 DbUp.upTable("nc_info").dataUpdate(ncDataMap, "num_comment", "zid");
	            }
			
			
			MDataMap plmDataMap = DbUp.upTable("nc_comment").one("comment_code",comment_code,"manage_code",getManageCode());
			
			/**将返回信息放入评论实体类中返回**/
			CommentdityApp comment = new CommentdityApp();
			
			if(plmDataMap!=null){
			
			/*返回创建时间*/
			comment.setCreated_at(plmDataMap.get("create_time"));
		
			/*评论编号*/
			comment.setId(plmDataMap.get("comment_code"));
			
			comment.setState(BigInteger.valueOf(Long.valueOf(plmDataMap.get("flag_show"))));
			
			}
			
			/*评论内容*/
			comment.setText(inputParam.getText());
			
			
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
			
			comment.setUser(user);
			
			result.setComment(comment);
			
		}
		
		return result;
	}

}
