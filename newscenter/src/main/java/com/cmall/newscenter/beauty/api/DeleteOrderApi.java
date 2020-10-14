package com.cmall.newscenter.beauty.api;

import com.cmall.newscenter.beauty.model.DeleteOrderInput;
import com.cmall.newscenter.beauty.model.DeleteOrderResult;
import com.cmall.ordercenter.service.OrderService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 删除订单Api
 * 
 * @author yangrong date: 2014-10-08
 * @version1.0
 */
public class DeleteOrderApi extends
		RootApiForToken<DeleteOrderResult, DeleteOrderInput> {

	public DeleteOrderResult Process(DeleteOrderInput inputParam,MDataMap mRequestMap) {

		DeleteOrderResult result = new DeleteOrderResult();

		// 设置相关信息
		if (result.upFlagTrue()) {

			OrderService orderService = new OrderService();

			int status = orderService.deleteOrderByBuyer(inputParam.getOrder_code(), getUserCode());

			if (status == 1) {
				result.setResultCode(934205147);
				result.setResultMessage(bInfo(934205147));
			} else if (status == 2) {
				result.setResultCode(934205148);
				result.setResultMessage(bInfo(934205148));
			} else if (status == 3) {
				result.setResultCode(934205149);
				result.setResultMessage(bInfo(934205149));
			} else if (status == 0) {
				result.setResultMessage(bInfo(934205150));
			}
		}

		return result;
	}
}
