package com.cmall.usercenter.service;

import java.util.UUID;

import com.cmall.ordercenter.common.DateUtil;
import com.cmall.usercenter.model.api.ApiMemberRegisterResult;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

public class UcMemberInfoService extends BaseClass{

	
	/**
	 * 注册会员是调用
	 * @param userId
	 * @param userCode
	 * @return
	 */
	public ApiMemberRegisterResult RegisterMember(String userId,String userCode){
		ApiMemberRegisterResult ret = new ApiMemberRegisterResult();
	
		UUID uuid = UUID.randomUUID();

		MDataMap insertDatamap = new MDataMap();

		insertDatamap.put("uid", uuid.toString().replace("-", ""));
		insertDatamap.put("user_code", userCode);
		insertDatamap.put("user_id", userId);
		
		DbUp.upTable("uc_memberinfo").dataInsert(insertDatamap);
		
		return ret;
	}
	
}
