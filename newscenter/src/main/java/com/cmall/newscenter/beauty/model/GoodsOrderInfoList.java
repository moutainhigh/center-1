package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 *  我的订单列表-》商品信息  输出类
 * @author houwen	
 * date 2014-10-10
 * @version 1.0
 */
public class GoodsOrderInfoList{
	
	@ZapcomApi(value = "sku编号", remark = "sku编号")
	private String sku_code = "";
	
	@ZapcomApi(value = "商品数量", remark = "商品数量")
	private int sku_num = 0;
	
	@ZapcomApi(value = "是否评价", remark = "是否评价:0:未评价；1：已评价")
	private String is_commented = "";

	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}

	public int getSku_num() {
		return sku_num;
	}

	public void setSku_num(int sku_num) {
		this.sku_num = sku_num;
	}

	public String getIs_commented() {
		return is_commented;
	}

	public void setIs_commented(String is_commented) {
		this.is_commented = is_commented;
	}
	
}
