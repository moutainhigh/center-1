package com.cmall.groupcenter.groupapp.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/***
 * 微公社app账户实体类
 * @author fengl
 * date 2015-11-6
 * @version 2.0
 */
public class AccountModel {

	@ZapcomApi(value = "账户金额",remark = "账户金额",demo = "0.00")
	private String accountMoney;
	
	@ZapcomApi(value = "预计返利",remark = "预计返利",demo = "0.00")
	private String expectedRebateMoney;
	
	@ZapcomApi(value = "已返利",remark = "已返利",demo = "0.00")
	private String alreadyRebateMoney;

	public String getAccountMoney() {
		return accountMoney;
	}

	public void setAccountMoney(String accountMoney) {
		this.accountMoney = accountMoney;
	}

	public String getExpectedRebateMoney() {
		return expectedRebateMoney;
	}

	public void setExpectedRebateMoney(String expectedRebateMoney) {
		this.expectedRebateMoney = expectedRebateMoney;
	}

	public String getAlreadyRebateMoney() {
		return alreadyRebateMoney;
	}

	public void setAlreadyRebateMoney(String alreadyRebateMoney) {
		this.alreadyRebateMoney = alreadyRebateMoney;
	}

    
	
}
