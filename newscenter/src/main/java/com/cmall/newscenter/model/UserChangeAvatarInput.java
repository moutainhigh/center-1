package com.cmall.newscenter.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 用户 - 修改头像
 * @author liqiang
 * date 2014-7-23
 * @version 1.0
 */
public class UserChangeAvatarInput extends RootInput{
	
	@ZapcomApi(value="上传文件名",demo="xxxx.jpg")
	private String avatar="";

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

}
