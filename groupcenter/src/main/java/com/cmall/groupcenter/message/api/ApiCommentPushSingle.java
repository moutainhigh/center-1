package com.cmall.groupcenter.message.api;

import com.cmall.groupcenter.message.model.CommentPushSingleList;
import com.cmall.groupcenter.message.model.CommentPushSingleListResult;
import com.cmall.groupcenter.model.MPageData;
import com.cmall.groupcenter.util.DataPaging;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 模块:个人中心->消息
 * 功能:查询当前用户的推送消息
 * is_read:4497465200180001(未读)|4497465200180002(已读)
 * @author LHY
 * 2015年1月15日 下午3:44:07
 */
public class ApiCommentPushSingle extends RootApiForToken<CommentPushSingleList, CommentPushSingleInput>{

	public CommentPushSingleList Process(CommentPushSingleInput inputParam, MDataMap mRequestMap) {
		CommentPushSingleList result = new CommentPushSingleList();
		String userCode = getUserCode();
		MDataMap mDataMap = new MDataMap();
		mDataMap.put("user_code", userCode);
		
		String account_code = String.valueOf(DbUp.upTable("mc_member_info").dataGet("account_code", "member_code=:user_code", mDataMap));
		mDataMap = new MDataMap();
		mDataMap.put("account_code", account_code);
		
		MPageData pageData = DataPaging.upPageData("sc_comment_push_single", "zid, uid, content,send_time,is_read, create_time", "create_time desc", mDataMap, inputParam.getPaging());
		for(MDataMap map: pageData.getListData()) {
			CommentPushSingleListResult push = new CommentPushSingleListResult();
			push.setContent(map.get("content"));
			push.setSendTime(map.get("create_time"));
			push.setIsRead(map.get("is_read"));
			result.getList().add(push);
		}
		result.setPaged(pageData.getPageResults());
		
		for(MDataMap map: pageData.getListData()) {
			if("4497465200180001".equals(map.get("is_read"))) {//判断未读,然后设置为已读
				map.put("is_read", "4497465200180002");
				DbUp.upTable("sc_comment_push_single").update(map);
			}
		}
		return result;
	}
}
