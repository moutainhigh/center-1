package com.cmall.newscenter.group.api;


import com.cmall.newscenter.group.model.PostListResult;
import com.cmall.newscenter.group.service.PostService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 按月份获取帖子api
 * @author chenxk
 *
 */
public class ApiGetPostsByCurrentMonth extends RootApiForManage<PostListResult, RootInput>{

	public PostListResult Process(RootInput inputParam,
			MDataMap mRequestMap) {
		
		return new PostService().getPostListByCurrentMonth(getManageCode());
	}
	
}
