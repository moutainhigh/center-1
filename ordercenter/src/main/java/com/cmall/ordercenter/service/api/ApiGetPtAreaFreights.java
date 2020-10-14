package com.cmall.ordercenter.service.api;

import com.cmall.ordercenter.model.GetPtAreaFreightInput;
import com.cmall.ordercenter.model.GetPtAreaFreightResult;
import com.cmall.ordercenter.service.GetPtAreaFreightsService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;

/**
 * 根据传入的商品编码，获得商品到各区域的运费
 * @author huoqiangshou
 *
 */
public class ApiGetPtAreaFreights extends RootApi<GetPtAreaFreightResult, GetPtAreaFreightInput> {

	public GetPtAreaFreightResult Process(GetPtAreaFreightInput inputParam,
			MDataMap mRequestMap) {  
		// TODO Auto-generated method stub
		GetPtAreaFreightsService service = new GetPtAreaFreightsService();
		return service.doGetPtAreaFreights(inputParam);
	}

}
