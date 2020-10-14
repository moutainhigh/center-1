package com.cmall.groupcenter.account.api;

import com.cmall.groupcenter.account.model.CreateDefaultRelationInput;
import com.cmall.groupcenter.support.GroupAccountSupport;
import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 创建默认上下级关系
 * @author panwei
 *
 */
public class ApiCreateDefaultRelation extends RootApiForManage<RootResultWeb, CreateDefaultRelationInput>{

	public RootResultWeb Process(CreateDefaultRelationInput inputParam,
			MDataMap mRequestMap) {
		// TODO Auto-generated method stub
		RootResultWeb rootResultWeb=new RootResultWeb();
		String loginName=inputParam.getLoginName();
		
		MDataMap loginInfoMap = DbUp.upTable("mc_login_info").one("login_name",loginName);
		if(null == loginInfoMap){
			rootResultWeb.setResultCode(915805334);
			rootResultWeb.setResultMessage(bInfo(915805334));
		}
		if(rootResultWeb.upFlagTrue()){
			 String memberCode=(String) loginInfoMap.get("member_code");
			 //默认绑定商户为上线
			 MDataMap appMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",loginInfoMap.get("manage_code"));
			 if(null!=appMap){
				 MDataMap traderMap=DbUp.upTable("gc_trader_info").one("trader_code",appMap.get("trade_code"));
				 if(null!=traderMap){
					 String parentMemberCode=DbUp.upTable("mc_login_info").one("login_name",traderMap.get("login_account")).get("member_code");
					 String accountCode=DbUp.upTable("mc_member_info").one("member_code",memberCode).get("account_code");
					 String parentAccountCode=DbUp.upTable("mc_member_info").one("member_code",parentMemberCode).get("account_code");
					 
					 GroupAccountSupport groupAccountSupport=new GroupAccountSupport();
					rootResultWeb.inOtherResult(groupAccountSupport.createRelation(accountCode,parentAccountCode, "",DateUtil.getSysDateTimeString()));
				 }
			 }
		}
		return rootResultWeb;
	}

	
}
