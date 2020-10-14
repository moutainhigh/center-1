package com.cmall.ordercenter.service.api;

import java.util.List;

import com.cmall.ordercenter.model.OcActivity;
import com.cmall.ordercenter.model.api.ApiGetSellerActivitysInput;
import com.cmall.ordercenter.model.api.ApiGetSellerActivitysResult;
import com.cmall.ordercenter.service.ActivityService;
import com.cmall.productcenter.model.PcProductInfoForI;

import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapweb.webfactory.UserFactory;

public class ApiGetActivityList extends RootApi<ApiGetSellerActivitysResult,ApiGetSellerActivitysInput> {

	public ApiGetSellerActivitysResult Process(ApiGetSellerActivitysInput api, MDataMap mRequestMap) {
		ApiGetSellerActivitysResult result = new ApiGetSellerActivitysResult();
		if(api == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		}else{
			
			//如果当前的SellerCode传的为空，则需要取得当前的数据
			if(api.getSellerCode().equals(""))
			{
				String manageCode = UserFactory.INSTANCE.create().getManageCode();
				api.setSellerCode(manageCode);
			}
			
			ActivityService s = new ActivityService();
			List<OcActivity> list= s.getActivityList(api.getSellerCode());
			result.setActivityList(list);
		}
		
		return result;
	}
}
