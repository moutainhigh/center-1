package com.cmall.groupcenter.wallet.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.account.model.AccountBankInfo;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 钱包(提现信息Result)
 * 
 * @author huangs
 * @date 2011-11-4
 *
 */
public class WithdrawInfoResult extends RootResultWeb {
	
	@ZapcomApi(value = "是否可提现", remark = "该字段为1时才标记可提现", demo = "1")
	private int flagWithdraw = 0;
	
	@ZapcomApi(value = "银行卡列表", remark = "银行卡列表", demo = "")
	private List<BankInfo> bankInfoList = new ArrayList<BankInfo>();
	
	@ZapcomApi(value = "账户余额", remark = "账户余额", demo = "0.00")
	private String withdrawMoney = "0.00";

	public int getFlagWithdraw() {
		return flagWithdraw;
	}

	public void setFlagWithdraw(int flagWithdraw) {
		this.flagWithdraw = flagWithdraw;
	}


	public List<BankInfo> getBankInfoList() {
		return bankInfoList;
	}

	public void setBankInfoList(List<BankInfo> bankInfoList) {
		this.bankInfoList = bankInfoList;
	}

	public String getWithdrawMoney() {
		return withdrawMoney;
	}

	public void setWithdrawMoney(String withdrawMoney) {
		this.withdrawMoney = withdrawMoney;
	}
	
	
}
