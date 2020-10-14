package com.cmall.groupcenter.userinfo.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class UserInfoInput extends RootInput{

	@ZapcomApi(value="昵称")
	private String nickName = "";
	@ZapcomApi(value="生日")
	private String birthday = "";
	@ZapcomApi(value="性别",remark="男：4497465100010002 女：4497465100010003")
	private String gender = "";
	@ZapcomApi(value="地区")
	private String region = "";
	@ZapcomApi(value="头像链接")
	private String headIconUrl = "";

	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getHeadIconUrl() {
		return headIconUrl;
	}
	public void setHeadIconUrl(String headIconUrl) {
		this.headIconUrl = headIconUrl;
	}
	
	
}
