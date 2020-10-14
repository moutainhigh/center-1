package com.cmall.productcenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 获取商家首页商品信息的输入参数
 * @author GaoYang
 *
 */
public class ApiGetShopHomepageProductInput extends RootInput{
	
	/**
	 * 卖家编号
	 */
	@ZapcomApi(value="卖家编号")
	private String selleCode="";
	
	/**
	 * 查询数量
	 */
	@ZapcomApi(value="查询数量")
	private int size = 8; 

	public String getSelleCode() {
		return selleCode;
	}

	public void setSelleCode(String selleCode) {
		this.selleCode = selleCode;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
}
