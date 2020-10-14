package com.cmall.groupcenter.account.api;

import com.cmall.groupcenter.account.model.GetConsumeDetailInput;
import com.cmall.groupcenter.account.model.GetConsumeDetailResult;
import com.cmall.groupcenter.service.GroupService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapweb.webapi.RootApiForToken;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 消费明细
 * @author chenbin
 *
 */
public class ApiGetConsumeDetail extends RootApiForToken<GetConsumeDetailResult, GetConsumeDetailInput>{

	public GetConsumeDetailResult Process(GetConsumeDetailInput inputParam, MDataMap mRequestMap) {
		GetConsumeDetailResult getConsumeDetailResult=new GetConsumeDetailResult();
	    GroupService groupService=new GroupService();
	    getConsumeDetailResult=groupService.getConsumeDetail(getUserCode(), inputParam);
		return getConsumeDetailResult;
	}

}
