package com.cmall.ordercenter.service;

import org.apache.commons.lang.StringUtils;

import com.cmall.ordercenter.model.ApiAddExchangegoodsInput;
import com.cmall.ordercenter.model.ApiGetExchangegoodsResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;

/**
 * 
 * 项目名称：ordercenter 
 * 类名称：     ApiAddExchangegoodsIface 
 * 类描述：     增加换货信息逻辑
 * 创建人：     gaoy  
 * 创建时间：2013年9月16日下午1:52:59
 * 修改人：     gaoy
 * 修改时间：2013年9月16日下午1:52:59
 * 修改备注：  
 * @version
 *
 */
public class ApiAddExchangegoodsIface extends RootApi<ApiGetExchangegoodsResult,ApiAddExchangegoodsInput>{
	
	public ApiGetExchangegoodsResult Process(ApiAddExchangegoodsInput apiExInput, MDataMap mRequestMap) {
		
		ApiGetExchangegoodsResult apiExResult = new ApiGetExchangegoodsResult();
		//传入的参数为空
		if(apiExInput == null)
		{
			apiExResult.setResultMessage(bInfo(939301012));
			apiExResult.setResultCode(939301012);
		} else {
			ExchangegoodsService exService = new ExchangegoodsService();
			//订单编号不能为空
			if(StringUtils.isBlank(apiExInput.getOrderCode())){
				apiExResult.setResultCode(939301036);
				apiExResult.setResultMessage(bInfo(939301036));
			}else{
				//增加换货信息处理
				apiExResult = exService.addExchangegoods(apiExInput);
			}
		}
		return apiExResult;
	}
}
