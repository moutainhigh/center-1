package com.cmall.groupcenter.message.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class UserAdviceFeedbackResult {
	@ZapcomApi(value="用户编号", remark="用户编号")
	private String userCode;
	@ZapcomApi(value="创建时间",remark="创建时间")
	private String createTime;
	@ZapcomApi(value="消息内容",remark="消息内容")
	private String descprition;
	
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getDescprition() {
		return descprition;
	}
	public void setDescprition(String descprition) {
		this.descprition = descprition;
	}
}