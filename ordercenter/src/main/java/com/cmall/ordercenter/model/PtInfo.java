package com.cmall.ordercenter.model;

/**
 * 商品信息
 * @author huoqiangshou
 *
 */
public class PtInfo{
	
	/**
	 * 商品代码
	 */
	private String productCode;
	
	/**
	 * 商品数量
	 */
	private int account;

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public int getAccount() {
		return account;
	}

	public void setAccount(int account) {
		this.account = account;
	}

	
}