package com.cmall.groupcenter.account.api;

import java.util.Map;

import com.cmall.groupcenter.account.model.CreateRelationInput;
import com.cmall.groupcenter.support.GroupAccountSupport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 创建上下级关系
 * @author chenbin@ichsy.com
 *
 */
public class ApiCreateRelation extends RootApi<RootResultWeb, CreateRelationInput>{

	public RootResultWeb Process(CreateRelationInput inputParam,
			MDataMap mRequestMap) {
		// TODO Auto-generated method stub
		RootResultWeb rootResultWeb=new RootResultWeb();
		String loginName=inputParam.getLoginName();
		String parentLoginName=inputParam.getParentLoginName();
		
		Map<?, ?> loginInfoMap = DbUp.upTable("mc_login_info").one("login_name",loginName);
		if(null == loginInfoMap){
			rootResultWeb.setResultCode(0);
			rootResultWeb.setResultMessage("该用户不存在");
		}
		if(rootResultWeb.upFlagTrue()){
			 String memberCode=(String) loginInfoMap.get("member_code");
			 if(null == DbUp.upTable("mc_login_info").one("login_name",parentLoginName)){
				rootResultWeb.setResultCode(0);
				rootResultWeb.inErrorMessage(918505170);
			 }
			 if(rootResultWeb.upFlagTrue()){
				 String parentMemberCode=DbUp.upTable("mc_login_info").one("login_name",parentLoginName).get("member_code");
				 String accountCode=DbUp.upTable("mc_member_info").one("member_code",memberCode).get("account_code");
				 String parentAccountCode=DbUp.upTable("mc_member_info").one("member_code",parentMemberCode).get("account_code");
				 
				 GroupAccountSupport groupAccountSupport=new GroupAccountSupport();
				rootResultWeb.inOtherResult(groupAccountSupport.createRelation(accountCode,parentAccountCode, "",inputParam.getCreateTime()));
			 }
		}
		return rootResultWeb;
	}

	
}
