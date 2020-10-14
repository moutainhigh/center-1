package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 微公社用户信息
 * 
 * @author chenbin
 * 
 */
public class AccountInfoResultNew extends RootResultWeb {

	@ZapcomApi(value = "预期返利金额", remark = "预期返利金额", demo = "0.00")
	private String expectRebateMoney = "0.00";

	@ZapcomApi(value = "累计返利金额", remark = "累计返利金额", demo = "0.00")
	private String totalRebateMoney = "0.00";

	@ZapcomApi(value = "当前月份", remark = "当前月份", demo = "11月")
	private String currentMonth = "";

	@ZapcomApi(value = "个人级别名称", remark = "个人级别名称", demo = "长工")
	private String levelName = "";

	@ZapcomApi(value = "下一级别名称", remark = "下一级别名称", demo = "地主")
	private String nextLevelName = "";

	@ZapcomApi(value = "升级下一级别要求消费金额", remark = "升级下一级别要求消费金额", demo = "5000.00")
	private String nextLevelConsume = "";

	@ZapcomApi(value = "升级下一级别要求好友数", remark = "升级下一级别要求消费金额", demo = "10")
	private String nextLevelFriend = "";

	@ZapcomApi(value = "升级下一级别还需消费金额", remark = "升级下一级别还需消费金额", demo = "5000.00")
	private String nextLevelGapConsume = "";

	@ZapcomApi(value = "升级下一级别要求还需好友数", remark = "升级下一级别还需好友数", demo = "10")
	private int nextLevelGapFriend = 0;

	@ZapcomApi(value = "升级下一级别消费比例", remark = "升级下一级别金额比例", demo = "50")
	private String nextLevelConsumePercent = "";

	@ZapcomApi(value = "升级下一级别好友数比例", remark = "升级下一级别好友数比例", demo = "10")
	private String nextLevelFriendPercent = "";

	@ZapcomApi(value = "账户金额", remark = "账户金额", demo = "0.00")
	private String withdrawMoney = "0.00";

	@ZapcomApi(value = "总好友数", remark = "总好友数", demo = "0")
	private int allFriend = 0;

	@ZapcomApi(value = "当月活跃好友", remark = "当月活跃好友", demo = "0")
	private int activeFriend = 0;

	@ZapcomApi(value = "当月消费金额", remark = "当月消费金额", demo = "0")
	private String currentConsume = "0.00";

	@ZapcomApi(value = "升级描述", remark = "升级描述", demo = "")
	private String updateRemark = "";

	@ZapcomApi(value = "是否可绑定上线", remark = "绑定描述,1是可绑定上线   0是不可绑定", demo = "1是可绑定上线   0是不可绑定")
	private int flagRelation = 1;

	@ZapcomApi(value = "用户名", remark = "用户的登录名称", demo = "13522283810")
	private String loginName = "";

	@ZapcomApi(value = "未读消息数", remark = "未读消息数", demo = "5")
	private int unreadCount = 0;

	@ZapcomApi(value = "比例", remark = "比例", demo = "0.05")
	private String scaleReckon = "";

	@ZapcomApi(value = "个人头像url")
	private String headIconUrl = "";

	@ZapcomApi(value = "昵称")
	private String nickName;

	@ZapcomApi(value = "上次登陆时间", remark = "上次登录时间", demo = "2015.04.20 15:30:40")
	private String lastLoginTime = "";

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getHeadIconUrl() {
		return headIconUrl;
	}

	public void setHeadIconUrl(String headIconUrl) {
		this.headIconUrl = headIconUrl;
	}

	public String getScaleReckon() {
		return scaleReckon;
	}

	public void setScaleReckon(String scaleReckon) {
		this.scaleReckon = scaleReckon;
	}

	public int getUnreadCount() {
		return unreadCount;
	}

	public void setUnreadCount(int unreadCount) {
		this.unreadCount = unreadCount;
	}

	public String getExpectRebateMoney() {
		return expectRebateMoney;
	}

	public void setExpectRebateMoney(String expectRebateMoney) {
		this.expectRebateMoney = expectRebateMoney;
	}

	public String getTotalRebateMoney() {
		return totalRebateMoney;
	}

	public void setTotalRebateMoney(String totalRebateMoney) {
		this.totalRebateMoney = totalRebateMoney;
	}

	public String getCurrentMonth() {
		return currentMonth;
	}

	public void setCurrentMonth(String currentMonth) {
		this.currentMonth = currentMonth;
	}

	public String getLevelName() {
		return levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
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

	public String getNextLevelGapConsume() {
		return nextLevelGapConsume;
	}

	public void setNextLevelGapConsume(String nextLevelGapConsume) {
		this.nextLevelGapConsume = nextLevelGapConsume;
	}

	public int getNextLevelGapFriend() {
		return nextLevelGapFriend;
	}

	public void setNextLevelGapFriend(int nextLevelGapFriend) {
		this.nextLevelGapFriend = nextLevelGapFriend;
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

	public String getWithdrawMoney() {
		return withdrawMoney;
	}

	public void setWithdrawMoney(String withdrawMoney) {
		this.withdrawMoney = withdrawMoney;
	}

	public int getAllFriend() {
		return allFriend;
	}

	public void setAllFriend(int allFriend) {
		this.allFriend = allFriend;
	}

	public int getActiveFriend() {
		return activeFriend;
	}

	public void setActiveFriend(int activeFriend) {
		this.activeFriend = activeFriend;
	}

	public String getCurrentConsume() {
		return currentConsume;
	}

	public void setCurrentConsume(String currentConsume) {
		this.currentConsume = currentConsume;
	}

	public String getUpdateRemark() {
		return updateRemark;
	}

	public void setUpdateRemark(String updateRemark) {
		this.updateRemark = updateRemark;
	}

	public int getFlagRelation() {
		return flagRelation;
	}

	public void setFlagRelation(int flagRelation) {
		this.flagRelation = flagRelation;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(String lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	
	

}
