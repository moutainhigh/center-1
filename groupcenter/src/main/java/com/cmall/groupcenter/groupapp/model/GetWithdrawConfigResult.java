package com.cmall.groupcenter.groupapp.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class GetWithdrawConfigResult extends RootResultWeb {
	
	@ZapcomApi(value = "最低提现金额")
	private Double minimumWithdrawMoney;
	@ZapcomApi(value = "手续费用")
	private Double fee_money;//
	@ZapcomApi(value = "收取手续费最大提现金额")
	private Double maximumMoneyRange; //
	public Double getMinimumWithdrawMoney() {
		return minimumWithdrawMoney;
	}
	public Double getFee_money() {
		return fee_money;
	}
	public Double getMaximumMoneyRange() {
		return maximumMoneyRange;
	}
	public void setMinimumWithdrawMoney(Double minimumWithdrawMoney) {
		this.minimumWithdrawMoney = minimumWithdrawMoney;
	}
	public void setFee_money(Double fee_money) {
		this.fee_money = fee_money;
	}
	public void setMaximumMoneyRange(Double maximumMoneyRange) {
		this.maximumMoneyRange = maximumMoneyRange;
	}
	

    
	
	
}
