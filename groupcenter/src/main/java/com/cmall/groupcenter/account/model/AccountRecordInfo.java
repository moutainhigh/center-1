package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 账户明细信息
 * @author Administrator
 *
 */
public class AccountRecordInfo {
	@ZapcomApi(value = "头像", remark = "头像")
	private String headIconUrl="";
	
	@ZapcomApi(value = "昵称", remark = "昵称")
	private String nickName="";
	
	@ZapcomApi(value = "社交关联度数", remark = "社交关联度数")
	private String relationLevel = "";
	
	@ZapcomApi(value = "交易类型", remark = "交易类型")
	private String transactionType="";
	
	@ZapcomApi(value = "交易时间", remark = "交易时间")
	private String transactionTime="";
	
	@ZapcomApi(value = "交易金额", remark = "交易金额")
	private String transactionMoney="0.00";
	
	@ZapcomApi(value = "交易标签", remark = "交易标签")
	private String transactionlabel="";
	
	@ZapcomApi(value = "交易记录UID", remark = "交易记录UID")
	private String transactionUid="";

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

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getTransactionTime() {
		return transactionTime;
	}

	public void setTransactionTime(String transactionTime) {
		this.transactionTime = transactionTime;
	}

	public String getTransactionMoney() {
		return transactionMoney;
	}

	public void setTransactionMoney(String transactionMoney) {
		this.transactionMoney = transactionMoney;
	}

	public String getTransactionlabel() {
		return transactionlabel;
	}

	public void setTransactionlabel(String transactionlabel) {
		this.transactionlabel = transactionlabel;
	}

	public String getTransactionUid() {
		return transactionUid;
	}

	public void setTransactionUid(String transactionUid) {
		this.transactionUid = transactionUid;
	}
	
}
