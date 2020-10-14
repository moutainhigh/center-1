package com.cmall.groupcenter.third.api;

import com.cmall.groupcenter.service.GroupWopenCreateAppService;
import com.cmall.groupcenter.third.model.GroupWopenCreateAppInput;
import com.cmall.groupcenter.third.model.GroupWopenCreateAppResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;

public class GroupWopenCreateApp extends RootApiForManage<GroupWopenCreateAppResult, GroupWopenCreateAppInput>{

	public GroupWopenCreateAppResult Process(GroupWopenCreateAppInput inputParam, MDataMap mRequestMap) {
		GroupWopenCreateAppResult result=new GroupWopenCreateAppResult();
		GroupWopenCreateAppService service=new GroupWopenCreateAppService();
		result=service.createApp(inputParam,getManageCode());		
		return result;

	}
}
