package com.cmall.membercenter.model;

import java.math.BigInteger;

import com.cmall.systemcenter.model.AppPhoto;
import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class MemberInfo {

	@ZapcomApi(value = "用户编号")
	private String member_code = "";

	@ZapcomApi(value = "昵称")
	private String nickname = "";

	@ZapcomApi(value = "用户组")
	private BigInteger group = new BigInteger("0");

	@ZapcomApi(value = "性别")
	private BigInteger gender = new BigInteger("0");

	@ZapcomApi(value = "用户头像")
	private AppPhoto avatar = new AppPhoto();

	@ZapcomApi(value = "积分")
	private int score = 0;
	
	@ZapcomApi(value = "pc积分")
	private int points = 0;

	@ZapcomApi(value = "积分单位")
	private String score_unit = "";

	@ZapcomApi(value = "等级")
	private int level = 0;
	
	@ZapcomApi(value = "pc等级")
	private int vip_level = 0;

	@ZapcomApi(value = "等级名称")
	private String level_name = "";
	
	@ZapcomApi(value = "pc等级名称")
	private String vip_level_name = "";

	@ZapcomApi(value = "加入时间")
	private String create_time = "";
	
	@ZapcomApi(value = "用户电话",require=1,verify="base=mobile")
	private String mobile = "";
	
	@ZapcomApi(value = "用户邮箱")
	private String email = "";
	
	@ZapcomApi(value = "用户邮箱")
	private String birthday = "";

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

	public AppPhoto getAvatar() {
		return avatar;
	}

	public void setAvatar(AppPhoto avatar) {
		this.avatar = avatar;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public String getScore_unit() {
		return score_unit;
	}

	public void setScore_unit(String score_unit) {
		this.score_unit = score_unit;
	}


	public String getLevel_name() {
		return level_name;
	}

	public void setLevel_name(String level_name) {
		this.level_name = level_name;
	}

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

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * 获取  email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * 设置 
	 * @param email 
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * 获取  birthday
	 */
	public String getBirthday() {
		return birthday;
	}

	/**
	 * 设置 
	 * @param birthday 
	 */
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public int getVip_level() {
		return vip_level;
	}

	public void setVip_level(int vip_level) {
		this.vip_level = vip_level;
	}

	public String getVip_level_name() {
		return vip_level_name;
	}

	public void setVip_level_name(String vip_level_name) {
		this.vip_level_name = vip_level_name;
	}
	
	

	
	
}
