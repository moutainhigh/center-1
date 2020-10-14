package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 好友信息
 * @author fengls
 *
 */
public class RelationInfo {

	@ZapcomApi(value = "手机号")
	private String mobile ;
	@ZapcomApi(value = "个人返利")
	private String rebateMoney;
	@ZapcomApi(value = "级别")
	private String levelName ;
	@ZapcomApi(value = "最后活动时间")
	private String activeTime ;
	@ZapcomApi(value = "关系类型")
	private String relationType = ""; 
	@ZapcomApi(value = "个人头像url")
	private String headIconUrl; 
	@ZapcomApi(value = "昵称")
	private String nickName;
	
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
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getRebateMoney() {
		return rebateMoney;
	}
	public void setRebateMoney(String rebateMoney) {
		this.rebateMoney = rebateMoney;
	}
	public String getLevelName() {
		return levelName;
	}
	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}
	public String getActiveTime() {
		return activeTime;
	}
	public void setActiveTime(String activeTime) {
		this.activeTime = activeTime;
	}
	public String getRelationType() {
		return relationType;
	}
	public void setRelationType(String relationType) {
		this.relationType = relationType;
	}

	
	

	

}
