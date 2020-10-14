package com.cmall.groupcenter.service;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.comment.model.PostCommentList;
import com.cmall.groupcenter.comment.model.PostCommentListInput;
import com.cmall.groupcenter.comment.model.PostCommentListResult;
import com.cmall.groupcenter.comment.util.ApiPostsCommentUtil;
import com.cmall.groupcenter.model.MPageData;
import com.cmall.groupcenter.util.DataPaging;
import com.cmall.groupcenter.util.MemberUtil;
import com.srnpr.zapcom.basemodel.MDataMap;

public class PostCommentService {
	/**
	 * 获取帖子的评论列表
	 * @param inputParam
	 * @param appCode
	 * @return
	 */
	public PostCommentListResult findCommentListPage(PostCommentListInput inputParam, String appCode) {
		PostCommentListResult result = new PostCommentListResult();
		MDataMap mDataMap = new MDataMap();
		mDataMap.put("post_code", inputParam.getPostCode());
		mDataMap.put("app_code", appCode);
		mDataMap.put("is_delete", "0");
		mDataMap.put("status", "4497472000010001");
		String sorted = "desc";
		if(StringUtils.isNotEmpty(inputParam.getSorted())) {
			sorted = inputParam.getSorted();
		}
		MPageData pageData = DataPaging.upPageData(
						"nc_posts_comment",
						"zid, uid, status, post_code, comment_content, comment_img, publisher_code, publish_time, " +
						"comment_code, comment_nick_name, floor, type, criticsTel, post_publisher, published_code",
						"publish_time "+sorted+ ",floor*1 desc",
						" post_code=:post_code and app_code=:app_code and is_delete=:is_delete",
						mDataMap, inputParam.getPaging());
		for(MDataMap map: pageData.getListData()) {
			PostCommentList commentList = new PostCommentList();
			String publisherCode = map.get("publisher_code");
			String publishedCode = map.get("published_code");
			String content = map.get("comment_content");
			if("4497472000010002".equals(map.get("status"))) {
				content = "此评论包含敏感词汇，不予以显示！";
			}
			commentList.setStatus(map.get("status"));
			commentList.setContent(content);
			commentList.setFloor(map.get("floor"));
			commentList.setPublishTime(map.get("publish_time"));
			commentList.setCommentCode(map.get("comment_code"));
			commentList.setType(map.get("type"));
			commentList.setPublisher(map.get("post_publisher"));
			commentList.setPublishedCode(publishedCode);
			commentList.setPostCode(inputParam.getPostCode());
			commentList.setPublisherCode(publisherCode);
			
			MDataMap getMap = new MemberUtil().getPublisherInfo(publisherCode);
			
			String mobile1 = getMap.get("mobile");
			if(StringUtils.isNotEmpty(mobile1)) {
				mobile1 = mobile1.substring(0, mobile1.length()-(mobile1.substring(3)).length())+"****"+mobile1.substring(7);
			}
			String nickName1 = getMap.get("nickname");
			commentList.setNickName(StringUtils.isNotEmpty(nickName1)?nickName1:mobile1);
			commentList.setPicUrl(getMap.get("head_icon_url"));
			
			MDataMap getMap2 = new MemberUtil().getPublisherInfo(publishedCode);
			
			String mobile2 = getMap2.get("mobile");
			if(StringUtils.isNotEmpty(mobile2)) {
				mobile2 = mobile2.substring(0, mobile2.length()-(mobile2.substring(3)).length())+"****"+mobile2.substring(7);
			}
			String nickName2 = getMap2.get("nickname");
			commentList.setPublisher(StringUtils.isNotEmpty(nickName2)?nickName2:mobile2);
			
			result.getList().add(commentList);
		}
		result.setNum(ApiPostsCommentUtil.getPostsCommentCount(inputParam.getPostCode()));
		result.setResultCode(1);
		result.setPaged(pageData.getPageResults());
		return result;
	}
}
