package com.cmall.groupcenter.account.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class MoneyWithdrawResult extends RootResultWeb {

	@ZapcomApi(value = "微账户余额", remark = "微账户余额", demo = "0.00")
	private String withdrawCurrent = "0.00";
	@ZapcomApi(value = "已提现金额", remark = "已提现金额", demo = "0.00")
	private String withdrawPayed = "0.00";
	@ZapcomApi(value = "总收益", remark = "总收益", demo = "0.00")
	private String withdrawTotal = "0.00";
	@ZapcomApi(value = "明细列表", remark = "明细列表")
	private List<MoneyWithdrawItem> items = new ArrayList<MoneyWithdrawItem>();

	public String getWithdrawCurrent() {
		return withdrawCurrent;
	}

	public void setWithdrawCurrent(String withdrawCurrent) {
		this.withdrawCurrent = withdrawCurrent;
	}

	public String getWithdrawPayed() {
		return withdrawPayed;
	}

	public void setWithdrawPayed(String withdrawPayed) {
		this.withdrawPayed = withdrawPayed;
	}

	public String getWithdrawTotal() {
		return withdrawTotal;
	}

	public void setWithdrawTotal(String withdrawTotal) {
		this.withdrawTotal = withdrawTotal;
	}

	public List<MoneyWithdrawItem> getItems() {
		return items;
	}

	public void setItems(List<MoneyWithdrawItem> items) {
		this.items = items;
	}

}
