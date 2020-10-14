package com.cmall.ordercenter.model;

/**
 * 店铺费用
 * @author huoqiangshou
 *
 */
public class StoreFreight{
	/**
	 * 店铺编号
	 */
	private String stroeCode;
	
	/**
	 * 运费
	 */
	private String freight;

	public String getStroeCode() {
		return stroeCode;
	}

	public void setStroeCode(String stroeCode) {
		this.stroeCode = stroeCode;
	}

	public String getFreight() {
		return freight;
	}

	public void setFreight(String freight) {
		this.freight = freight;
	}
	
}
