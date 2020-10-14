package com.cmall.newscenter.beauty.api;

import com.cmall.newscenter.beauty.model.DeleteSkuToShopCarInput;
import com.cmall.newscenter.beauty.model.DeleteSkuToShopCarResult;
import com.cmall.newscenter.service.ShopCarService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 删除购物车商品 Api类
 * 
 * @author yangrong date 2014-9-20
 * @version 1.0
 */
public class DeleteSkuToShopCarApi extends RootApiForToken<DeleteSkuToShopCarResult, DeleteSkuToShopCarInput> {

	public DeleteSkuToShopCarResult Process(DeleteSkuToShopCarInput inputParam,MDataMap mRequestMap) {

		DeleteSkuToShopCarResult result = new DeleteSkuToShopCarResult();

		// 设置相关信息
		if (result.upFlagTrue()) {

			ShopCarService sc = new ShopCarService();

			Boolean flag = sc.deleteSkuForShopCart(getUserCode(),inputParam.getSku_code());

			if (!flag) {
				result.setResultCode(934205149);
				result.setResultMessage(bInfo(934205149));
			}
		}

		return result;
	}
}
