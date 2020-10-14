package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 返利记录
 * @author GaoYang
 *
 */
public class WithdrawRecordNewVersionInfo {
	
	@ZapcomApi(value = "头像", remark = "头像")
	private String headIconUrl="";
	
	@ZapcomApi(value = "昵称", remark = "昵称")
	private String nickName="";
	
	@ZapcomApi(value = "社交关联度数", remark = "0:自己 1：一度好友 2:2度好友")
	private String relationLevel = "";
	
	@ZapcomApi(value = "订单创建时间", remark = "订单创建时间")
	private String orderCreateTime="";
	
	@ZapcomApi(value = "订单状态", remark = "4497153900010001:等待付款,4497153900010002:等待发货,4497153900010003:等待收货,4497153900010004:已收货,4497153900010005:交易成功,4497153900010006:交易失败")
	private String orderStatus="";
	
	@ZapcomApi(value = "返利金额", remark = "返利金额")
	private String rebateMoney="0.00";
	
	@ZapcomApi(value = "返利状态", remark = "4497465200170001  未付款,4497465200170002  已付款,4497465200170003  已取消 ,4497465200170004  已返利")
	private String rebateStatus="";
	
	@ZapcomApi(value = "返利标签", remark = "预计xx月xx日返利,已返利，已取消")
	private String rebateLabel="";
	
	@ZapcomApi(value = "返利记录UID", remark = "返利记录UID")
	private String rebateUid="";

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

	public String getRelationLevel() {
		return relationLevel;
	}

	public void setRelationLevel(String relationLevel) {
		this.relationLevel = relationLevel;
	}

	public String getOrderCreateTime() {
		return orderCreateTime;
	}

	public void setOrderCreateTime(String orderCreateTime) {
		this.orderCreateTime = orderCreateTime;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getRebateMoney() {
		return rebateMoney;
	}

	public void setRebateMoney(String rebateMoney) {
		this.rebateMoney = rebateMoney;
	}

	public String getRebateStatus() {
		return rebateStatus;
	}

	public void setRebateStatus(String rebateStatus) {
		this.rebateStatus = rebateStatus;
	}

	public String getRebateLabel() {
		return rebateLabel;
	}

	public void setRebateLabel(String rebateLabel) {
		this.rebateLabel = rebateLabel;
	}

	public String getRebateUid() {
		return rebateUid;
	}

	public void setRebateUid(String rebateUid) {
		this.rebateUid = rebateUid;
	}
	
	
}
