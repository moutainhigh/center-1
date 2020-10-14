package com.cmall.groupcenter.comment.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.comment.model.PostReplyInput;
import com.cmall.groupcenter.comment.model.PostReplyResult;
import com.cmall.groupcenter.comment.util.ApiPostsCommentUtil;
import com.cmall.groupcenter.util.MemberUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 针对帖子进行回复
 * @author LHY
 * 2015年4月27日 上午10:29:59
 */
public class ApiPostReply extends RootApiForToken<PostReplyResult, PostReplyInput> {

	public PostReplyResult Process(PostReplyInput inputParam, MDataMap mRequestMap) {
		PostReplyResult result = new PostReplyResult();
		
		String userCode = getUserCode();
		String appCode = getManageCode();
		String postCode = inputParam.getPost_code();
		
		MDataMap mWhereMap = new MDataMap();
		mWhereMap.put("pid", postCode);
		// 根据帖子ID查询帖子相关信息
		List<MDataMap> wMDataMap = new ArrayList<MDataMap>();
		wMDataMap = DbUp.upTable("nc_post").queryAll("", "", "", mWhereMap);
		
		MDataMap getMap =  new MemberUtil().getPublisherInfo(userCode);
		if(getMap==null || getMap.size()==0) {
			result.setResultCode(-1);
			result.setResultMessage("发送失败了");
			return result;
		}
		// 锁定帖子
		//String sLockCode = WebHelper.addLock(500, postCode);
		//if(StringUtils.isNotEmpty(sLockCode)) {
			MDataMap insertMap = new MDataMap();
			insertMap.put("post_code", postCode);
			insertMap.put("comment_content", inputParam.getComment_content().trim());
			insertMap.put("comment_img", StringUtils.isEmpty(getMap.get("head_icon_url"))?"":getMap.get("head_icon_url"));
			insertMap.put("publisher_code", userCode); //评论人Id
			insertMap.put("comment_nick_name", StringUtils.isEmpty(getMap.get("nickname"))?"":getMap.get("nickname")); //评论人昵称
			insertMap.put("comment_code", WebHelper.upCode("HML")); //评论ID
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			insertMap.put("publish_time", df.format(new Date()));
			//前台评论一条帖子， 默认为审核通过状态,审核通过：4497472000010001；审核拒绝：4497472000010002；
			insertMap.put("status", "4497472000010001");
			insertMap.put("post_title", wMDataMap.get(0).get("p_title")); // 被评论帖子标题
			insertMap.put("type", "0"); // 针对帖子的评论:0；针对评论的评论:1
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
		return result;
	}
}