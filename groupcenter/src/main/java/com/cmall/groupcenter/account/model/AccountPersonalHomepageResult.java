package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 获取个人主页结果
 * @author GaoYang
 *
 */
public class AccountPersonalHomepageResult extends RootResultWeb{
	
	@ZapcomApi(value = "用户编号", remark = "用户编号")
	private String memberCode="";
	
	@ZapcomApi(value = "筛选类型",remark = "1:我的好友  2:本月活跃")
	private String selectionType = "";
	
	@ZapcomApi(value = "好友关系级别", remark = "好友关系级别  0:自己 1：一度好友 2：二度好友 -1：推荐人 -2：二度推荐人")
	private String relationLevel="";
	
	@ZapcomApi(value = "头像", remark = "头像")
	private String headIconUrl="";
	
	@ZapcomApi(value = "昵称", remark = "昵称")
	private String nickName="";
	
	@ZapcomApi(value = "好友等级", remark = "中农  富农 地主 土豪 特殊级别")
	private String fridenLevel = "";
	
	@ZapcomApi(value = "手机号", remark = "手机号")
	private String moblieNo = "";
	
	@ZapcomApi(value = "加入时间", remark = "加入时间")
	private String joinTime = "";
	
	@ZapcomApi(value = "本月消费", remark = "本月消费")
	private String monthConsumeMoney="0.00";
	
	@ZapcomApi(value = "Ta本月消费", remark = "Ta本月消费")
	private String taMonthConsumeMoney="0.00";
	
	@ZapcomApi(value = "Ta的一度好友本月消费", remark = "Ta的一度好友本月消费")
	private String taOneFridendMonthConsumeMoney="0.00";
	
	@ZapcomApi(value = "Ta的一度好友人数", remark = "Ta的一度好友人数")
	private String taOneFridendNumber="0";
	
	@ZapcomApi(value = "本月返利", remark = "本月返利")
	private String monthRebateMoney="0.00";
	
	@ZapcomApi(value = "本月预计返利", remark = "本月预计返利")
	private String monthExpectRebateMoney="0.00";
	
	@ZapcomApi(value = "总消费", remark = "总消费")
	private String totalConsumeMoney="0.00";
	
	@ZapcomApi(value = "总返利", remark = "总返利")
	private String totalRebateMoney="0.00";
	
	@ZapcomApi(value = "Ta的推荐人昵称", remark = "Ta的推荐人昵称")
	private String taRefereeNickName="";
	
	@ZapcomApi(value = "下一级别名称", remark = "下一级别名称", demo = "地主")
	private String nextLevelName = "";
	
	@ZapcomApi(value = "升级下一级别要求消费金额", remark = "升级下一级别要求消费金额", demo = "5000.00")
	private String nextLevelConsume = "";
	
	@ZapcomApi(value = "升级下一级别要求好友数", remark = "升级下一级别要求消费金额", demo = "10")
	private String nextLevelFriend = "";
	
	@ZapcomApi(value = "升级下一级别还需消费金额", remark = "升级下一级别还需消费金额", demo = "5000.00")
	private String nextLevelGapConsume = "0.00";
	
	@ZapcomApi(value = "升级下一级别要求还需好友数", remark = "升级下一级别还需好友数", demo = "10")
	private String nextLevelGapFriend = "0";
	
	@ZapcomApi(value = "升级下一级别消费比例", remark = "升级下一级别金额比例", demo = "50")
	private String nextLevelConsumePercent = "0";
	
	@ZapcomApi(value = "升级下一级别好友数比例", remark = "升级下一级别好友数比例", demo = "10")
	private String nextLevelFriendPercent = "0";
	
	@ZapcomApi(value = "当月活跃好友", remark = "当月活跃好友", demo = "0")
	private String activeFriend = "0";
	
	@ZapcomApi(value = "当月消费金额", remark = "当月消费金额", demo = "0")
	private String currentConsume = "0.00";
	
	@ZapcomApi(value = "一度好友人数", remark = "一度好友人数")
	private String oneFridendsNumber = "0";
	
	@ZapcomApi(value = "二度好友人数", remark = "二度好友人数")
	private String twoFridendsNumber = "0";
	
	@ZapcomApi(value = "一度好友本月消费", remark = "一度好友本月消费")
	private String oneFridendsMonthConsumeMoney="0.00";
	
	@ZapcomApi(value = "二度好友本月消费", remark = "二度好友本月消费")
	private String twoFridendsMonthConsumeMoney="0.00";

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	public String getSelectionType() {
		return selectionType;
	}

	public void setSelectionType(String selectionType) {
		this.selectionType = selectionType;
	}

	public String getRelationLevel() {
		return relationLevel;
	}

	public void setRelationLevel(String relationLevel) {
		this.relationLevel = relationLevel;
	}

	public String getHeadIconUrl() {
		return headIconUrl;
	}

