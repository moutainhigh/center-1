package com.cmall.newscenter.api;

import com.cmall.newscenter.beauty.model.CityResult;
import com.cmall.newscenter.service.DistrictService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 获取所有城市API
 * date 2015-4-22
 * @version 1.0
 */
public class GetAllCityApi extends RootApiForManage<CityResult, RootInput> {

	public CityResult Process(RootInput inputParam,
			MDataMap mRequestMap) {
		
		return new DistrictService().getAllCitys();
	}

}

