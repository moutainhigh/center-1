package com.cmall.groupcenter.message.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 模块:个人中心->消息
 * 功能:返回数据信息
 * @author LHY
 * 2015年1月15日 下午4:33:11
 */
public class CommentPushSingleListResult {
	@ZapcomApi(value="发送时间",remark="发送时间", require= 1)
	private String sendTime;
	@ZapcomApi(value="消息内容",remark="消息内容", require= 1)
	private String content;
	@ZapcomApi(value="是否已读",remark="是否已读", require= 1)
	private String isRead;
	
	public String getSendTime() {
		return sendTime;
	}
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getIsRead() {
		return isRead;
	}
	public void setIsRead(String isRead) {
		this.isRead = isRead;
	}
}