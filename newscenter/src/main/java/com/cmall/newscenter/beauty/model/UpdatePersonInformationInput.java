package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;


/**
 * 惠美丽-修改个人资料输入类
 * @author yangrong
 * date 2014-8-20
 * @version 1.0
 */
public class UpdatePersonInformationInput extends RootInput {
	
	@ZapcomApi(value = "头像",remark="头像",demo="http://....")
	private String avatar = "";
	
	@ZapcomApi(value = "昵称",remark="昵称唯一",demo="西瓜",require=1)
	private String nickname = "";
	
	@ZapcomApi(value = "性别",remark="性别",demo="男=4497465100010002   女=4497465100010003")
	private String sex = "";
	
	@ZapcomApi(value = "皮肤类型",remark="皮肤类型",demo="449746650001=混合皮肤  449746650002=干性皮肤  449746650003=中性皮肤  449746650004=油性皮肤 449746650005=敏感皮肤")
	private String skin_type = "";
	
	@ZapcomApi(value = "期待改善方向",remark="期待改善方向",demo="449746660001=美白 449746660002=祛痘  449746660003=祛斑 449746660004=祛皱 449746660005=细致毛孔 449746660006=抗敏感  449746660007=保湿")
	private String hopeful = "";
	
	@ZapcomApi(value = "收货地址信息",remark="收货地址信息",demo="北京市朝阳区高碑店小郊亭1376号润坤大厦十层   ")
	private BeautyAddress adress = new BeautyAddress();
	
	@ZapcomApi(value = "生日",remark="1995-02-10")
	private String birthday = "";

	@ZapcomApi(value="地区",remark="北京市")
	private String area_code = "";

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

	public void BeautyAddress(BeautyAddress adress) {
		this.adress = adress;
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

	public void setAdress(BeautyAddress adress) {
		this.adress = adress;
	}

	
}
