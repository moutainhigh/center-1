package com.cmall.ordercenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.topapi.RootResult;

/**
 *订单运费 按店铺分开
 * @author huoqiangshou
 *
 */
public class GetOrderFreightResult extends RootResult{
	
	/**
	 * 店铺运费
	 */
	private List<StoreFreight> list;
		
	/**
	 * 指定区域不可销售
	 */
	private List<String> noSaleList;
	
	
	
	public List<String> getNoSaleList() {
		return noSaleList;
	}



	public void setNoSaleList(List<String> noSaleList) {
		this.noSaleList = noSaleList;
	}



	public List<StoreFreight> getList() {
		return list;
	}



	public void setList(List<StoreFreight> list) {
		this.list = list;
	}

}
