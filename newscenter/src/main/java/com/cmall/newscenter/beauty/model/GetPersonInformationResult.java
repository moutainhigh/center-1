package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 惠美丽-获取个人资料输出类
 * 
 * @author yangrong date 2014-8-20
 * @version 1.0
 */
public class GetPersonInformationResult extends RootResultWeb {

	@ZapcomApi(value = "头像")
	private String avatar = "";

	@ZapcomApi(value = "昵称")
	private String nickname = "";

	@ZapcomApi(value = "性别", remark = "男=4497465100010002   女=4497465100010003")
	private String sex = "";

	@ZapcomApi(value = "皮肤类型编码")
	private String skin_type = "";

	@ZapcomApi(value = "皮肤类型名称")
	private String skintype_name = "";

	@ZapcomApi(value = "护肤需求编码")
	private String hopeful = "";

	@ZapcomApi(value = "生日")
	private String birthday = "";

	@ZapcomApi(value = "地区")
	private String area_code = "";

	@ZapcomApi(value = "年代")
	private String century = "";

	@ZapcomApi(value = "默认收货地址")
	private BeautyAddress adress = new BeautyAddress();

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

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getSkin_type() {
		return skin_type;
	}

	public void setSkin_type(String skin_type) {
		this.skin_type = skin_type;
	}

	public String getHopeful() {
		return hopeful;
	}

	public void setHopeful(String hopeful) {
		this.hopeful = hopeful;
	}

	public BeautyAddress getAdress() {
		return adress;
	}

	public void setAdress(BeautyAddress adress) {
		this.adress = adress;
	}

	public String getSkintype_name() {
		return skintype_name;
	}

	public void setSkintype_name(String skintype_name) {
		this.skintype_name = skintype_name;
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
