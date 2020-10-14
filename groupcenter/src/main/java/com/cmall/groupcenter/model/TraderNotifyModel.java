package com.cmall.groupcenter.model;

/**
 * 预存款提醒信息
 * @author panwei
 *
 */
public class TraderNotifyModel {

	//商户编号
	private String traderCode="";
	//邮件主题
	private String title="";
	//邮件和短信内容
	private String content="";
	//是否发送邮件
	private boolean isEmail=false;
	//是否发送短信
	private boolean isPhone=false;
	
	public String getTraderCode() {
		return traderCode;
	}
	public void setTraderCode(String traderCode) {
		this.traderCode = traderCode;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public boolean isEmail() {
		return isEmail;
	}
	public void setEmail(boolean isEmail) {
		this.isEmail = isEmail;
	}
	public boolean isPhone() {
		return isPhone;
	}
	public void setPhone(boolean isPhone) {
		this.isPhone = isPhone;
	}
	
	
}
