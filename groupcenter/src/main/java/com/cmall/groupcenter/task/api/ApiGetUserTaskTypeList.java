package com.cmall.groupcenter.task.api;

import com.cmall.groupcenter.model.AccountTaskInput;
import com.cmall.groupcenter.model.AccountTaskListResult;
import com.cmall.groupcenter.service.TaskDetailService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

public class ApiGetUserTaskTypeList extends RootApiForToken<AccountTaskListResult,AccountTaskInput>{

	

	public AccountTaskListResult Process(AccountTaskInput inputParam,
			MDataMap mRequestMap) {
		String accountCode = DbUp.upTable("mc_member_info")
				.oneWhere("account_code", "", "", "member_code", getUserCode())
				.get("account_code");
		TaskDetailService taskService = new TaskDetailService();
		String mobile = getOauthInfo().getLoginName();
		//1：用户等级升级,2:用户是否有任务
		if("1".equals(inputParam.getFlag())){
			taskService.updateAccountTask(accountCode,mobile,getUserCode());
			return new AccountTaskListResult();
		}else if("2".equals(inputParam.getFlag())){
			AccountTaskListResult result = taskService.judgeIsTask(accountCode);
		
			return result;
		}
		AccountTaskListResult result = taskService.showAccountTaskDetail(accountCode,inputParam,getUserCode(),mobile);
		return result;
	}

}
