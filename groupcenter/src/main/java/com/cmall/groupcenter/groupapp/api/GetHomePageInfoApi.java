package com.cmall.groupcenter.groupapp.api;

import com.cmall.groupcenter.groupapp.model.GetHomePageInfoResult;
import com.cmall.groupcenter.groupapp.service.HomePageInfoService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapweb.webapi.RootApiForToken;

public class GetHomePageInfoApi extends RootApiForToken<GetHomePageInfoResult, RootInput>{

	@Override
	public GetHomePageInfoResult Process(RootInput inputParam,
			MDataMap mRequestMap) {
		HomePageInfoService service=new HomePageInfoService();
		return service.getHomePageInfoResult(getUserCode(),getManageCode());
	}

}
