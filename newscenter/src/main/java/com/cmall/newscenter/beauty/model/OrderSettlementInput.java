package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;


/**
 *  订单预结算输入类
 * @author houwen	
 * date 2014-10-13
 * @version 1.0
 */
public class OrderSettlementInput extends RootInput{

	@ZapcomApi(value="商品信息",remark="商品信息",demo="",require=1)
	private List<PurchaseGoods> purchaseGoods = new ArrayList<PurchaseGoods>();

	@ZapcomApi(value="订单总价",remark="订单总价",demo="233",require=1)
	private String order_money = "";
	
	@ZapcomApi(value="试用商品类型",remark="如果试用商品提交订单，则传一个试用商品标识；试用商品：2",demo="2",require=0)
	private String type = "";
	
	public List<PurchaseGoods> getPurchaseGoods() {
		return purchaseGoods;
	}

	public void setPurchaseGoods(List<PurchaseGoods> purchaseGoods) {
		this.purchaseGoods = purchaseGoods;
	}

	public String getOrder_money() {
		return order_money;
	}

	public void setOrder_money(String order_money) {
		this.order_money = order_money;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
