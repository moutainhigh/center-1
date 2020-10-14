package com.cmall.groupcenter.third.api;

import com.cmall.groupcenter.service.GroupPayService;
import com.cmall.groupcenter.service.TraderOperationFilterService;
import com.cmall.groupcenter.third.model.GroupReconciliationInput;
import com.cmall.groupcenter.third.model.GroupReconciliationResult;
import com.srnpr.zapcom.basehelper.VersionHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.webmodel.MWebResult;

public class GroupReconciliation extends RootApiForManage<GroupReconciliationResult, GroupReconciliationInput>{

	public GroupReconciliationResult Process(
			GroupReconciliationInput inputParam, MDataMap mRequestMap) {

		GroupReconciliationResult groupReconciliationResult=new GroupReconciliationResult();


		//加入版本号控制
		//判断用户是否开通了支付功能
		TraderOperationFilterService traderOperationFilterService = new TraderOperationFilterService();
		MWebResult webResult = traderOperationFilterService.checkOperationPayByManageCode(getManageCode());
		groupReconciliationResult.inOtherResult(webResult);

		if (groupReconciliationResult.upFlagTrue()){
			GroupPayService groupPayService=new GroupPayService();
			groupReconciliationResult=groupPayService.groupReconciliation(inputParam, getManageCode());
		}
		return groupReconciliationResult;
	}

}
