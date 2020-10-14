package com.cmall.groupcenter.pc.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * PC版本好友信息
 * @author GaoYang
 *
 */
public class PcFridensInfo {
	
	@ZapcomApi(value = "头像", remark = "头像")
	private String headIconUrl="";
	
	@ZapcomApi(value = "好友等级", remark = "中农  富农 地主 土豪 特殊级别")
	private String fridenLevel = "";
	
	@ZapcomApi(value = "好友手机号", remark = "好友手机号")
	private String fridenMobile = "";
	
	@ZapcomApi(value = "好友关系级别", remark = "好友关系级别  0:自己 1：一度好友 2：二度好友 -1：推荐人 -2：二度推荐人")
	private String relationLevel="";
	
	@ZapcomApi(value = "活跃好友本月返利", remark = "活跃好友本月返利")
	private String activeMonthRebateMoney="0.00";
	
	@ZapcomApi(value = "最近下单时间", remark = "最近下单时间")
	private String orderTime="";
	
	@ZapcomApi(value = "好友累计返利", remark = "好友累计返利")
	private String totalRebateMoney="0.00";

	public String getHeadIconUrl() {
		return headIconUrl;
	}

	public void setHeadIconUrl(String headIconUrl) {
		this.headIconUrl = headIconUrl;
	}

	public String getFridenLevel() {
		return fridenLevel;
	}

	public void setFridenLevel(String fridenLevel) {
		this.fridenLevel = fridenLevel;
	}

	public String getFridenMobile() {
		return fridenMobile;
	}

	public void setFridenMobile(String fridenMobile) {
		this.fridenMobile = fridenMobile;
	}

	public String getRelationLevel() {
		return relationLevel;
	}

	public void setRelationLevel(String relationLevel) {
		this.relationLevel = relationLevel;
	}

	public String getActiveMonthRebateMoney() {
		return activeMonthRebateMoney;
	}

	public void setActiveMonthRebateMoney(String activeMonthRebateMoney) {
		this.activeMonthRebateMoney = activeMonthRebateMoney;
	}

	public String getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

	public String getTotalRebateMoney() {
		return totalRebateMoney;
	}

	public void setTotalRebateMoney(String totalRebateMoney) {
		this.totalRebateMoney = totalRebateMoney;
	}

	
}
