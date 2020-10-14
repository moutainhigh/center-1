package com.cmall.groupcenter.recommend.api;

import com.alibaba.fastjson.JSON;
import com.cmall.groupcenter.recommend.model.ApiGetRecommendDetailInput;
import com.cmall.groupcenter.recommend.model.ApiGetRecommendDetailResult;
import com.cmall.groupcenter.service.NcPostService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 获取好物推荐详情
 * 
 * @author gaozx
 *
 */
public class ApiGetRecommendDetail
		extends
		RootApiForManage<ApiGetRecommendDetailResult, ApiGetRecommendDetailInput> {

	public ApiGetRecommendDetailResult Process(
			ApiGetRecommendDetailInput inputParam, MDataMap mRequestMap) {
		NcPostService ncPostService = new NcPostService();
		ApiGetRecommendDetailResult detailResult = ncPostService.getRecommendDetailService(inputParam, getManageCode());
		detailResult.setFavoriteState("");
		if(detailResult.getResultCode() != 1) {
			detailResult.setResultMessage(bInfo(detailResult.getResultCode()));
		}
		bLogInfo(0, "ApiGetRecommendDetail-pid:" + inputParam.getPid() + "\n"
				+ JSON.toJSON(detailResult));
		return detailResult;
	}

}
