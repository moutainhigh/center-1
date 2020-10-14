package com.cmall.groupcenter.third.api;

import com.cmall.groupcenter.service.GroupRebateService;
import com.cmall.groupcenter.third.model.GroupRefundInput;
import com.cmall.groupcenter.third.model.GroupSetRebateScaleInput;
import com.cmall.groupcenter.third.model.GroupSetRebateScaleResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class GroupSetRebateScale extends RootApiForManage<GroupSetRebateScaleResult, GroupSetRebateScaleInput>{

	public GroupSetRebateScaleResult Process(GroupSetRebateScaleInput inputParam, MDataMap mRequestMap) {
		GroupSetRebateScaleResult groupSetRebateScaleResult=new GroupSetRebateScaleResult();
		GroupRebateService groupRebateService=new GroupRebateService();
		groupSetRebateScaleResult=groupRebateService.setRebateScale(inputParam, getManageCode());
		return groupSetRebateScaleResult;
	}

}
