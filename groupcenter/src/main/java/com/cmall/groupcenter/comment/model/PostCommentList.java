package com.cmall.groupcenter.comment.model;

import com.cmall.groupcenter.util.DateTimeUtil;
import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class PostCommentList {
	@ZapcomApi(value="帖子ID",remark="帖子ID", require= 1)
	private String postCode;
	@ZapcomApi(value="昵称",remark="昵称", require= 1)
	private String nickName;
	@ZapcomApi(value="头像",remark="头像", require= 1)
	private String picUrl;
	@ZapcomApi(value="评论内容",remark="评论内容", require= 1)
	private String content;
	@ZapcomApi(value="评论时间",remark="评论时间", require= 1)
	private String publishTime;
	@ZapcomApi(value="格式化后的评论时间",remark="格式化后的评论时间", require= 1)
	private String formatPublishTime;
	@ZapcomApi(value="评论人ID",remark="评论人ID", require= 1)
	private String publisherCode;
	@ZapcomApi(value="楼层",remark="楼层", require= 1)
	private String floor;
	@ZapcomApi(value="评论类型",remark="针对帖子的回复，针对评论的回复", require= 1)
	private String type;
	@ZapcomApi(value="评论内容ID",remark="评论内容ID", require= 1)
	private String commentCode;
	@ZapcomApi(value="被评论人昵称",remark="被评论人昵称", require= 1)
	private String publisher;
	@ZapcomApi(value="被评论人code",remark="被评论人code", require= 1)
	private String publishedCode;
	@ZapcomApi(value="评论的状态",remark="评论的状态")
	private String status;
	
	public String getPostCode() {
		return postCode;
	}
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getPicUrl() {
		return picUrl;
	}
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getPublishTime() {
		return publishTime;
	}
	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}
	public String getFormatPublishTime() {
		formatPublishTime = DateTimeUtil.getDateDiff(getPublishTime());
		return formatPublishTime;
	}
	public void setFormatPublishTime(String formatPublishTime) {
		this.formatPublishTime = formatPublishTime;
	}
	public String getPublisherCode() {
		return publisherCode;
	}
	public void setPublisherCode(String publisherCode) {
		this.publisherCode = publisherCode;
	}
	public String getFloor() {
		return floor;
	}
	public void setFloor(String floor) {
		this.floor = floor;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCommentCode() {
		return commentCode;
	}
	public void setCommentCode(String commentCode) {
		this.commentCode = commentCode;
	}
	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public String getPublishedCode() {
		return publishedCode;
	}
	public void setPublishedCode(String publishedCode) {
		this.publishedCode = publishedCode;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}