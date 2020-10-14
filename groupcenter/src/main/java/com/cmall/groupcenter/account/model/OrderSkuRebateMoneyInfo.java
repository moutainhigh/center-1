package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 返利详情中的商品返利金额信息
 * @author GaoYang
 *
 */
public class OrderSkuRebateMoneyInfo {
	@ZapcomApi(value = "商品名称", remark = "SKU名称")
	private String skuName="";
	
	@ZapcomApi(value = "返利比例", remark = "返利比例")
	private String scaleReckon="0.0000";
	
	@ZapcomApi(value = "返利金额", remark = "返利金额")
	private String reckonMoney="0.00";

	public String getSkuName() {
		return skuName;
	}

	public void setSkuName(String skuName) {
		this.skuName = skuName;
	}

	public String getScaleReckon() {
		return scaleReckon;
	}

	public void setScaleReckon(String scaleReckon) {
		this.scaleReckon = scaleReckon;
	}

	public String getReckonMoney() {
		return reckonMoney;
	}

	public void setReckonMoney(String reckonMoney) {
		this.reckonMoney = reckonMoney;
	}

}
