package com.cmall.groupcenter.account.api;

import com.cmall.groupcenter.account.model.TemporaryConfigResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class ApiTemporaryConfig extends RootApiForManage<TemporaryConfigResult, RootInput>{

	public TemporaryConfigResult Process(RootInput inputParam, MDataMap mRequestMap) {
		TemporaryConfigResult temporaryConfigResult=new TemporaryConfigResult();
		temporaryConfigResult.setPeriod("30");
		return temporaryConfigResult;
	}

}
