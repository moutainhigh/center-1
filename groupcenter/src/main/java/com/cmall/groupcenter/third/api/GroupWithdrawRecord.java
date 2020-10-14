package com.cmall.groupcenter.third.api;

import com.cmall.groupcenter.account.model.WithdrawRecordInput;
import com.cmall.groupcenter.account.model.WithdrawRecordResult;
import com.cmall.groupcenter.service.GroupAccountService;
import com.cmall.groupcenter.service.GroupService;
import com.cmall.groupcenter.third.model.GroupAccountInfoInput;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 获取提款记录
 * @author chenbin
 *
 */
public class GroupWithdrawRecord extends RootApiForManage<WithdrawRecordResult, GroupAccountInfoInput>{

	public WithdrawRecordResult Process(GroupAccountInfoInput inputParam,
			MDataMap mRequestMap) {
		WithdrawRecordResult withdrawRecordResult=new WithdrawRecordResult();
		GroupAccountService groupAccountService=new GroupAccountService();
		String accountCode=groupAccountService.getAccountCodeByMemberCode(inputParam.getMemberCode());
		GroupService groupService=new GroupService();
		if(accountCode!=null){
			WithdrawRecordInput withdrawRecordInput=new WithdrawRecordInput();
			withdrawRecordResult=groupService.shoWithdrawRecord(accountCode, withdrawRecordInput);
		}
		return withdrawRecordResult;
	}

}
