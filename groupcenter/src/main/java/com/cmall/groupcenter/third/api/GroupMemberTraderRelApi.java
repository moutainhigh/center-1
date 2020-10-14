package com.cmall.groupcenter.third.api;

import java.util.Arrays;
import java.util.List;

import com.cmall.groupcenter.service.GroupWopenCreateAppService;
import com.cmall.groupcenter.third.model.GroupMemberTraderRelInput;
import com.cmall.groupcenter.third.model.GroupMemberTraderRelResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 查询用户与店铺的关系
 * @author panwei
 *
 */
public class GroupMemberTraderRelApi extends RootApiForManage<GroupMemberTraderRelResult, GroupMemberTraderRelInput>{

	public GroupMemberTraderRelResult Process(
			GroupMemberTraderRelInput inputParam, MDataMap mRequestMap) {
		String[] mobile=inputParam.getMobile().split(",");
		List<String> mobileList=Arrays.asList(mobile);
		GroupWopenCreateAppService service=new GroupWopenCreateAppService();
		return service.getMemberTraderRel(mobileList,getManageCode());		
	}

}