	public void setHeadIconUrl(String headIconUrl) {
		this.headIconUrl = headIconUrl;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getFridenLevel() {
		return fridenLevel;
	}

	public void setFridenLevel(String fridenLevel) {
		this.fridenLevel = fridenLevel;
	}

	public String getMoblieNo() {
		return moblieNo;
	}

	public void setMoblieNo(String moblieNo) {
		this.moblieNo = moblieNo;
	}

	public String getJoinTime() {
		return joinTime;
	}

	public void setJoinTime(String joinTime) {
		this.joinTime = joinTime;
	}

	public String getMonthConsumeMoney() {
		return monthConsumeMoney;
	}

	public void setMonthConsumeMoney(String monthConsumeMoney) {
		this.monthConsumeMoney = monthConsumeMoney;
	}

	public String getTaMonthConsumeMoney() {
		return taMonthConsumeMoney;
	}

	public void setTaMonthConsumeMoney(String taMonthConsumeMoney) {
		this.taMonthConsumeMoney = taMonthConsumeMoney;
	}

	public String getTaOneFridendMonthConsumeMoney() {
		return taOneFridendMonthConsumeMoney;
	}

	public void setTaOneFridendMonthConsumeMoney(
			String taOneFridendMonthConsumeMoney) {
		this.taOneFridendMonthConsumeMoney = taOneFridendMonthConsumeMoney;
	}

	public String getTaOneFridendNumber() {
		return taOneFridendNumber;
	}

	public void setTaOneFridendNumber(String taOneFridendNumber) {
		this.taOneFridendNumber = taOneFridendNumber;
	}

	public String getMonthRebateMoney() {
		return monthRebateMoney;
	}

	public void setMonthRebateMoney(String monthRebateMoney) {
		this.monthRebateMoney = monthRebateMoney;
	}

	public String getMonthExpectRebateMoney() {
		return monthExpectRebateMoney;
	}

	public void setMonthExpectRebateMoney(String monthExpectRebateMoney) {
		this.monthExpectRebateMoney = monthExpectRebateMoney;
	}

	public String getTotalConsumeMoney() {
		return totalConsumeMoney;
	}

	public void setTotalConsumeMoney(String totalConsumeMoney) {
		this.totalConsumeMoney = totalConsumeMoney;
	}

	public String getTotalRebateMoney() {
		return totalRebateMoney;
	}

	public void setTotalRebateMoney(String totalRebateMoney) {
		this.totalRebateMoney = totalRebateMoney;
	}

	public String getTaRefereeNickName() {
		return taRefereeNickName;
	}

	public void setTaRefereeNickName(String taRefereeNickName) {
		this.taRefereeNickName = taRefereeNickName;
	}

	public String getNextLevelName() {
		return nextLevelName;
	}

	public void setNextLevelName(String nextLevelName) {
		this.nextLevelName = nextLevelName;
	}

	public String getNextLevelConsume() {
		return nextLevelConsume;
	}

	public void setNextLevelConsume(String nextLevelConsume) {
		this.nextLevelConsume = nextLevelConsume;
	}

	public String getNextLevelFriend() {
		return nextLevelFriend;
	}

	public void setNextLevelFriend(String nextLevelFriend) {
		this.nextLevelFriend = nextLevelFriend;
	}

	public String getActiveFriend() {
		return activeFriend;
	}

	public void setActiveFriend(String activeFriend) {
		this.activeFriend = activeFriend;
	}

	public String getCurrentConsume() {
		return currentConsume;
	}

	public void setCurrentConsume(String currentConsume) {
		this.currentConsume = currentConsume;
	}

	public String getNextLevelConsumePercent() {
		return nextLevelConsumePercent;
	}

	public void setNextLevelConsumePercent(String nextLevelConsumePercent) {
		this.nextLevelConsumePercent = nextLevelConsumePercent;
	}

	public String getNextLevelFriendPercent() {
		return nextLevelFriendPercent;
	}

	public void setNextLevelFriendPercent(String nextLevelFriendPercent) {
		this.nextLevelFriendPercent = nextLevelFriendPercent;
	}

	public String getNextLevelGapConsume() {
		return nextLevelGapConsume;
	}

	public void setNextLevelGapConsume(String nextLevelGapConsume) {
		this.nextLevelGapConsume = nextLevelGapConsume;
	}

	public String getNextLevelGapFriend() {
		return nextLevelGapFriend;
	}

	public void setNextLevelGapFriend(String nextLevelGapFriend) {
		this.nextLevelGapFriend = nextLevelGapFriend;
	}

	public String getOneFridendsNumber() {
		return oneFridendsNumber;
	}

	public void setOneFridendsNumber(String oneFridendsNumber) {
		this.oneFridendsNumber = oneFridendsNumber;
	}

	public String getTwoFridendsNumber() {
		return twoFridendsNumber;
	}

	public void setTwoFridendsNumber(String twoFridendsNumber) {
		this.twoFridendsNumber = twoFridendsNumber;
	}

	public String getOneFridendsMonthConsumeMoney() {
		return oneFridendsMonthConsumeMoney;
	}

	public void setOneFridendsMonthConsumeMoney(String oneFridendsMonthConsumeMoney) {
		this.oneFridendsMonthConsumeMoney = oneFridendsMonthConsumeMoney;
	}

	public String getTwoFridendsMonthConsumeMoney() {
		return twoFridendsMonthConsumeMoney;
	}

	public void setTwoFridendsMonthConsumeMoney(String twoFridendsMonthConsumeMoney) {
		this.twoFridendsMonthConsumeMoney = twoFridendsMonthConsumeMoney;
	}
	
}
