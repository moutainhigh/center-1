package com.cmall.groupcenter.favorites.api;


import com.cmall.groupcenter.favorites.model.ApiCollectionsAddInput;
import com.cmall.groupcenter.favorites.model.ApiCollectionsAddResult;
import com.cmall.groupcenter.service.NcPostService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForToken;
/**
 * 判断用户对于某个帖子收藏状态
 * @author gaozx
 *
 */

public class ApiCollectionsStateOfUser extends RootApiForToken<ApiCollectionsAddResult, ApiCollectionsAddInput>{
	
	public ApiCollectionsAddResult Process(ApiCollectionsAddInput inputParam,
			MDataMap mRequestMap) {
		ApiCollectionsAddResult result = new ApiCollectionsAddResult();
		result.setFlag("4497472000020002");
		if(result.upFlagTrue()){
			NcPostService ncPostService = new NcPostService();
			MDataMap getFavoriteStatemap = new MDataMap("post_id", inputParam.getPost_id(), "member_code", getUserCode(), "app_code", super.getManageCode());
			String favoriteState = ncPostService.getFavoriteStateOfUser(getFavoriteStatemap);
			result.setFlag(favoriteState);
		}
		return result;
	}
}
