package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 评论人列表类输出
 * @author houwen
 * date 2014-08-26
 * @version 1.0
 */
public class PostPublisherList {

	
	@ZapcomApi(value="用户编号")
	private String member_code = "";
	
	@ZapcomApi(value="头像")
	private String member_avatar = "";
	
	@ZapcomApi(value="昵称")
	private String nickname  = "";

	@ZapcomApi(value="皮肤类型",remark="混合皮肤:449746650001;干性皮肤:449746650002;中性皮肤:449746650003;油性皮肤:449746650004;敏感皮肤:449746650005")
	private String skin_type  = "";

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

	public String getMember_code() {
		return member_code;
	}

	public void setMember_code(String member_code) {
		this.member_code = member_code;
	}
}
