package com.cmall.newscenter.young.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
import com.cmall.newscenter.beauty.model.BeautyAddress;

/**
 * 小时代-修改个人资料输入类
 * 
 * @author yangrong date 2015-2-3
 * @version 1.0
 */
public class UpdatePersonInformationInput extends RootInput {

	@ZapcomApi(value = "头像", remark = "头像", demo = "http://....")
	private String avatar = "";

	@ZapcomApi(value = "昵称", remark = "昵称唯一", demo = "西瓜", require = 1)
	private String nickname = "";

	@ZapcomApi(value = "性别", remark = "性别", demo = "男=4497465100010002   女=4497465100010003")
	private String sex = "";

	@ZapcomApi(value = "生日")
	private String birthday = "";

	@ZapcomApi(value = "地区")
	private String area = "";

	@ZapcomApi(value = "收货地址信息", remark = "收货地址信息", demo = "北京市朝阳区高碑店小郊亭1376号润坤大厦十层   ")
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

	public BeautyAddress getAdress() {
		return adress;
	}

	public void BeautyAddress(BeautyAddress adress) {
		this.adress = adress;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public void setAdress(BeautyAddress adress) {
		this.adress = adress;
	}
	
	

}
