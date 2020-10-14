package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class GetRelationForImInfo {

	@ZapcomApi(value = "用户编号",demo = "MI15060110001")
	String memberCode="";
	
	@ZapcomApi(value = "用户昵称",demo = "昵称")
	String nickName="";
	
	@ZapcomApi(value = "头像",demo = "http://..")
	String headImage="";
	
	@ZapcomApi(value = "好友关系",demo = "一度好友",remark = "好友关系  0:自己 1：一度好友 2：二度好友 -1：推荐人 -2：二度推荐人")
	String relativeLevel="";
	
	@ZapcomApi(value = "备注",demo = "备注")
	String remark="";

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getHeadImage() {
		return headImage;
	}

	public void setHeadImage(String headImage) {
		this.headImage = headImage;
	}

	public String getRelativeLevel() {
		return relativeLevel;
	}

	public void setRelativeLevel(String relativeLevel) {
		this.relativeLevel = relativeLevel;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
}
