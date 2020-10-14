package com.cmall.newscenter.beauty.api;

import com.cmall.newscenter.beauty.model.AddSkuToShopCarInput;
import com.cmall.newscenter.beauty.model.AddSkuToShopCarResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 加入购物车 API
 * 
 * @author yangrong date 2014-9-20
 * @version 1.0
 */
public class AddSkuToShopCarApi extends RootApiForToken<AddSkuToShopCarResult, AddSkuToShopCarInput> {

	public AddSkuToShopCarResult Process(AddSkuToShopCarInput inputParam,MDataMap mRequestMap) {

		AddSkuToShopCarResult result = new AddSkuToShopCarResult();

		// 设置相关信息
		if (result.upFlagTrue()) {

		}

		return result;
	}
}
