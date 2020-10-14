package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;


/**
 *  订单预结算 -》商品信息 输入类
 * @author houwen	
 * date 2014-10-13
 * @version 1.0
 */
public class PurchaseGoods extends RootInput{
	
	
	@ZapcomApi(value="商品ID",remark="商品ID",demo="33343",require=1)
	private String sku_code = "";
	
	@ZapcomApi(value="商品数量",remark="商品数量",demo="2",require=1)
	private String order_count = "";
	

	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}

	public String getOrder_count() {
		return order_count;
	}

	public void setOrder_count(String order_count) {
		this.order_count = order_count;
	}

}
