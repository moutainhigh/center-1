package com.cmall.groupcenter.third.api;

import com.cmall.groupcenter.service.GroupRebateService;
import com.cmall.groupcenter.third.model.GroupUpdateRebateScaleInput;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class GroupUpdateRebateScale extends RootApiForManage<RootResultWeb, GroupUpdateRebateScaleInput>{

	public RootResultWeb Process(GroupUpdateRebateScaleInput inputParam, MDataMap mRequestMap) {
		RootResultWeb rootResultWeb=new RootResultWeb();
		GroupRebateService groupRebateService=new GroupRebateService();
		rootResultWeb=groupRebateService.updateRebateScaleStatus(inputParam,getManageCode());
		return rootResultWeb;
	}

}
