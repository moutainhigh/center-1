package com.cmall.groupcenter.comment.api;

import com.cmall.groupcenter.comment.model.PostCommentListInput;
import com.cmall.groupcenter.comment.model.PostCommentListResult;
import com.cmall.groupcenter.service.PostCommentService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;

public class ApiPostCommentListPage extends RootApiForManage<PostCommentListResult, PostCommentListInput> {

	public PostCommentListResult Process(PostCommentListInput inputParam, MDataMap mRequestMap) {
		String appCode = getManageCode();
		PostCommentListResult result = new PostCommentService().findCommentListPage(inputParam, appCode);
		return result;
	}

}
