package com.cmall.groupcenter.account.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 
 * 新版本返利详情结果(2.1.4版)
 * @author GaoYang
 *
 */
public class RebateRecordDetailResult extends RootResultWeb{

	@ZapcomApi(value = "头像", remark = "头像")
	private String headIconUrl="";
	
	@ZapcomApi(value = "昵称", remark = "昵称")
	private String nickName="";
	
	@ZapcomApi(value = "社交关联度数", remark = "0:自己 1：一度好友 2:2度好友")
	private String relationLevel = "";
	
	@ZapcomApi(value = "当前用户清分级别", remark = "当前用户清分级别")
	private String accountLevel = "";
	
	@ZapcomApi(value = "返利金额", remark = "返利金额")
	private String rebateMoney = "0.0";
	
	@ZapcomApi(value = "返利金额列表", remark = "返利金额列表")
	List<OrderSkuRebateMoneyInfo> rebateSkuMoneyList=new ArrayList<OrderSkuRebateMoneyInfo>();
	
	@ZapcomApi(value = "订单金额", remark = "订单金额")
	private String orderMoney="0.00";
	
	@ZapcomApi(value = "当前返利状态", remark = "4497465200170001:未付款  4497465200170002:已付款  4497465200170003:已取消  4497465200170004:已返利 ")
	private String rebateStatus="0.00";
	
	@ZapcomApi(value = "下单APP", remark = "订单所属APP")
	private String orderApp="";
	
	@ZapcomApi(value = "订单交易节点列表", remark = "订单交易节点列表")
	List<OrderTransactionHistoryInfo> orderHistroyList=new ArrayList<OrderTransactionHistoryInfo>();

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

	public String getAccountLevel() {
		return accountLevel;
	}

	public void setAccountLevel(String accountLevel) {
		this.accountLevel = accountLevel;
	}

	public String getRebateMoney() {
		return rebateMoney;
	}

	public void setRebateMoney(String rebateMoney) {
		this.rebateMoney = rebateMoney;
	}

	public List<OrderSkuRebateMoneyInfo> getRebateSkuMoneyList() {
		return rebateSkuMoneyList;
	}

	public void setRebateSkuMoneyList(
			List<OrderSkuRebateMoneyInfo> rebateSkuMoneyList) {
		this.rebateSkuMoneyList = rebateSkuMoneyList;
	}

	public String getOrderMoney() {
		return orderMoney;
	}

	public void setOrderMoney(String orderMoney) {
		this.orderMoney = orderMoney;
	}

	public String getRebateStatus() {
		return rebateStatus;
	}

	public void setRebateStatus(String rebateStatus) {
		this.rebateStatus = rebateStatus;
	}

	public String getOrderApp() {
		return orderApp;
	}

	public void setOrderApp(String orderApp) {
		this.orderApp = orderApp;
	}

	public List<OrderTransactionHistoryInfo> getOrderHistroyList() {
		return orderHistroyList;
	}

	public void setOrderHistroyList(
			List<OrderTransactionHistoryInfo> orderHistroyList) {
		this.orderHistroyList = orderHistroyList;
	}


	
}
