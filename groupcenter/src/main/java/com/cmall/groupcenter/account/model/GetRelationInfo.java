package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class GetRelationInfo {
	
	@ZapcomApi(value = "成功标示", remark = "1:成功，2：失败")
	private String flag;
	
	@ZapcomApi(value = "手机号", remark = "手机号")
	private String mobile;
    
	@ZapcomApi(value = "用户账户中的返现总金额", remark = "用户账户中的返现总金额")
	private String totalAmount;
	
	@ZapcomApi(value = "用户的一度好友人数", remark = "用户的一度好友人数")
	private String firstFriend;
	
	@ZapcomApi(value = "用户的二度好友人数", remark = "用户的二度好友人数")
	private String secondFriend;
	
	@ZapcomApi(value = "当前返现级别", remark = "当前返现级别")
	private String userLevel;
	
	@ZapcomApi(value = "返现已领金额", remark = "返现已领金额")
	private String alreadyAmount;
	
	@ZapcomApi(value = "返现未领金额", remark = "返现未领金额")
	private String notreadyAmount;
	
	@ZapcomApi(value = "一度好友总返现金额", remark = "一度好友总返现金额")
	private String firstTotalAmount;
	
	@ZapcomApi(value = "二度好友总返现金额", remark = "二度好友总返现金额")
	private String secondTotalAmount;
	
	@ZapcomApi(value = "近30天内本人加一度好友加二度好友的总返现金额", remark = "近30天内本人加一度好友加二度好友的总返现金额")
	private String timeTotalAmount;
	
	@ZapcomApi(value = "本人提升到下一返现的条件", remark = "本人提升到下一返现的条件")
	private String levelBased;
	
	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getFirstFriend() {
		return firstFriend;
	}

	public void setFirstFriend(String firstFriend) {
		this.firstFriend = firstFriend;
	}

	public String getSecondFriend() {
		return secondFriend;
	}

	public void setSecondFriend(String secondFriend) {
		this.secondFriend = secondFriend;
	}

	public String getUserLevel() {
		return userLevel;
	}

	public void setUserLevel(String userLevel) {
		this.userLevel = userLevel;
	}

	public String getAlreadyAmount() {
		return alreadyAmount;
	}

	public void setAlreadyAmount(String alreadyAmount) {
		this.alreadyAmount = alreadyAmount;
	}

	public String getNotreadyAmount() {
		return notreadyAmount;
	}

	public void setNotreadyAmount(String notreadyAmount) {
		this.notreadyAmount = notreadyAmount;
	}

	public String getFirstTotalAmount() {
		return firstTotalAmount;
	}

	public void setFirstTotalAmount(String firstTotalAmount) {
		this.firstTotalAmount = firstTotalAmount;
	}

	public String getSecondTotalAmount() {
		return secondTotalAmount;
	}

	public void setSecondTotalAmount(String secondTotalAmount) {
		this.secondTotalAmount = secondTotalAmount;
	}

	public String getTimeTotalAmount() {
		return timeTotalAmount;
	}

	public void setTimeTotalAmount(String timeTotalAmount) {
		this.timeTotalAmount = timeTotalAmount;
	}

	public String getLevelBased() {
		return levelBased;
	}

	public void setLevelBased(String levelBased) {
		this.levelBased = levelBased;
	}
}
