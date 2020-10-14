package com.cmall.newscenter.group.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiForAdvertiseMoreInput extends RootInput{
	@ZapcomApi(value = "广告位code" , require=1,remark="邀请注册轮播:AdP150525100001")
	private String position="";

	/**
	 * 获取  position
	 */
	public String getPosition() {
		return position;
	}

	/**
	 * 设置 
	 * @param position 
	 */
	public void setPosition(String position) {
		this.position = position;
	}
	
	
}
