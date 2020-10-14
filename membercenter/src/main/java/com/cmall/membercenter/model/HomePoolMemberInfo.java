package com.cmall.membercenter.model;

import java.math.BigInteger;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class HomePoolMemberInfo {

	@ZapcomApi(value = "用户编号")
	private String member_code = "";

	@ZapcomApi(value = "昵称")
	private String nickname = "";

	@ZapcomApi(value = "用户组")
	private BigInteger group = new BigInteger("0");

	@ZapcomApi(value = "性别")
	private BigInteger gender = new BigInteger("0");

//	@ZapcomApi(value = "用户头像")
//	private AppPhoto avatar = new AppPhoto();
	
	@ZapcomApi(value = "用户头像")
	private String headPic;
	
	@ZapcomApi(value = "出生日期")
	private String birthday;
	
	@ZapcomApi(value = "邮箱")
	private String email;
	
	@ZapcomApi(value = "会员姓名")
	private String memberName;
	
	@ZapcomApi(value = "旧系统编号")
	private String oldCode;
	
	@ZapcomApi(value = "用户标记")
	private String memberSign;
	
	@ZapcomApi(value = "加入时间")
	private String create_time = "";
	
	@ZapcomApi(value = "用户电话",require=1,verify="base=mobile")
	private String mobile = "";
	
	@ZapcomApi(value = "真实姓名")
	private String realName = "";
	
	@ZapcomApi(value = "邮箱是否验证",remark="1是0否")
	private String emaiStatus = "";
	
	@ZapcomApi(value = "手机是否验证",remark="1是0否")
	private String mobileStatus = "";
	
	@ZapcomApi(value = "会员类型",remark="内部员工(4497469400050001),家有会员(4497469400050002)")
	private String vipType = "";
	
	@ZapcomApi(value = "会员等级",remark="普通会员(4497469400060001 ),一星(4497469400060002)...五星(4497469400060006)")
	private String vipLevel = "";
	
	@ZapcomApi(value = "积分")
	private String points = "";
	
	@ZapcomApi(value = "会员类型",remark="1:新注册会员，2:家有汇老会员")
	private String memberType = "";
	
	public String getMember_code() {
		return member_code;
	}

	public void setMember_code(String member_code) {
		this.member_code = member_code;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public BigInteger getGroup() {
		return group;
	}

	public void setGroup(BigInteger group) {
		this.group = group;
	}

	public BigInteger getGender() {
		return gender;
	}

	public void setGender(BigInteger gender) {
		this.gender = gender;
	}

//	public AppPhoto getAvatar() {
//		return avatar;
//	}
//
//	public void setAvatar(AppPhoto avatar) {
//		this.avatar = avatar;
//	}


	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getHeadPic() {
		return headPic;
	}

	public void setHeadPic(String headPic) {
		this.headPic = headPic;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getOldCode() {
		return oldCode;
	}

	public void setOldCode(String oldCode) {
		this.oldCode = oldCode;
	}

	public String getMemberSign() {
		return memberSign;
	}

	public void setMemberSign(String memberSign) {
		this.memberSign = memberSign;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getEmaiStatus() {
		return emaiStatus;
	}

	public void setEmaiStatus(String emaiStatus) {
		this.emaiStatus = emaiStatus;
	}

	public String getMobileStatus() {
		return mobileStatus;
	}

	public void setMobileStatus(String mobileStatus) {
		this.mobileStatus = mobileStatus;
	}

	public String getVipType() {
		return vipType;
	}

	public void setVipType(String vipType) {
		this.vipType = vipType;
	}

	public String getVipLevel() {
		return vipLevel;
	}

	public void setVipLevel(String vipLevel) {
		this.vipLevel = vipLevel;
	}

	public String getPoints() {
		return points;
	}

	public void setPoints(String points) {
		this.points = points;
	}

	public String getMemberType() {
		return memberType;
	}

	public void setMemberType(String memberType) {
		this.memberType = memberType;
	}

}
