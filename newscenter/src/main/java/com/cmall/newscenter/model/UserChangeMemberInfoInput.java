package com.cmall.newscenter.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 用户 - 修改资料
 * @author liqiang
 * date 2014-7-23
 * @version 1.0
 */
public class UserChangeMemberInfoInput extends RootInput{
	
	@ZapcomApi(value="昵称",demo="xxx",require=0)
	private String nickname="";

	@ZapcomApi(value="性别",require=0)
	private User_gender gender = new User_gender();

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public User_gender getGender() {
		return gender;
	}

	public void setGender(User_gender gender) {
		this.gender = gender;
	} 

}
