package com.cmall.productcenter.service;

import com.srnpr.zapcom.baseclass.BaseClass;

/**
 * 商品价格修改异步
 */
public class ProductSkuPriceThread extends BaseClass implements Runnable{

	public void run() {
		new ProductSkuPriceService().updateSkupriceTimeScope();
	}
	
}
