package com.cmall.groupcenter.comment.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.comment.model.PostCommentReplyInput;
import com.cmall.groupcenter.comment.model.PostCommentReportResult;
import com.cmall.groupcenter.comment.util.ApiPostsCommentUtil;
import com.cmall.groupcenter.util.MemberUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 针对评论进行跟帖回复
 * @author LHY
 * 2015年4月24日 下午5:03:46
 */
public class ApiPostCommentReply extends RootApiForToken<PostCommentReportResult, PostCommentReplyInput> {

	public PostCommentReportResult Process(PostCommentReplyInput inputParam, MDataMap mRequestMap) {
		PostCommentReportResult result = new PostCommentReportResult();
		
		if (result.upFlagTrue()) {
			MDataMap mWhereMap = new MDataMap();
			MDataMap insertMap = new MDataMap();
			List<MDataMap> wMDataMap = new ArrayList<MDataMap>();
			
			String userCode = getUserCode();
			String postCode = inputParam.getPost_code();
			String commentCode = inputParam.getComment_code();
			String appCode = getManageCode();
			
			MDataMap getMap =  new MemberUtil().getPublisherInfo(userCode);
			if(getMap==null || getMap.size()==0) {
				result.setResultCode(-1);
				result.setResultMessage("发送失败了");
				return result;
			}
			
			mWhereMap.put("comment_code", commentCode);
			wMDataMap = DbUp.upTable("nc_posts_comment").queryAll("", "", "", mWhereMap);//根据评论ID查询评论相关信息
			
			if (wMDataMap.size() != 0) {
				// 锁定帖子
				//String sLockCode = WebHelper.addLock(500, postCode);
				//if(StringUtils.isNotEmpty(sLockCode)) {
					insertMap.put("post_code", postCode);
					insertMap.put("comment_img", StringUtils.isEmpty(getMap.get("head_icon_url"))?"":getMap.get("head_icon_url"));
					insertMap.put("comment_code", WebHelper.upCode("HML")); //评论ID
					insertMap.put("commented_code", commentCode); //被评论的评论ID
					insertMap.put("publisher_code", userCode); //评论人Id
					insertMap.put("comment_nick_name", StringUtils.isEmpty(getMap.get("nickname"))?"":getMap.get("nickname")); //评论人昵称
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //评论时间 为系统时间
					insertMap.put("publish_time", df.format(new Date()));
					insertMap.put("comment_content", inputParam.getComment_content().trim());
					insertMap.put("status", "4497472000010001"); //前台评论一条帖子，默认为待审核状态,审核通过：4497472000010001；审核拒绝：4497472000010002
					insertMap.put("published_code", wMDataMap.get(0).get("publisher_code")); //被评论人code
					insertMap.put("post_publisher", wMDataMap.get(0).get("comment_nick_name")); //被评论人昵称
					insertMap.put("post_title", wMDataMap.get(0).get("comment_content")); //被评论内容
					insertMap.put("type", "1"); //针对帖子的评论：0；针对 评论的评论：1
					insertMap.put("app_code", appCode);
					insertMap.put("criticsTel", StringUtils.isEmpty(getMap.get("mobile"))?"":getMap.get("mobile")); //评论人手机号
					synchronized (insertMap) {
						String uuid = DbUp.upTable("nc_posts_comment").dataInsert(insertMap);
						DbUp.upTable("nc_posts_comment").dataExec("UPDATE nc_posts_comment a,(SELECT COUNT(*) num FROM nc_posts_comment WHERE post_code =:post_code ) b SET floor = b.num WHERE uid = '"+uuid+"'", insertMap);
						MDataMap updateMap = new MDataMap();
						
						updateMap.put("pid", postCode);
						
						DbUp.upTable("nc_post").dataExec("UPDATE nc_post SET comment_num = comment_num+1 WHERE pid =:pid ", updateMap);
						result.setMap(ApiPostsCommentUtil.getPostsComment(uuid));
						result.setNum(ApiPostsCommentUtil.getPostsCommentCount(postCode));
					}
					//WebHelper.unLock(sLockCode);
				//}
				result.setResultCode(1);
				result.setResultMessage("发送成功啦");
			}
		}
		return result;
	}
}