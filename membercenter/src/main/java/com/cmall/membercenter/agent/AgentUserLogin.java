package com.cmall.membercenter.agent;


import com.cmall.membercenter.support.MemberLoginSupport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;
/**
 * 微信代理商登录
 * @author shiyz
 */
public class AgentUserLogin extends RootApiForManage<AgentLoginResult, AgentLoginInput> {


	public AgentLoginResult Process(AgentLoginInput inputParam,
			MDataMap mRequestMap) {
		
		AgentLoginResult userLoginResult = new AgentLoginResult();
		
            if(userLoginResult.upFlagTrue()){
			
            userLoginResult = new MemberLoginSupport()
			.agentStarLogin(inputParam,getManageCode());
			
            if(userLoginResult.upFlagTrue()){
            	userLoginResult.setUser(userLoginResult.getUser());
			
            	userLoginResult.setUser_token(userLoginResult.getUser_token());
             }
			
	}
            return userLoginResult;
}
	
}
