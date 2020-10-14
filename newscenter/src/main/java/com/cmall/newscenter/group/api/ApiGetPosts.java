package com.cmall.newscenter.group.api;


import com.cmall.newscenter.group.model.PostListInput;
import com.cmall.newscenter.group.model.PostListResult;
import com.cmall.newscenter.group.service.PostService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 获取帖子api
 * @author chenxk
 *
 */
public class ApiGetPosts extends RootApiForManage<PostListResult, PostListInput>{

	public PostListResult Process(PostListInput inputParam,
			MDataMap mRequestMap) {
		//分享url前缀
 		String weiPrefix = bConfig("groupcenter.app_recommendPageUrl");

		return new PostService().getPostList(inputParam,getManageCode(),weiPrefix);
	}
	
}
