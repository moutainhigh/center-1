package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 商品评论列表-》》评论人信息  输出
 * @author houwen
 * date 2014-09-19
 * @version 1.0
 */
public class Commentator {

	@ZapcomApi(value="评论人code")
	private String member_code = "";
	
	@ZapcomApi(value="评论人昵称")
	private String nickname = "";
	
	@ZapcomApi(value="肤质",remark="混合皮肤:449746650001;干性皮肤:449746650002;中性皮肤:449746650003;油性皮肤:449746650004;敏感皮肤:449746650005")
	private String skin_type = "";

	@ZapcomApi(value="头像URl")
	private String member_avatar = "";

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

	public String getMember_avatar() {
		return member_avatar;
	}

	public void setMember_avatar(String member_avatar) {
		this.member_avatar = member_avatar;
	}

	public String getMember_code() {
		return member_code;
	}

	public void setMember_code(String member_code) {
		this.member_code = member_code;
	}

}
