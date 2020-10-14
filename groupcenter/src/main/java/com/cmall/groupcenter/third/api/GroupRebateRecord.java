package com.cmall.groupcenter.third.api;

import com.cmall.groupcenter.account.model.RebateRecordResult;
import com.cmall.groupcenter.account.model.WithdrawRecordInput;
import com.cmall.groupcenter.service.GroupAccountService;
import com.cmall.groupcenter.service.GroupService;
import com.cmall.groupcenter.third.model.GroupAccountInfoInput;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;
import org.apache.commons.lang.StringUtils;

/**
 * 获取返利记录明细
 * @author chenbin
 *
 */
public class GroupRebateRecord extends RootApiForManage<RebateRecordResult, GroupAccountInfoInput>{

	public RebateRecordResult Process(GroupAccountInfoInput inputParam,
			MDataMap mRequestMap) {

		RebateRecordResult rebateRecordResult=new RebateRecordResult();
		GroupAccountService groupAccountService=new GroupAccountService();
		String accountCode=groupAccountService.getAccountCodeByMemberCode(inputParam.getMemberCode());
		GroupService groupService=new GroupService();

//		如果ordercode没有值， 则查询该用户下所有的返利记录，否则查询该用户改订单下所有的返利记录
		if (StringUtils.isBlank(inputParam.getReckonOrderCode())){
			if(accountCode!=null){
				WithdrawRecordInput withdrawRecordInput=new WithdrawRecordInput();
				rebateRecordResult=groupService.showRebateRecord(accountCode, withdrawRecordInput);
			}
		}else {
			if(accountCode!=null){
				WithdrawRecordInput withdrawRecordInput=new WithdrawRecordInput();
				rebateRecordResult=groupService.showRebateRecord(accountCode, withdrawRecordInput,inputParam.getReckonOrderCode());
			}
		}



		return rebateRecordResult;
	}

}
