package com.cmall.groupcenter.message.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 功能：意见反馈
 * @author LHY
 * 2015年1月16日 下午2:00:25
 */
public class UserAdviceFeedbackInput extends RootInput {
	@ZapcomApi(value="信息内容",remark="信息内容", require=1)
	private String description = "";

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}	
}