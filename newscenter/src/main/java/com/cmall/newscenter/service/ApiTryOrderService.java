package com.cmall.newscenter.service;

import com.cmall.newscenter.model.ApiTryOrderServiceResult;
import com.cmall.ordercenter.model.api.ApiTryOrderServiceInput;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapcom.topapi.RootResult;

/**
 * 试用商品处理逻辑
 * @author jl
 *
 */
public class ApiTryOrderService extends RootApi<ApiTryOrderServiceResult,ApiTryOrderServiceInput> {

	public ApiTryOrderServiceResult Process(ApiTryOrderServiceInput inputParam, MDataMap mRequestMap) {
		ApiTryOrderServiceResult result = new ApiTryOrderServiceResult();
		
		if(inputParam == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		}else{
			String buyerCode=inputParam.getBuyerCode();//买家编号
			String skuCode=inputParam.getSkuCode();//SKU编号
			String address_id=inputParam.getAddress_id();//地址编号
			int amount=inputParam.getAmount();//商品数量
			String appCode=inputParam.getAppCode();
					
			RootResult ret = new RootResult();
			//订单处理
			TxTryOrderService tryOrderService = BeansHelper.upBean("bean_com_cmall_newscenter_service_TxTryOrderService");
			tryOrderService.taddOrder(buyerCode, skuCode, address_id, amount, appCode, result,"");
			result.setResultCode(ret.getResultCode());
			result.setResultMessage(ret.getResultMessage());
		}
		return result;
	}
	
}
