package com.cmall.newscenter.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 设置 - 意见反馈输入类
 * @author liqiang
 * date 2014-7-21
 * @version 1.0
 */
public class SettingFeedBackInput extends RootInput{
	
	@ZapcomApi(value="意见反馈")
	private String text="";

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
