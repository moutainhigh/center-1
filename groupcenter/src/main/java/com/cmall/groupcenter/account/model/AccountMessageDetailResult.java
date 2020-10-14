package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 消息模块-设置消息为已读状态接口
 * @author lipengfei
 * @date 2015-5-29
 * email:lipf@ichsy.com
 *
 */
public class AccountMessageDetailResult{
	
	@ZapcomApi(value = "消息时间", remark = "时间戳", demo = "1433236913000")
	private String messageDate;
	
	
	@ZapcomApi(value = "消息标题", remark = "消息标题", demo = "新好友加入通知撒")
	private String messageTitle;
	
	@ZapcomApi(value = "消息内容", remark = "消息内容", demo = "xxx成功接受邀请成为你的微公社一度好友")
	private String messageContent;
	
	@ZapcomApi(value = "关联membercode", remark = "关联membercode", demo = "xxx成功接受邀请成为你的微公社一度好友")
	private String relationMemberCode;
	
	@ZapcomApi(value = "头像", remark = "头像", demo = "http://...")
	private String headUrl;
	
	@ZapcomApi(value = "昵称", remark = "昵称,查询用户扩展信息表，如果有则取出，如果没有则查询用户登录信息表，取出手机号，若仍没有则返回空字符串", demo = "http://...")
	private String nickName;
	
	@ZapcomApi(value = "是否是微公社member", remark = "1：是，0：否", demo = "")
	private int isGroup=1;
	

	public int getIsGroup() {
		return isGroup;
	}

	public void setIsGroup(int isGroup) {
		this.isGroup = isGroup;
	}

	public String getMessageDate() {
		return messageDate;
	}

	public void setMessageDate(String messageDate) {
		this.messageDate = messageDate;
	}

	public String getMessageTitle() {
		return messageTitle;
	}

	public void setMessageTitle(String messageTitle) {
		this.messageTitle = messageTitle;
	}

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	public String getRelationMemberCode() {
		return relationMemberCode;
	}

	public void setRelationMemberCode(String relationMemberCode) {
		this.relationMemberCode = relationMemberCode;
	}

	public String getHeadUrl() {
		return headUrl;
	}

	public void setHeadUrl(String headUrl) {
		this.headUrl = headUrl;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	
}
