package com.cmall.newscenter.api;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import com.cmall.membercenter.model.MemberInfo;
import com.cmall.newscenter.model.CommentdityApp;
import com.cmall.newscenter.model.InforMationCommentInput;
import com.cmall.newscenter.model.InforMationCommentResult;
import com.cmall.newscenter.model.PageResults;
import com.cmall.systemcenter.model.AppPhoto;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForMember;

/***
 * 资讯评价列表API
 * 
 * @author shiyz date： 2014-7-4
 * @version 1.0
 */
public class InforMationCommentApi extends
		RootApiForMember<InforMationCommentResult, InforMationCommentInput> {

	public InforMationCommentResult Process(InforMationCommentInput inputParam,
			MDataMap mRequestMap) {

		InforMationCommentResult result = new InforMationCommentResult();
		// 设置相关信息
		/* app编号 */
		String app_code = bConfig("newscenter.app_code");
		String Score_unit = bConfig("newscenter.Score_unit");
		if (result.upFlagTrue()) {
			
			MDataMap mWhereMap = new MDataMap();

			mWhereMap.put("info_code", inputParam.getFeed());
			mWhereMap.put("manage_code", app_code);
			mWhereMap.put("flag_show", "4497172100030002");
			/* 根据评价编号查询相关已审核通过评价列表 */
			List<MDataMap> mPageData = DbUp.upTable("nc_comment").queryAll("", "-create_time", "", mWhereMap);
//			int flagNum = mPageData.size();//标记已审核通过评价列表的数量
			
			/*如果用户登陆则增加该用户未审核及审核拒绝的列表*/
			if(getFlagLogin()){
				List<MDataMap> mdata = DbUp.upTable("nc_comment").queryIn("", "-create_time", "info_code='"+inputParam.getFeed()+"' and create_member='"+getOauthInfo().getUserCode()+"' and manage_code='"+app_code+"'", new MDataMap(), -1, -1, "flag_show", "4497172100030001,4497172100030003");				
				mPageData.addAll(mdata);
			}
			
			//评价总数
			int totalNum = mPageData.size();
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
			
			/* 返回翻页结果 */
			result.setPaged(pageResults);
			//返回评价列表
			List<MDataMap> subList = mPageData.subList(startNum, endNum);
			
			if (subList.size() != 0) {

				for (MDataMap mDataMap : subList) {

					CommentdityApp comments = new CommentdityApp();

					comments.setCreated_at(mDataMap.get("create_time"));

					comments.setId(mDataMap.get("comment_code"));

					comments.setText(mDataMap.get("comment_info"));

					comments.setState(new BigInteger(mDataMap.get("flag_show")));
					
					String replayCode = "";

					replayCode = mDataMap.get("replay_code");

					String memberCode = "";

					memberCode = mDataMap.get("create_member");

					String sSql = "select * from mc_extend_info_star ms ,mc_member_level mg where ms.member_code =:member_code and ms.member_level = mg.level_code and ms.app_code = mg.manage_code and ms.app_code =:app_code";

					MDataMap mMemberMap = new MDataMap();

					mMemberMap.put("member_code", memberCode);
					mMemberMap.put("app_code", app_code);
					Map<String, Object> map = DbUp.upTable("mc_extend_info_star").dataSqlOne(sSql, mMemberMap);

					if (replayCode != "") {

						if (map != null && !map.isEmpty()) {

							MemberInfo user = new MemberInfo();
							user.setMember_code(String.valueOf(map.get("member_code").toString()));
							user.setNickname(String.valueOf(map.get("nickname").toString()));
							user.setGroup(BigInteger.valueOf(Long.valueOf(map.get("member_group").toString())));
							user.setGender(BigInteger.valueOf(Long.valueOf(map.get("member_sex").toString())));
							user.setLevel(Integer.valueOf(map.get("member_level").toString().substring(map.get("member_level").toString().length() - 4,map.get("member_level").toString().length())));
							user.setLevel_name(String.valueOf(map.get("level_name").toString()));
							user.setScore(Integer.valueOf(map.get("member_score").toString()));
							user.setScore_unit(Score_unit);
							user.setCreate_time(String.valueOf(map.get("create_time").toString()));
							user.setMobile(String.valueOf(map.get("mobile_phone").toString()));
							/* 头像 */
							AppPhoto avatar = new AppPhoto();
							avatar.setLarge(String.valueOf(map.get("member_avatar").toString()));
							avatar.setThumb(String.valueOf(map.get("member_avatar").toString()));
							user.setAvatar(avatar);
							comments.setUser(user);

							//添加评论所属人信息
							MDataMap userMap = DbUp.upTable("nc_comment").one("comment_code",replayCode,"manage_code",app_code);
							String sql = "select * from mc_extend_info_star ms ,mc_member_level mg where ms.member_code =:member_code and ms.member_level = mg.level_code and ms.app_code = mg.manage_code and ms.app_code =:app_code";
							MDataMap m = new MDataMap();
							m.put("member_code", userMap.get("create_member"));
							m.put("app_code", app_code);
							Map<String, Object> userLevel = DbUp.upTable("mc_extend_info_star").dataSqlOne(sql, m);
							
							MemberInfo mreply = new MemberInfo();
							mreply.setMember_code(String.valueOf(userLevel.get("member_code").toString()));
							mreply.setNickname(String.valueOf(userLevel.get("nickname").toString()));
							mreply.setGroup(BigInteger.valueOf(Long.valueOf(userLevel.get("member_group").toString())));
							mreply.setGender(BigInteger.valueOf(Long.valueOf(userLevel.get("member_sex").toString())));
							mreply.setLevel(Integer.valueOf(userLevel.get("member_level").toString().substring(userLevel.get("member_level").toString().length() - 4,userLevel.get("member_level").toString().length())));
							mreply.setLevel_name(String.valueOf(userLevel.get("level_name").toString()));
							mreply.setScore(Integer.valueOf(userLevel.get("member_score").toString()));
							mreply.setScore_unit(Score_unit);
							mreply.setCreate_time(String.valueOf(userLevel.get("create_time").toString()));
							mreply.setMobile(String.valueOf(userLevel.get("mobile_phone").toString()));
							AppPhoto avatar2 = new AppPhoto();
							avatar2.setLarge(String.valueOf(userLevel.get("member_avatar").toString()));
							avatar2.setThumb(String.valueOf(userLevel.get("member_avatar").toString()));
							mreply.setAvatar(avatar2);
							comments.setReply(mreply);
						}
					} else {
						if (map != null && !map.isEmpty()) {

							MemberInfo user = new MemberInfo();
							user.setMember_code(String.valueOf(map.get("member_code").toString()));
							user.setNickname(String.valueOf(map.get("nickname").toString()));
							user.setGroup(BigInteger.valueOf(Long.valueOf(map.get("member_group").toString())));
							user.setGender(BigInteger.valueOf(Long.valueOf(map.get("member_sex").toString())));
							user.setLevel(Integer.valueOf(map.get("member_level").toString().substring(map.get("member_level").toString().length() - 4,map.get("member_level").toString().length())));
							user.setLevel_name(String.valueOf(map.get("level_name").toString()));
							user.setScore(Integer.valueOf(map.get("member_score").toString()));
							user.setScore_unit(Score_unit);
							user.setCreate_time(String.valueOf(map.get("create_time").toString()));
							user.setMobile(String.valueOf(map.get("mobile_phone").toString()));
							AppPhoto avatar = new AppPhoto();
							avatar.setLarge(String.valueOf(map.get("member_avatar").toString()));
							avatar.setThumb(String.valueOf(map.get("member_avatar").toString()));
							user.setAvatar(avatar);
							comments.setUser(user);
						}
					}

					/* 返回评论列表 */
					result.getComments().add(comments);
				}

			}
			
			

		}
		return result;
	}

}
