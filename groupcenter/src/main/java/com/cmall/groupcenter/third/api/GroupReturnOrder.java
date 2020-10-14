package com.cmall.groupcenter.third.api;

import com.cmall.groupcenter.service.GroupRebateService;
import com.cmall.groupcenter.third.model.GroupReturnOrderInput;
import com.cmall.groupcenter.third.model.GroupReturnOrderResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;

public class GroupReturnOrder extends RootApiForManage<GroupReturnOrderResult, GroupReturnOrderInput>{

	public GroupReturnOrderResult Process(GroupReturnOrderInput inputParam,
			MDataMap mRequestMap) {
		GroupReturnOrderResult groupReturnOrderResult=new GroupReturnOrderResult();
		GroupRebateService groupRebateService=new GroupRebateService();
		groupReturnOrderResult=groupRebateService.groupReturnOrder(inputParam, getManageCode());
		return groupReturnOrderResult;
	}

}
