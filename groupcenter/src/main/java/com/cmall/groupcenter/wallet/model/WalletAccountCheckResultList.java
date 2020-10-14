package com.cmall.groupcenter.wallet.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class WalletAccountCheckResultList {
	@ZapcomApi(value = "对账类型", remark = "转账:4497476000020001; 退款:4497476000020002; 提现:4497476000020003; 提现失败:4497476000020004")
	private String accountCheckType = "";
	
	@ZapcomApi(value = "流水号", remark = "流水号")
	private String serialNumber = "";
	
	@ZapcomApi(value = "金额", remark = "金额")
	private String money = "";
	
	@ZapcomApi(value = "时间", remark = "时间")
	private String time = "";

	public String getAccountCheckType() {
		return accountCheckType;
	}

	public void setAccountCheckType(String accountCheckType) {
		this.accountCheckType = accountCheckType;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	
	
}
