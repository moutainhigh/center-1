package com.cmall.usercenter.service.api;

import java.util.ArrayList;
import java.util.List;

import com.cmall.usercenter.model.api.ApiGetSellerInfoListResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapdata.dbdo.DbUp;

/** 
* @ClassName: ApiGetSmallSellerInfo 
* @Description: 获取商家信息
* @author 张海生
* @date 2015-9-9 上午11:11:26 
*  
*/
public class ApiGetSellerInfoList extends RootApi<ApiGetSellerInfoListResult,RootInput> {

	public ApiGetSellerInfoListResult Process(RootInput api, MDataMap mRequestMap) {
		ApiGetSellerInfoListResult result = new ApiGetSellerInfoListResult();
		List<MDataMap> sellerList = DbUp.upTable("uc_sellerinfo").queryAll("small_seller_code,seller_name", "", "", new MDataMap());
		if(sellerList == null || sellerList.size() == 0){
			sellerList = new ArrayList<MDataMap>();
		}
		result.setSellerList(sellerList);
		return result;
	}
}