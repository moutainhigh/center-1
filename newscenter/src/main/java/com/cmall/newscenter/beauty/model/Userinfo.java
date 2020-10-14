package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 惠美丽用户信息类
 * @author yangrong
 * date 2014-9-10
 * @version 1.0
 */
public class Userinfo {
	
	@ZapcomApi(value = "头像")
	private String avatar = "";
	
	@ZapcomApi(value = "昵称")
	private String nickname = "";
	
	@ZapcomApi(value = "皮肤类型",demo="449746650001=混合皮肤  449746650002=干性皮肤  449746650003=中性皮肤  449746650004=油性皮肤 449746650005=敏感皮肤")
	private String skin_type = "";
	
	@ZapcomApi(value = "用户id")
	private String userid = "";
	
	@ZapcomApi(value = "手机号")
	private String phone = "";
	
	@ZapcomApi(value = "生日")
	private String birthday = "";

	@ZapcomApi(value="地区")
	private String area_code = "";
	
	@ZapcomApi(value="年代")
	private String century = ""; 

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getSkin_type() {
		return skin_type;
	}

	public void setSkin_type(String skin_type) {
		this.skin_type = skin_type;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getArea_code() {
		return area_code;
	}

	public void setArea_code(String area_code) {
		this.area_code = area_code;
	}

	public String getCentury() {
		return century;
	}

	public void setCentury(String century) {
		this.century = century;
	}
	
	
}
