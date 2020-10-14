package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 客户端消息分类
 * @author sunyan
 *
 */
public class ApiHomeMessageClassResult{
	@ZapcomApi(value="消息分类",remark="系统消息/售后消息/物流通知/意见反馈")
	private String classify="";
	@ZapcomApi(value="消息分类编码",remark="系统消息：4497471600420001/售后消息：4497471600420002/物流通知:4497471600420003/意见反馈:4497471600420004")
	private String classifyCode="";
	@ZapcomApi(value="未读消息数量")
	private String count="";
	@ZapcomApi(value="消息内容")
	private String content="";
	@ZapcomApi(value="消息开始时间")
	private String start_time="";
	
	public String getClassifyCode() {
		return classifyCode;
	}
	public void setClassifyCode(String classifyCode) {
		this.classifyCode = classifyCode;
	}
	public String getClassify() {
		return classify;
	}
	public void setClassify(String classify) {
		this.classify = classify;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
		
}
