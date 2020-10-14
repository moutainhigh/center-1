package com.cmall.groupcenter.behavior.model;

/**
 * 百分点推荐商品信息
 * @author pang_jhui
 *
 */
public class BfdRecProductInfo {
	
	/*商品信息*/
	private String productCode = "";

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		BfdRecProductInfo bfdRecProductInfo=(BfdRecProductInfo)obj;
		return this.productCode.equals(bfdRecProductInfo.getProductCode());
	}
	
	@Override
	public int hashCode() {
		return this.productCode.hashCode();
	}

	

}
