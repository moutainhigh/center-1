package com.cmall.groupcenter.model.api;

import java.math.BigDecimal;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class WithdrawApiResult extends RootResultWeb{

	@ZapcomApi(value="提现结果",remark="返回提现结果",demo="1")
	private String result;
	
	@ZapcomApi(value="提现金额",remark="提现金额",demo="100")
	private BigDecimal withdrawMoney;
	
	@ZapcomApi(value="扣税金额",remark="扣税金额",demo="10")
	private BigDecimal taxMoney;
	
	@ZapcomApi(value="手续费",remark="手续费",demo="10")
	private BigDecimal feeMoney;
	
	@ZapcomApi(value="实际支付金额",remark="实际支付金额",demo="80")
	private BigDecimal realPayMoney;

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public BigDecimal getWithdrawMoney() {
		return withdrawMoney;
	}

	public void setWithdrawMoney(BigDecimal withdrawMoney) {
		this.withdrawMoney = withdrawMoney;
	}

	public BigDecimal getTaxMoney() {
		return taxMoney;
	}

	public void setTaxMoney(BigDecimal taxMoney) {
		this.taxMoney = taxMoney;
	}

	public BigDecimal getFeeMoney() {
		return feeMoney;
	}

	public void setFeeMoney(BigDecimal feeMoney) {
		this.feeMoney = feeMoney;
	}

	public BigDecimal getRealPayMoney() {
		return realPayMoney;
	}

	public void setRealPayMoney(BigDecimal realPayMoney) {
		this.realPayMoney = realPayMoney;
	}
	
	
}
