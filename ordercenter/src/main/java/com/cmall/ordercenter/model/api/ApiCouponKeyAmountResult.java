package com.cmall.ordercenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootResult;

public class ApiCouponKeyAmountResult extends RootResult {
	@ZapcomApi(value="特定优惠码限制使用总数量")
	private int amount;
	@ZapcomApi(value="已兑换优惠券的优惠码数量")
	private int exchanged;
	@ZapcomApi(value="未兑换优惠券的优惠码数量")
	private int remain;
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public int getExchanged() {
		return exchanged;
	}
	public void setExchanged(int exchanged) {
		this.exchanged = exchanged;
	}
	public int getRemain() {
		return remain;
	}
	public void setRemain(int remain) {
		this.remain = remain;
	}

	
}
