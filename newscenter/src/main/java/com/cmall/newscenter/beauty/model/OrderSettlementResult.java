package com.cmall.newscenter.beauty.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 *  订单预结算输出类
 * @author houwen	
 * date 2014-10-13
 * @version 1.0
 */
public class OrderSettlementResult extends RootResultWeb{

	@ZapcomApi(value="运费",remark="运费")
	private String postage = "";

	@ZapcomApi(value="订单提示",remark="订单提示")
	private String prompt = "";
	
	@ZapcomApi(value="订单金额",remark="订单金额")
	private String order_money = "";

	public String getPostage() {
		return postage;
	}

	public void setPostage(String postage) {
		this.postage = postage;
	}

	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public String getOrder_money() {
		return order_money;
	}

	public void setOrder_money(String order_money) {
		this.order_money = order_money;
	}
	
}
