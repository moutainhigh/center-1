package com.cmall.groupcenter.recommend.model;

import java.util.LinkedList;
import java.util.List;

import com.cmall.groupcenter.comment.model.PostCommentList;
import com.cmall.groupcenter.groupapp.model.ShareModel;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class ApiGetRecommendDetailResult extends RootResultWeb{
	@ZapcomApi(value = "帖子id")
	private String pid;
	
	@ZapcomApi(value = "详情页封面图")
	private String coverPicUrl;
	
	@ZapcomApi(value = "文章封面图")
	private String articleCoverPicUrl;
	
	@ZapcomApi(value = "标题")
	private String title;
	
	@ZapcomApi(value="浏览数量")
	private String seenNum;
	
	@ZapcomApi(value = "评论数量")
	private String commentsNum;
	
	@ZapcomApi(value = "收藏数量")
	private String collectNum;
	
	@ZapcomApi(value = "分享数量")
	private String sharedNum;
	
	@ZapcomApi(value = "通过分享返利人数_2")
	private String rebatePersonNum;
	
	@ZapcomApi(value = "用户评论数_2")
	private String commentNum;

	@ZapcomApi(value = "返利金额")
	private String returnMoney;
	
	@ZapcomApi(value = "导语")
	private String intro;
	
	@ZapcomApi(value = "文章分享链接_2")
	private String articleShareLink;
	
	@ZapcomApi(value = "帖子收藏状态", remark="用户未登录时，此值无意义")
	private String favoriteState;
	
	@ZapcomApi(value = "帖子内容列表")
	private List<ApiGetRecommendDetailContentResult> listContent = new LinkedList<ApiGetRecommendDetailContentResult>();
	
	@ZapcomApi(value = "帖子评论列表")
	private List<PostCommentList> listComment = new LinkedList<PostCommentList>();
	
	

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getCoverPicUrl() {
		return coverPicUrl;
	}

	public void setCoverPicUrl(String coverPicUrl) {
		this.coverPicUrl = coverPicUrl;
	}

	public String getArticleCoverPicUrl() {
		return articleCoverPicUrl;
	}

	public void setArticleCoverPicUrl(String articleCoverPicUrl) {
		this.articleCoverPicUrl = articleCoverPicUrl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSeenNum() {
		return seenNum;
	}

	public void setSeenNum(String seenNum) {
		this.seenNum = seenNum;
	}

	public String getCommentsNum() {
		return commentsNum;
	}

	public void setCommentsNum(String commentsNum) {
		this.commentsNum = commentsNum;
	}

	public String getCollectNum() {
		return collectNum;
	}

	public void setCollectNum(String collectNum) {
		this.collectNum = collectNum;
	}

	public String getSharedNum() {
		return sharedNum;
	}

	public void setSharedNum(String sharedNum) {
		this.sharedNum = sharedNum;
	}

	public String getRebatePersonNum() {
		return rebatePersonNum;
	}

	public void setRebatePersonNum(String rebatePersonNum) {
		this.rebatePersonNum = rebatePersonNum;
	}

	public String getCommentNum() {
		return commentNum;
	}

	public void setCommentNum(String commentNum) {
		this.commentNum = commentNum;
	}

	public String getReturnMoney() {
		return returnMoney;
	}

	public void setReturnMoney(String returnMoney) {
		this.returnMoney = returnMoney;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getArticleShareLink() {
		return articleShareLink;
	}

	public void setArticleShareLink(String articleShareLink) {
		this.articleShareLink = articleShareLink;
	}

	public String getFavoriteState() {
		return favoriteState;
	}

	public void setFavoriteState(String favoriteState) {
		this.favoriteState = favoriteState;
	}

	public List<ApiGetRecommendDetailContentResult> getListContent() {
		return listContent;
	}

	public void setListContent(List<ApiGetRecommendDetailContentResult> listContent) {
		this.listContent = listContent;
	}

	public List<PostCommentList> getListComment() {
		return listComment;
	}

	public void setListComment(List<PostCommentList> listComment) {
		this.listComment = listComment;
	}
	
}
