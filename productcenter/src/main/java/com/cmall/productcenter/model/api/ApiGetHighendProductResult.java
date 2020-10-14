package com.cmall.productcenter.model.api;

import java.util.ArrayList;
import java.util.List;

import com.cmall.productcenter.model.HighendProductInfo;
import com.srnpr.zapcom.topapi.RootResult;

/**
 * 获取高端商品信息结果
 * @author GaoYang
 *
 */
public class ApiGetHighendProductResult extends RootResult{
	
	/**
	 * 高端商品总数量
	 */
	private int highendCount = 0;
	
	/**
	 * 高端商品详细信息
	 */
	private List<HighendProductInfo> productList = new ArrayList<HighendProductInfo>();

	public int getHighendCount() {
		return highendCount;
	}

	public void setHighendCount(int highendCount) {
		this.highendCount = highendCount;
	}

	public List<HighendProductInfo> getProductList() {
		return productList;
	}

	public void setProductList(List<HighendProductInfo> productList) {
		this.productList = productList;
	}
}
