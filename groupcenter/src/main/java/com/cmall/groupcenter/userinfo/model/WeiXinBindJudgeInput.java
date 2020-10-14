package com.cmall.groupcenter.userinfo.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 绑定参数
 * @author chenbin
 *
 */
public class WeiXinBindJudgeInput extends RootInput{
	
	@ZapcomApi(value = "流水号",demo = "dsfsdfdfdfsf3232", require = 1)
	String serialNumber="";

	@ZapcomApi(value = "openid",demo = "dsfsdfdfdfsf3232", require = 1)
	String openId="";
	
	@ZapcomApi(value = "unionid",demo = "dsfsdfdfdfsf3232", require = 0)
	String unionId="";
	
	@ZapcomApi(value = "性别",demo = "4497465100010002（对应男）4497465100010003（对应女）", require = 0)
	String gender="";
	
	@ZapcomApi(value = "城市",demo = "北京", require = 0)
	String location="";
	
	@ZapcomApi(value = "头像",demo = "http://", require = 0)
	String headerImageUrl="";
	
	@ZapcomApi(value = "昵称",demo = "bdce", require = 0)
	String nickName="";

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getHeaderImageUrl() {
		return headerImageUrl;
	}

	public void setHeaderImageUrl(String headerImageUrl) {
		this.headerImageUrl = headerImageUrl;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getUnionId() {
		return unionId;
	}

	public void setUnionId(String unionId) {
		this.unionId = unionId;
	}
	
}
