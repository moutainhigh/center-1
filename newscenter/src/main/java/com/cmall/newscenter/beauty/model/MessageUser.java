package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 消息-》 用户类输出
 * @author houwen
 * date 2014-09-16
 * @version 1.0
 */
public class MessageUser {

	
	@ZapcomApi(value="头像")
	private String member_avatar = "";
	
	@ZapcomApi(value="昵称")
	private String nickname  = "";

	@ZapcomApi(value="皮肤类型")
	private String skin_type  = "";

	@ZapcomApi(value="用户ID")
	private String user_id = "";
	
	@ZapcomApi(value="手机号")
	private String mobile_phone  = "";
	
	public String getMember_avatar() {
		return member_avatar;
	}

	public void setMember_avatar(String member_avatar) {
		this.member_avatar = member_avatar;
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

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getMobile_phone() {
		return mobile_phone;
	}

	public void setMobile_phone(String mobile_phone) {
		this.mobile_phone = mobile_phone;
	}
	
}
