package com.cmall.groupcenter.wallet.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class WithdrawApplyResult extends RootResultWeb{

	@ZapcomApi(value = "流水号",demo="GCWWI151109100002")
	private String withdrawcode="";
	
	@ZapcomApi(value = "提现金额",demo="10.00")
	private String withdrawMoney="";
	
	@ZapcomApi(value = "实际支付金额",demo="10.00")
	private String payMoney="";
	
	@ZapcomApi(value = "手续费",demo="10.00")
	private String feeMoney="";

	public String getWithdrawcode() {
		return withdrawcode;
	}

	public void setWithdrawcode(String withdrawcode) {
		this.withdrawcode = withdrawcode;
	}

	public String getWithdrawMoney() {
		return withdrawMoney;
	}

	public void setWithdrawMoney(String withdrawMoney) {
		this.withdrawMoney = withdrawMoney;
	}

	public String getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(String payMoney) {
		this.payMoney = payMoney;
	}

	public String getFeeMoney() {
		return feeMoney;
	}

	public void setFeeMoney(String feeMoney) {
		this.feeMoney = feeMoney;
	}

	
}
