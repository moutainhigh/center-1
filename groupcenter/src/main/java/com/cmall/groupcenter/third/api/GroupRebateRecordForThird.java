package com.cmall.groupcenter.third.api;

import com.cmall.groupcenter.service.GroupService;
import com.cmall.groupcenter.third.model.GroupRebateRecordInput;
import com.cmall.groupcenter.third.model.GroupRebateRecordResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 第三方返利对账
 * @author chenbin
 *
 */
public class GroupRebateRecordForThird extends RootApiForManage<GroupRebateRecordResult, GroupRebateRecordInput>{

	public GroupRebateRecordResult Process(GroupRebateRecordInput inputParam,
			MDataMap mRequestMap) {
		GroupRebateRecordResult groupRebateRecordResult=new GroupRebateRecordResult();
		GroupService groupService=new GroupService();
		groupRebateRecordResult=groupService.thirdRebateRecord(getManageCode(), inputParam);
		return groupRebateRecordResult;
	}

}
