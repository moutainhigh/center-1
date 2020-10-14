package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 好友信息
 * @author GaoYang
 *
 */
public class AccountFridensInfo {
	@ZapcomApi(value = "用户编号", remark = "用户编号")
	private String memberCode="";
	
	@ZapcomApi(value = "是否微公社用户标记", remark = "是否微公社用户标记 1:是 0:不是")
	private String isGroup="";
	
	@ZapcomApi(value = "他账户编号", remark = "他账户编号")
	private String taAccountCode="";
	
	@ZapcomApi(value = "头像", remark = "头像")
	private String headIconUrl="";
	
	@ZapcomApi(value = "昵称", remark = "昵称")
	private String nickName="";
	
	@ZapcomApi(value = "本月消费", remark = "本月消费")
	private String monthConsumeMoney="0.00";
	
	@ZapcomApi(value = "总消费", remark = "总消费")
	private String totalConsumeMoney="0.00";
	
	@ZapcomApi(value = "好友关系级别", remark = "好友关系级别  0:自己 1：一度好友 2：二度好友 -1：推荐人 -2：二度推荐人")
	private String relationLevel="";
	
	@ZapcomApi(value = "好友等级", remark = "中农  富农 地主 土豪 特殊级别")
	private String fridenLevel = "";
	
	@ZapcomApi(value = "筛选类型", remark = "1:我的好友  2:本月活跃")
	private String selectionType = "";

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	public String getIsGroup() {
		return isGroup;
	}

	public void setIsGroup(String isGroup) {
		this.isGroup = isGroup;
	}

	public String getTaAccountCode() {
		return taAccountCode;
	}

	public void setTaAccountCode(String taAccountCode) {
		this.taAccountCode = taAccountCode;
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

	public String getMonthConsumeMoney() {
		return monthConsumeMoney;
	}

	public void setMonthConsumeMoney(String monthConsumeMoney) {
		this.monthConsumeMoney = monthConsumeMoney;
	}

	public String getTotalConsumeMoney() {
		return totalConsumeMoney;
	}

	public void setTotalConsumeMoney(String totalConsumeMoney) {
		this.totalConsumeMoney = totalConsumeMoney;
	}

	public String getRelationLevel() {
		return relationLevel;
	}

	public void setRelationLevel(String relationLevel) {
		this.relationLevel = relationLevel;
	}

	public String getFridenLevel() {
		return fridenLevel;
	}

	public void setFridenLevel(String fridenLevel) {
		this.fridenLevel = fridenLevel;
	}

	public String getSelectionType() {
		return selectionType;
	}

	public void setSelectionType(String selectionType) {
		this.selectionType = selectionType;
	}
	
}
