package com.cmall.ordercenter.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.ordercenter.model.ApiGetExchangegoodsInput;
import com.cmall.ordercenter.model.ApiGetExchangegoodsResult;
import com.cmall.ordercenter.model.ApiGetExchangegoodsResultChild;
import com.cmall.ordercenter.model.ExchangegoodsModelChild;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;

/**
 * 
 * 项目名称：ordercenter 
 * 类名称：     ApiGetExchangegoodsIface 
 * 类描述：     获取换货信息逻辑
 * 创建人：     gaoy  
 * 创建时间：2013年9月16日下午1:53:01
 * 修改人：     gaoy
 * 修改时间：2013年9月16日下午1:53:01
 * 修改备注：  
 * @version
 * 
 */
public class ApiGetExchangegoodsIface extends RootApi<ApiGetExchangegoodsResult,ApiGetExchangegoodsInput>{

	public ApiGetExchangegoodsResult Process(ApiGetExchangegoodsInput apiExInput, MDataMap mRequestMap) {
		
		ApiGetExchangegoodsResultChild apiExResult = new ApiGetExchangegoodsResultChild();
		//传入的参数为空
		if(apiExInput == null)
		{
			apiExResult.setResultMessage(bInfo(939301012));
			apiExResult.setResultCode(939301012);
		//查询条件判断（换货单号非空）
		} else if(StringUtils.isBlank(apiExInput.getBuyerCode())){
			apiExResult.setResultMessage(bInfo(939301013,"买家编号"));
			apiExResult.setResultCode(939301013);
		} else {
			ExchangegoodsService exService = new ExchangegoodsService();
			//查询处理
			List<ExchangegoodsModelChild> exchangeGoods = exService.searchExchangegoods(apiExInput.getBuyerCode());
			apiExResult.setExchangeGoods(exchangeGoods);
			//查询结果为空
			if(exchangeGoods.size() == 0){
				apiExResult.setResultMessage(bInfo(939301014, apiExInput.getBuyerCode()));
				apiExResult.setResultCode(939301014);
			}
		}
		
		return apiExResult;
	}
}
