package com.cmall.newscenter.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * pc意见反馈-输入类
 * @author wangzx
 * date 2015-8-4
 * @version 1.0
 */
public class PcFeedbackInput extends RootInput {
	
	@ZapcomApi(value = "反馈内容",remark = "接收用户 Id" ,require = 0)
	private String  description = "";
	
	@ZapcomApi(value = "反馈图片",remark = "反馈图片url，多张用逗号分隔" ,require = 0)
	private String  imgUrl = "";
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	
	

	

}
