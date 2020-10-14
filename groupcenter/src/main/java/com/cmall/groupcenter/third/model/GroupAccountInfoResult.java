package com.cmall.groupcenter.third.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class GroupAccountInfoResult extends RootResultWeb{

	@ZapcomApi(value = "账户余额",remark="可提现金额",demo = "1001.32", require = 1)
	String withdrawMoney="";
	
	@ZapcomApi(value = "预期返利",remark="预期返利",demo = "1001.32", require = 1)
    String rebateMoney="";
	
	@ZapcomApi(value = "用户编号",remark="用户编号",demo = "MI15040300001", require = 1)
    String memberCode="";
	
	@ZapcomApi(value = "用户等级",remark="用户等级,4497465200010002:中农,4497465200010003：富农，4497465200010004：地主，4497465200010005：土豪",demo = "4497465200010002", require = 1)
    String relationLevel="";
	
	@ZapcomApi(value = "账户余额是否可用",remark="1:可用，0：不可用,默认可用",demo = "1", require = 1)
    String flagEnable="1";
	
	public String getFlagEnable() {
		return flagEnable;
	}

	public void setFlagEnable(String flagEnable) {
		this.flagEnable = flagEnable;
	}

	public String getRelationLevel() {
		return relationLevel;
	}

	public void setRelationLevel(String relationLevel) {
		this.relationLevel = relationLevel;
	}

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	public String getRebateMoney() {
		return rebateMoney;
	}

	public void setRebateMoney(String rebateMoney) {
		this.rebateMoney = rebateMoney;
	}

	public String getWithdrawMoney() {
		return withdrawMoney;
	}

	public void setWithdrawMoney(String withdrawMoney) {
		this.withdrawMoney = withdrawMoney;
	}
}
