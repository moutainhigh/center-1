package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class AccountInfoResult extends RootResultWeb {

	@ZapcomApi(value = "清分金额", remark = "清分金额", demo = "0.00")
	private String reckonMoney = "0.00";

	@ZapcomApi(value = "可提现金额", remark = "可提现金额", demo = "0.00")
	private String withdrawMoney = "0.00";

	@ZapcomApi(value = "总舍友人数", remark = "总舍友人数", demo = "0")
	private int allMember = 0;

	@ZapcomApi(value = "活跃社友", remark = "活跃社友", demo = "0")
	private int sumMember = 0;
	@ZapcomApi(value = "当前消费", remark = "当前消费", demo = "0")
	private String sumConsume = "0.00";
	@ZapcomApi(value = "级别名称", remark = "级别名称", demo = "长工")
	private String levelName = "";
	@ZapcomApi(value = "升级描述", remark = "升级描述", demo = "")
	private String updateRemark = "";

	@ZapcomApi(value = "是否可绑定上线", remark = "绑定描述", demo = "1是可绑定上线   0是不可绑定")
	private int flagRelation = 1;
	@ZapcomApi(value = "用户名", remark = "用户的登录名称", demo = "13522283810")
	private String loginName = "";

	public String getReckonMoney() {
		return reckonMoney;
	}

	public void setReckonMoney(String reckonMoney) {
		this.reckonMoney = reckonMoney;
	}

	public int getSumMember() {
		return sumMember;
	}

	public void setSumMember(int sumMember) {
		this.sumMember = sumMember;
	}

	public String getLevelName() {
		return levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

	public int getAllMember() {
		return allMember;
	}

	public void setAllMember(int allMember) {
		this.allMember = allMember;
	}

	public String getSumConsume() {
		return sumConsume;
	}

	public void setSumConsume(String sumConsume) {
		this.sumConsume = sumConsume;
	}

	public String getUpdateRemark() {
		return updateRemark;
	}

	public void setUpdateRemark(String updateRemark) {
		this.updateRemark = updateRemark;
	}

	public String getWithdrawMoney() {
		return withdrawMoney;
	}

	public void setWithdrawMoney(String withdrawMoney) {
		this.withdrawMoney = withdrawMoney;
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

}
