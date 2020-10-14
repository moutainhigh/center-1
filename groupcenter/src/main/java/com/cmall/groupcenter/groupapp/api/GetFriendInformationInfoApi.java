package com.cmall.groupcenter.groupapp.api;

import com.cmall.groupcenter.groupapp.model.GetFriendInformationInfoInput;
import com.cmall.groupcenter.groupapp.model.GetFriendInformationInfoResult;
import com.cmall.groupcenter.groupapp.service.GetFriendInfoService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 好友数据
 * 
 * @author fengl 
 * 
 */
public class GetFriendInformationInfoApi extends RootApiForToken<GetFriendInformationInfoResult, GetFriendInformationInfoInput>{ 

	public GetFriendInformationInfoResult Process(GetFriendInformationInfoInput inputParam,
			MDataMap mRequestMap) {
		
		String appCode=getManageCode();
		String woMemberCode=getUserCode();
		String memberCode=inputParam.getMemberCode();
		GetFriendInformationInfoResult result=new GetFriendInformationInfoResult();
		GetFriendInfoService service=new GetFriendInfoService();
		result=service.GetFriendInfo(memberCode,appCode,woMemberCode);	
		
		return result;
	}
}
