package com.cmall.groupcenter.account.api;

import java.util.Map;

import com.cmall.groupcenter.account.model.CreateRelationPrivateInput;
import com.cmall.groupcenter.support.GroupAccountSupport;
import com.cmall.systemcenter.enumer.EVerifyCodeTypeEnumer;
import com.cmall.systemcenter.support.VerifySupport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 创建上下级关系
 * @author fls@ichsy.com
 *
 */
public class ApiCreateRelationPrivate extends RootApi<RootResultWeb, CreateRelationPrivateInput>{

	public RootResultWeb Process(CreateRelationPrivateInput inputParam,
			MDataMap mRequestMap) {
		// TODO Auto-generated method stub
		RootResultWeb rootResultWeb=new RootResultWeb();
		String loginName=inputParam.getLoginName();
		String parentLoginName=inputParam.getParentLoginName();
		String createTime = inputParam.getCreateTime();
		String verify_code = inputParam.getVerify_code();
		// 判断验证码是否正确
		if (rootResultWeb.upFlagTrue()) {
			VerifySupport verifySupport = new VerifySupport();
			rootResultWeb.inOtherResult(verifySupport.checkVerifyCodeByType(
					EVerifyCodeTypeEnumer.Binding,
					loginName, verify_code)

			);
		}
		
		if (rootResultWeb.upFlagTrue()) {
			createRelation(rootResultWeb,loginName,parentLoginName,createTime);
		}
		
				
		return rootResultWeb;
	}

	
	public void createRelation(RootResultWeb rootResultWeb,String loginName,String parentLoginName,String createTime){


		
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
				rootResultWeb.inOtherResult(groupAccountSupport.createRelation(accountCode,parentAccountCode, "",createTime));
			 }
		}
	
	
	}
	
}
