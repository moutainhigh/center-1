package com.cmall.usercenter.template;

import com.srnpr.zapcom.topapi.RootInput;

public class ApiGetShopTemplateInput extends RootInput {

	
	
	/**
	 * 调用类型   传入1为表示读取自动保存的信息  传入2表示自动保存信息  传入3表示发布
	 */
	private int callType=1;
	
	
	
	/**
	 * 编辑的对象信息
	 */
	private String uid="";
	
	
	/**
	 * 内容
	 */
	private String content="";
	
	
	/**
	 * 预览内容
	 */
	private String preview="";
	

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getCallType() {
		return callType;
	}

	public void setCallType(int callType) {
		this.callType = callType;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getPreview() {
		return preview;
	}

	public void setPreview(String preview) {
		this.preview = preview;
	}

	
	
	
	
}
