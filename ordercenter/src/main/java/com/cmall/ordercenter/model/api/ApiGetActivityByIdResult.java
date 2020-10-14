package com.cmall.ordercenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;

public class ApiGetActivityByIdResult extends RootResult{

	@ZapcomApi(value="活动信息")
	private MDataMap acMap = new MDataMap();

	public MDataMap getAcMap() {
		return acMap;
	}

	public void setAcMap(MDataMap acMap) {
		this.acMap = acMap;
	}

}
