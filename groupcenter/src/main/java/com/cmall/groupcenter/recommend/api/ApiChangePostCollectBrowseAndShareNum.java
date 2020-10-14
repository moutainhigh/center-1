package com.cmall.groupcenter.recommend.api;

import com.alibaba.fastjson.JSON;
import com.cmall.groupcenter.recommend.model.ApiChangePostCollectBrowseAndShareNumInput;
import com.cmall.groupcenter.recommend.model.ApiChangePostCollectBrowseAndShareNumResult;
import com.cmall.groupcenter.txservice.TxNcPostChangeService;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 修改帖子的收藏数量、浏览数量、分享数量
 * 
 * @author gaozx
 *
 */
public class ApiChangePostCollectBrowseAndShareNum
		extends
		RootApiForManage<ApiChangePostCollectBrowseAndShareNumResult, ApiChangePostCollectBrowseAndShareNumInput> {

	public ApiChangePostCollectBrowseAndShareNumResult Process(
			ApiChangePostCollectBrowseAndShareNumInput inputParam, MDataMap mRequestMap) {
		
		ApiChangePostCollectBrowseAndShareNumResult result = new ApiChangePostCollectBrowseAndShareNumResult();

		String pid = inputParam.getPid();
		TxNcPostChangeService txNcPostChangeService = BeansHelper
				.upBean("bean_com_cmall_groupcenter_txservice_TxNcPostChangeService");
		result = txNcPostChangeService.changeNcPostSomeNum(inputParam, getManageCode());
		
		bLogInfo(0, "ApiGetRecommendDetail-pid:" + pid + "\n"
				+ JSON.toJSON(result));
		return result;
	}

}
