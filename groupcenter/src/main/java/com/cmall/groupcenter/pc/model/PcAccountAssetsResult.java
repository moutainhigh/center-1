package com.cmall.groupcenter.pc.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class PcAccountAssetsResult extends RootResultWeb{

	@ZapcomApi(value = "账户余额", remark = "账户余额")
	String accountWithdrawMoney="0.00";
	@ZapcomApi(value = "历史累计返利金额", remark = "历史累计返利金额")
	String totalRebateMoney = "0.00";
	@ZapcomApi(value = "历史累计提现金额", remark = "历史累计提现金额")
	String totalWithdrawMoney = "0.00";
	public String getAccountWithdrawMoney() {
		return accountWithdrawMoney;
	}
	public void setAccountWithdrawMoney(String accountWithdrawMoney) {
		this.accountWithdrawMoney = accountWithdrawMoney;
	}
	public String getTotalRebateMoney() {
		return totalRebateMoney;
	}
	public void setTotalRebateMoney(String totalRebateMoney) {
		this.totalRebateMoney = totalRebateMoney;
	}
	public String getTotalWithdrawMoney() {
		return totalWithdrawMoney;
	}
	public void setTotalWithdrawMoney(String totalWithdrawMoney) {
		this.totalWithdrawMoney = totalWithdrawMoney;
	}
	
}
