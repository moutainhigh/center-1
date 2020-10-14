package com.cmall.groupcenter.recommend.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.cmall.groupcenter.comment.model.PostCommentList;
import com.cmall.groupcenter.comment.model.PostCommentListInput;
import com.cmall.groupcenter.comment.model.PostCommentListResult;
import com.cmall.groupcenter.model.PageOption;
import com.cmall.groupcenter.recommend.model.ApiGetRecommendDetailInput;
import com.cmall.groupcenter.recommend.model.ApiGetRecommendDetailResult;
import com.cmall.groupcenter.service.NcPostService;
import com.cmall.groupcenter.service.PostCommentService;
import com.cmall.groupcenter.util.DateTimeUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 获取好物推荐详情-登录后
 * 
 * @author gaozx
 *
 */
public class ApiGetRecommendDetailAfterLogin
		extends
		RootApiForToken<ApiGetRecommendDetailResult, ApiGetRecommendDetailInput> {

	public ApiGetRecommendDetailResult Process(
			ApiGetRecommendDetailInput inputParam, MDataMap mRequestMap) {
		NcPostService ncPostService = new NcPostService();
		String manageCode = getManageCode();
		ApiGetRecommendDetailResult detailResult = ncPostService.getRecommendDetailService(inputParam, manageCode);
		if(detailResult.getResultCode() != 1) {
			detailResult.setResultMessage(bInfo(detailResult.getResultCode()));
		} else {
			//设置文章分享链接
			String shareLink = bConfig("groupcenter.recommedDetailArticleShareLink");
			shareLink = shareLink.replace("[pid]", inputParam.getPid());
			detailResult.setArticleShareLink(shareLink);
			
			MDataMap getFavoriteStatemap = new MDataMap("post_id", inputParam.getPid(), "member_code", getUserCode(), "app_code", manageCode);
			String favoriteState = ncPostService.getFavoriteStateOfUser(getFavoriteStatemap);
			detailResult.setFavoriteState(favoriteState);
			
			//获取评论列表
			PostCommentListInput commentInput = new PostCommentListInput();
			PageOption commentPage = new PageOption();
			commentPage.setLimit(2);
			commentPage.setOffset(0);
			commentInput.setPostCode(inputParam.getPid());
			commentInput.setPaging(commentPage);
			PostCommentListResult commentListRes = new PostCommentService().findCommentListPage(commentInput, manageCode);
			detailResult.setCommentNum(commentListRes.getPaged().getTotal() + "");
			List<PostCommentList> commentList = commentListRes.getList();
			for(int i=0; i<commentList.size(); i++) {
				//2015-06-02 15:57:43
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String toFormatTime = commentList.get(i).getPublishTime();
				try {
					commentList.get(i).setFormatPublishTime(DateTimeUtil.formatDate4PostComment(sdf.parse(toFormatTime)));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			detailResult.setListComment(commentListRes.getList());
		}
		ncPostService.resizeContentImgSize(detailResult, inputParam);
		bLogInfo(0, "ApiGetRecommendDetail-pid:" + inputParam.getPid() + "\n"
				+ JSON.toJSON(detailResult));
		return detailResult;
	}

}
