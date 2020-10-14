package com.cmall.groupcenter.account.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 提现账户显示
 * 
 * @author srnpr
 * 
 */
public class WithdrawInfoResult extends RootResultWeb {

	@ZapcomApi(value = "可提现金额", remark = "可提现金额", demo = "0.00")
	private String withdrawMoney = "0.00";

	@ZapcomApi(value = "银行卡列表", remark = "银行卡列表", demo = "")
	private List<AccountBankInfo> bankInfoList = new ArrayList<AccountBankInfo>();

	@ZapcomApi(value = "是否可提现", remark = "该字段为1时才标记可提现", demo = "1")
	private int flagWithdraw = 0;

	public String getWithdrawMoney() {
		return withdrawMoney;
	}

	public void setWithdrawMoney(String withdrawMoney) {
		this.withdrawMoney = withdrawMoney;
	}

	public List<AccountBankInfo> getBankInfoList() {
		return bankInfoList;
	}

	public void setBankInfoList(List<AccountBankInfo> bankInfoList) {
		this.bankInfoList = bankInfoList;
	}

	public int getFlagWithdraw() {
		return flagWithdraw;
	}

	public void setFlagWithdraw(int flagWithdraw) {
		this.flagWithdraw = flagWithdraw;
	}

}
