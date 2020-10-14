package com.cmall.groupcenter.account.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class MoneyReckonResult extends RootResultWeb {

	@ZapcomApi(value = "总计清分金额")
	private String reckonMoney = "";
	@ZapcomApi(value = "清分明细")
	private List<MoneyReckonItem> items = new ArrayList<MoneyReckonItem>();
	public String getReckonMoney() {
		return reckonMoney;
	}
	public void setReckonMoney(String reckonMoney) {
		this.reckonMoney = reckonMoney;
	}
	public List<MoneyReckonItem> getItems() {
		return items;
	}
	public void setItems(List<MoneyReckonItem> items) {
		this.items = items;
	}

}
