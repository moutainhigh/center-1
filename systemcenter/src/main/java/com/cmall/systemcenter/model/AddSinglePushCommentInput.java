package com.cmall.systemcenter.model;


/**
 * @author dyc
 * 单一用户推送信息输入类
 * */
public class AddSinglePushCommentInput{

	/**
	 * 信息标题
	 * */
	private String title = "";
	/**
	 * 信息内容
	 */
	private String content = "";
	/**
	 * 用户编号
	 * */
	private String userCode = "";
	/**
	 * 信息属性
	 * ctype=xx&cvalue=xxx
	 * */
	private String properties = "";
	/**
	 * 预计发送时间
	 * yyyy-MM-dd HH:mm:ss
	 * */
	private String preSendTime = "";
	
	/**
	 * APP编码
	 * */
	private String appCode = "";
	/**
	 * 消息类型
	 * */
	private String type = "";
	
	/**
	 * 账户编号
	 * */
	private String accountCode = "";
	
	/**
	 * 关联编号
	 * */
	private String relationCode = "";
	
	/**
	 * 关联编号
	 * */
	private String sendStatus = "";
	
	public String getSendStatus() {
		return sendStatus;
	}
	public void setSendStatus(String sendStatus) {
		this.sendStatus = sendStatus;
	}
	public String getRelationCode() {
		return relationCode;
	}
	public void setRelationCode(String relationCode) {
		this.relationCode = relationCode;
	}
	/**
	 * 获取  信息标题
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * 设置 信息标题
	 * @param title 
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * 获取  信息内容
	 */
	public String getContent() {
		return content;
	}
	/**
	 * 设置 信息内容
	 * @param content 
	 */
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * 获取  用户编号
	 */
	public String getUserCode() {
		return userCode;
	}
	/**
	 * 设置 用户编号
	 * @param userCode 
	 */
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	/**
	 * 获取  信息属性
	 * ctype=xx&cvalue=xxx
	 */
	public String getProperties() {
		return properties;
	}
	/**
	 * 设置 信息属性
	 * ctype=xx&cvalue=xxx
	 * @param properties 
	 */
	public void setProperties(String properties) {
		this.properties = properties;
	}
	/**
	 * 获取  预计发送时间
	 * yyyy-MM-dd HH:mm:ss
	 */
	public String getPreSendTime() {
		return preSendTime;
	}
	/**
	 * 设置 预计发送时间
	 * yyyy-MM-dd HH:mm:ss
	 * @param preSendTime 
	 */
	public void setPreSendTime(String preSendTime) {
		this.preSendTime = preSendTime;
	}
	/**
	 * 获取  APP编码
	 */
	public String getAppCode() {
		return appCode;
	}
	/**
	 * 设置 APP编码
	 * @param appCode 
	 */
	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}
	/**
	 * 获取  消息类型
	 */
	public String getType() {
		return type;
	}
	/**
	 * 设置 消息类型
	 * @param type 
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * 获取  账户编号
	 */
	public String getAccountCode() {
		return accountCode;
	}
	/**
	 * 设置 
	 * @param 账户编号
	 */
	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}
	
	
	
}
