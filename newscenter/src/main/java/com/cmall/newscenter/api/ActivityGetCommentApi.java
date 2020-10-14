package com.cmall.newscenter.api;

import java.math.BigInteger;
import java.util.List;

import com.cmall.membercenter.model.MemberInfo;
import com.cmall.newscenter.model.ActivityGetCommentListsInput;
import com.cmall.newscenter.model.ActivityGetCommentListsResult;
import com.cmall.newscenter.model.CommentdityApp;
import com.cmall.newscenter.model.PageResults;
import com.cmall.systemcenter.model.AppPhoto;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForMember;
/**
 * 活动-获取评论列表Api
 * @author yangrong
 * date: 2014-07-04
 * @version1.0
 */
public class ActivityGetCommentApi extends RootApiForMember<ActivityGetCommentListsResult, ActivityGetCommentListsInput> {

	public ActivityGetCommentListsResult Process(
			ActivityGetCommentListsInput inputParam, MDataMap mRequestMap) {
		
		ActivityGetCommentListsResult result = new ActivityGetCommentListsResult();
		
		// 设置相关信息
		if (result.upFlagTrue()) {
			
			MDataMap mWhereMap = new MDataMap();
			
			mWhereMap.put("info_code", inputParam.getInfo_code());
			mWhereMap.put("manage_code", getManageCode());
			mWhereMap.put("flag_show", "4497172100030002");
			/* 根据评价编号查询相关已审核通过评价列表 */
			List<MDataMap> mPageData = DbUp.upTable("nc_comment").queryAll("", "-create_time", "", mWhereMap);
			/*如果用户登陆则增加该用户未审核及审核拒绝的列表*/
			if(getFlagLogin()){
				List<MDataMap> mdata = DbUp.upTable("nc_comment").queryIn("", "-create_time", "info_code='"+inputParam.getInfo_code()+"' and create_member='"+getOauthInfo().getUserCode()+"' and manage_code='"+getOauthInfo().getManageCode()+"'", new MDataMap(), -1, -1, "flag_show", "4497172100030001,4497172100030003");				
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
			if(subList.size()!=0){
			
				for(MDataMap mDataMap:subList){
					
					CommentdityApp comments = new CommentdityApp();
					
					comments.setCreated_at(mDataMap.get("create_time"));
					
					comments.setId(mDataMap.get("comment_code"));
					
					comments.setText(mDataMap.get("comment_info"));
					comments.setState(new BigInteger(mDataMap.get("flag_show")));
					//查出评论用户 信息                           
					MDataMap mUserMap = DbUp.upTable("mc_extend_info_star").one("member_code",mDataMap.get("create_member"));
					
					if(mUserMap!=null&&!mUserMap.isEmpty()){
						MemberInfo user = new MemberInfo();
						user.setMember_code(String.valueOf(mUserMap.get("member_code").toString()));
						
						user.setNickname(String.valueOf(mUserMap.get("nickname").toString()));
						
						user.setGroup(BigInteger.valueOf(Long.valueOf(mUserMap.get("member_group").toString())));
						
						user.setGender(BigInteger.valueOf(Long.valueOf(mUserMap.get("member_sex").toString())));
						
						user.setLevel(Integer.valueOf(mUserMap.get("member_level").substring(mUserMap.get("member_level").length()-4, mUserMap.get("member_level").length())));
						
						//根据等级编号查出等级名称
						MDataMap mLevelMap = DbUp.upTable("mc_member_level").one("level_code",mUserMap.get("member_level").toString(),"manage_code",getManageCode());
						if(mLevelMap!=null){
							
							user.setLevel_name(mLevelMap.get("level_name"));                                 //等级名称
						}
						
						user.setScore(Integer.valueOf(mUserMap.get("member_score").toString()));
						
						user.setMobile(mUserMap.get("mobile_phone").toString());
						
						String Score_unit = bConfig("newscenter.Score_unit");
						
						user.setScore_unit(Score_unit);
						
						user.setCreate_time(String.valueOf(mUserMap.get("create_time").toString()));
						
						AppPhoto avatar = new AppPhoto();
						
						avatar.setLarge(String.valueOf(mUserMap.get("member_avatar")));
						avatar.setThumb(String.valueOf(mUserMap.get("member_avatar")));
						user.setAvatar(avatar);
						comments.setUser(user);						
						
					}
					if(!mDataMap.get("replay_code").equals("")){
						//查出回复用户编号
						MDataMap Map = DbUp.upTable("nc_comment").one("comment_code",mDataMap.get("replay_code"));
						
						if(Map!=null){							
							//查出回复用户 信息                           
							MDataMap mReplyUserMap = DbUp.upTable("mc_extend_info_star").one("member_code",Map.get("create_member"));
							
							if(mReplyUserMap != null){
								MemberInfo mreplay = new MemberInfo();
								mreplay.setMember_code(String.valueOf(mReplyUserMap.get("member_code")));
								
								mreplay.setNickname(String.valueOf(mReplyUserMap.get("nickname")));
								
								mreplay.setGroup(BigInteger.valueOf(Long.valueOf(mReplyUserMap.get("member_group"))));
								
								mreplay.setGender(BigInteger.valueOf(Long.valueOf(mReplyUserMap.get("member_sex"))));
								
								mreplay.setLevel(Integer.valueOf(mReplyUserMap.get("member_level").substring(mReplyUserMap.get("member_level").length()-4, mReplyUserMap.get("member_level").length())));
								
								//根据等级编号查出等级名称
								MDataMap mLevelMap = DbUp.upTable("mc_member_level").one("level_code",mReplyUserMap.get("member_level"));
								if(mLevelMap!=null){
									
									mreplay.setLevel_name(mLevelMap.get("level_name"));                                 //等级名称
								}
								
								mreplay.setScore(Integer.valueOf(mReplyUserMap.get("member_score")));
								
								mreplay.setMobile(mReplyUserMap.get("mobile_phone"));
								
								String Score_unit = bConfig("newscenter.Score_unit");
								
								mreplay.setScore_unit(Score_unit);
								
								mreplay.setCreate_time(String.valueOf(mReplyUserMap.get("create_time")));
								
								AppPhoto avatar = new AppPhoto();
								
								avatar.setLarge(String.valueOf(mReplyUserMap.get("member_avatar")));
								avatar.setThumb(String.valueOf(mReplyUserMap.get("member_avatar")));
								mreplay.setAvatar(avatar);
								comments.setReply(mreplay);
							}
						}
					}										
					/*返回评论列表*/
					result.getComments().add(comments);
				}
			
			}
			
			
		}
		return result;
	}

}
