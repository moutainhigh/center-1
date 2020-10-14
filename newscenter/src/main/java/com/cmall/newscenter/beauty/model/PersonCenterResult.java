package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;


/**
 * 惠美丽-个人中心输出类
 * @author yangrong	
 * date 2014-8-20
 * @version 1.0
 */
public class PersonCenterResult extends RootResultWeb {

	@ZapcomApi(value = "头像")
	private String avatar = "";
	
	@ZapcomApi(value = "昵称")
	private String nickname = "";
	
	@ZapcomApi(value = "皮肤类型" ,demo="449746650001=混合皮肤  449746650002=干性皮肤  449746650003=中性皮肤  449746650004=油性皮肤 449746650005=敏感皮肤")
	private String skin_type = "";
	
	@ZapcomApi(value="年代")
	private String century = "";
	
	@ZapcomApi(value="用户性别")
	private String sex = "";

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

	public String getCentury() {
		return century;
	}

	public void setCentury(String century) {
		this.century = century;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

}
