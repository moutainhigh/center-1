package com.cmall.ordercenter.service;

import com.cmall.ordercenter.model.ApiGetExchangegoodsResult;
import com.cmall.ordercenter.model.ApiModExchangegoodsInput;
import com.cmall.ordercenter.model.ExchangegoodsModelChild;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;

/**
 * 
 * 项目名称：ordercenter 
 * 类名称：     ApiModExchangegoodsIface 
 * 类描述：     更新换货状态逻辑
 * 创建人：     gaoy  
 * 创建时间：2013年9月16日下午1:53:03
 * 修改人：     gaoy
 * 修改时间：2013年9月16日下午1:53:03
 * 修改备注：  
 * @version
 * 
 */
public class ApiModExchangegoodsIface extends RootApi<ApiGetExchangegoodsResult,ApiModExchangegoodsInput>{
	
	public ApiGetExchangegoodsResult Process(ApiModExchangegoodsInput apiExInput, MDataMap mRequestMap) {
		
		ApiGetExchangegoodsResult apiExResult = new ApiGetExchangegoodsResult();
		//传入的参数为空
		if(apiExInput == null)
		{
			apiExResult.setResultMessage(bInfo(939301012));
			apiExResult.setResultCode(939301012);
			
		//更新条件判断（换货单号非空）
		} else if(apiExInput.getExchangeNo() == null || "".equals(apiExInput.getExchangeNo())){
			apiExResult.setResultMessage(bInfo(939301013,"换货单号"));
			apiExResult.setResultCode(939301013);
		} else if(apiExInput.getStatus() == null || "".equals(apiExInput.getStatus())){
			apiExResult.setResultMessage(bInfo(939301013,"换货状态"));
			apiExResult.setResultCode(939301013);
		} else {
			ExchangegoodsService exService = new ExchangegoodsService();
			ExchangegoodsModelChild segm = new ExchangegoodsModelChild();
			segm.setExchangeNo(apiExInput.getExchangeNo());
			segm.setStatus(apiExInput.getStatus());
			//更新换货状态处理
			apiExResult = exService.updExchangegoods(segm);
		}
		
		return apiExResult;
	}
}
