package com.cmall.groupcenter.third.api;

import com.cmall.groupcenter.service.GroupAccountService;
import com.cmall.groupcenter.service.GroupService;
import com.cmall.groupcenter.third.model.GroupAccountInfoInput;
import com.cmall.groupcenter.third.model.GroupAccountInfoResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.webapi.RootApiForMember;

/**
 * 第三方获取账户信息
 * @author chenbin
 *
 */
public class GroupAccountInfoApi extends RootApiForManage<GroupAccountInfoResult, GroupAccountInfoInput>{

	public GroupAccountInfoResult Process(GroupAccountInfoInput inputParam,
			MDataMap mRequestMap) {
		GroupAccountInfoResult groupAccountInfoResult=new GroupAccountInfoResult();
		GroupAccountService groupAccountService=new GroupAccountService();
		String accountCode=groupAccountService.getAccountCodeByMemberCode(inputParam.getMemberCode());
		if(accountCode!=null){
			groupAccountInfoResult=groupAccountService.getAccountInfo(accountCode);
		}
		else{
			groupAccountInfoResult.inErrorMessage(918523013);
		}
		groupAccountInfoResult.setMemberCode(inputParam.getMemberCode());
		return groupAccountInfoResult;
	}

}
