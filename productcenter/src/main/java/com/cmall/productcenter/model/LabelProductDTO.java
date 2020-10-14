package com.cmall.productcenter.model;

import java.util.ArrayList;
import java.util.List;



/**
 *标签商品管理
 * 
 */
public class LabelProductDTO {
	
	private List<ProductBaseInfo> productInfoList = new ArrayList<ProductBaseInfo>();
	
	private String productCodes = "";
	
	public String getProductCodes() {
		return productCodes;
	}
	public void setProductCodes(String productCodes) {
		this.productCodes = productCodes;
	}
	public List<ProductBaseInfo> getProductInfoList() {
		return productInfoList;
	}
	public void setProductInfoList(List<ProductBaseInfo> productInfoList) {
		this.productInfoList = productInfoList;
	}
}
