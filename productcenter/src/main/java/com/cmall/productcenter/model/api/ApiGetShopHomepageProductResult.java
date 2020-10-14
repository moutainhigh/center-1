package com.cmall.productcenter.model.api;

import java.util.List;

import com.cmall.productcenter.model.PcProductInfoForI;
import com.srnpr.zapcom.topapi.RootResult;

/**
 * 获取商家首页商品信息结果
 * @author GaoYang
 *
 */
public class ApiGetShopHomepageProductResult extends RootResult{

	private List<PcProductInfoForI> productList = null;

	public List<PcProductInfoForI> getProductList() {
		return productList;
	}

	public void setProductList(List<PcProductInfoForI> productList) {
		this.productList = productList;
	}
}
