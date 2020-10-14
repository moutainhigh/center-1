package com.cmall.newscenter.beauty.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cmall.newscenter.beauty.model.PostsCommentReplyAddInput;
import com.cmall.newscenter.beauty.model.PostsReplyAddResult;
import com.cmall.productcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 对帖子评论进行评论
 * @author houwen	
 * date 2014-09-28
 * @version 1.0
 */

public class PostCommentReplyAddApi extends RootApiForToken<PostsReplyAddResult, PostsCommentReplyAddInput> {

	public PostsReplyAddResult Process(PostsCommentReplyAddInput inputParam,
			MDataMap mRequestMap) {

		PostsReplyAddResult result = new PostsReplyAddResult();
		
		MDataMap mWhereMap = new MDataMap();
		if(result.upFlagTrue()){
			
			MDataMap mDataMap = new MDataMap();
			MDataMap mReplayDataMap = new MDataMap();
			List<MDataMap> wReplayMDataMap = new ArrayList<MDataMap>();
			List<MDataMap> wMDataMap = new ArrayList<MDataMap>();
			mWhereMap.put("comment_code", inputParam.getComment_code());
			//根据评论ID查询评论相关信息
			wMDataMap = DbUp.upTable("nc_posts_comment").queryAll("", "", "", mWhereMap);
			if(wMDataMap.size()!=0){
				
				   String publisher = wMDataMap.get(0).get("publisher_code");
					
					mDataMap.put("comment_code",WebHelper.upCode("HML")); // 评论ID
					
					mDataMap.put("commented_code",inputParam.getComment_code()); // 被评论的评论ID
					
					mDataMap.put("post_code", inputParam.getPost_code());
					
					mDataMap.put("publisher_code", getUserCode());  //评论人Id

					List<MDataMap>  commenterList = this.getNickName(getUserCode());
					if(commenterList.size()!=0){
					mDataMap.put("comment_nick_name",commenterList.get(0).get("nickname"));  //评论人昵称
					}
					//评论时间 为系统时间
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //设置日期格式
					
					mDataMap.put("publish_time",df.format(new Date()));
					
					mDataMap.put("comment_content",inputParam.getComment_content());
					
					mDataMap.put("status","449746800001"); //前台评论一条帖子，默认为待审核状态   审核通过：449746800001；审核拒绝：449746800002；待审核：  449746800003
					
					mDataMap.put("published_code", publisher);   //被评论人code
					List<MDataMap>  publisherList = this.getNickName(publisher);
					if(publisherList.size()!=0){
					mDataMap.put("post_publisher", publisherList.get(0).get("nickname"));  //被评论人昵称
					}
					mDataMap.put("post_title",wMDataMap.get(0).get("comment_content"));  //被评论内容
					
					mDataMap.put("type", "1");    //针对帖子的评论：0；针对 评论的评论：1
					
					mDataMap.put("app_code", getManageCode());//app
					mReplayDataMap.put("post_code", inputParam.getPost_code());
					
					synchronized(wReplayMDataMap){
						
						//根据帖子ID查询帖子相关信息
						wReplayMDataMap = DbUp.upTable("nc_posts_comment").queryAll("", "", "", mReplayDataMap);
						
						mDataMap.put("floor", String.valueOf(wReplayMDataMap.size()+1));
						
						/*将帖子信息放入数据库中*/
						DbUp.upTable("nc_posts_comment").dataInsert(mDataMap);
						
						/*将回复消息插入到消息表中*/
						MessageTemplate.MessageReplyRule(inputParam.getPost_code(),getUserCode(), publisher,inputParam.getComment_content(), wMDataMap.get(0).get("comment_content"),getManageCode(),"449746920004");
						
						
						//推送表中插入一条消息
						
						MDataMap dbDataMap = DbUp.upTable("nc_sys_push_configure").one("configure_code","TS449746660005","status","449747090001","app_code",getManageCode());
						
						if(dbDataMap!=null && !dbDataMap.isEmpty()){
							
							String start = dbDataMap.get("push_time_start").toString();
							
							String end = dbDataMap.get("push_time_end").toString();
							
							String now = DateUtil.getSysTimeString();
							
							int num1 = now.compareTo(start);
							
							int num2 = end.compareTo(now);
							
							Boolean flag = num1>=0 && num2>=0;
							
							if(start.equals("全天") || flag ){
								
								MDataMap datamap = DbUp.upTable("mc_extend_info_star").one("member_code", getUserCode());
								
								String content = dbDataMap.get("comment").replace("***", datamap.get("nickname"));
								
								MDataMap insertmap = new MDataMap();
								
								if (datamap != null) {

									insertmap.inAllValues("accept_member",publisher,"comment", content , "push_time",DateUtil.getSysDateTimeString(), "jump_type", "6","jump_position", inputParam.getPost_code(), "push_status","4497465000070001", "create_time",DateUtil.getSysDateTimeString(), "app_code",getManageCode());
									
									DbUp.upTable("nc_comment_push_system").dataInsert(insertmap);
								}
							}
							
						}
					}
					
					
				}
			}
			
	
	return result;
	}
	
	
	/**
	 * 根据用户Id查询用户昵称
	 * @param member_code
	 * @return
	 */
	public List<MDataMap> getNickName(String member_code){
		
		MDataMap mWhereDataMap = new MDataMap();
		mWhereDataMap.put("member_code", member_code);
		
		List<MDataMap> mDataMap = DbUp.upTable("mc_extend_info_star").queryAll("", "", "", mWhereDataMap);
		return mDataMap;
	}

}
