package com.cmall.groupcenter.groupapp.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/***
 * 微公社app用户实体类
 * @author fengl
 * date 2015-11-6
 * @version 2.0
 */
public class Person {

	@ZapcomApi(value = "头像", remark = "头像")
	private String headerUrl = "";
	
	@ZapcomApi(value = "昵称", remark = "昵称")
	private String nickName = "";
	
	@ZapcomApi(value = "级别", remark = "中农  富农 地主 土豪 特殊级别")
	private String level = "";
	
	@ZapcomApi(value = "电话", remark = "电话")
	private String telephone = "";
	
	@ZapcomApi(value = "加入时间", remark = "加入时间")
	private String joinTime = "";
	
	@ZapcomApi(value = "性别", remark = "性别")
	private String gender = "";
	
	@ZapcomApi(value = "生日", remark = "生日")
	private String brithday ="";
	
	@ZapcomApi(value = "地区", remark = "地区")
	private String region ="";
	
	@ZapcomApi(value = "备注名", remark = "备注名")
	private String remarkName = "";
	
	@ZapcomApi(value = "等级关系", remark = "好友等级关系  0:自己 1：一度好友 2：二度好友 -1：推荐人 -2：二度推荐人")
	private String relativeLevel = "";
	
	@ZapcomApi(value = "用户编号", remark = "用户编号")
	private String memberCode = "";
	
	
	@ZapcomApi(value = "二维码url",remark ="/cgroup/web/grouppageSecond/showqrcode.html?mem_code=MI150205100001")
	private String qrCodeUrl= "";

	public String getHeaderUrl() {
		return headerUrl;
	}

	public void setHeaderUrl(String headerUrl) {
		this.headerUrl = headerUrl;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getJoinTime() {
		return joinTime;
	}

	public void setJoinTime(String joinTime) {
		this.joinTime = joinTime;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getBrithday() {
		return brithday;
	}

	public void setBrithday(String brithday) {
		this.brithday = brithday;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getRemarkName() {
		return remarkName;
	}

	public void setRemarkName(String remarkName) {
		this.remarkName = remarkName;
	}

	public String getRelativeLevel() {
		return relativeLevel;
	}

	public void setRelativeLevel(String relativeLevel) {
		this.relativeLevel = relativeLevel;
	}

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	public String getQrCodeUrl() {
		return qrCodeUrl;
	}

	public void setQrCodeUrl(String qrCodeUrl) {
		this.qrCodeUrl = qrCodeUrl;
	}
	
    
}
