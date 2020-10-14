package com.cmall.newscenter.beauty.api;

import com.cmall.newscenter.beauty.model.ShoppingCarListInput;
import com.cmall.newscenter.beauty.model.ShoppingCarListResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 购物车列表Api
 * 
 * @author yangrong date: 2014-09-16
 * @version1.0
 */
public class ShoppingCarListApi extends
		RootApiForToken<ShoppingCarListResult, ShoppingCarListInput> {

	public ShoppingCarListResult Process(ShoppingCarListInput inputParam,
			MDataMap mRequestMap) {

		ShoppingCarListResult result = new ShoppingCarListResult();

		// 设置相关信息
		if (result.upFlagTrue()) {

		}
		return result;
	}
}
