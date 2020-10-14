package com.cmall.membercenter.api;

import com.cmall.membercenter.memberdo.MemberConst;
import com.cmall.membercenter.model.MLoginInput;
import com.cmall.membercenter.model.MemberReginsterInput;
import com.cmall.membercenter.txservice.TxMemberBase;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class MemberReginsterForHome extends RootApiForManage<RootResultWeb, MemberReginsterInput> {

	public RootResultWeb Process(MemberReginsterInput inputParam,
			MDataMap mRequestMap) {
		
		RootResultWeb rootResultWeb=new RootResultWeb();
		
		if(rootResultWeb.upFlagTrue())
		{
			if(DbUp.upTable("mc_login_info").count("login_name",inputParam.getMobile())>0)
			{
				rootResultWeb.inErrorMessage(934105104);
			}
			
		}
		
		
		if(rootResultWeb.upFlagTrue())
		{
			
			TxMemberBase txMemberBase=BeansHelper.upBean("bean_com_cmall_membercenter_txservice_TxMemberBase");
			
			MLoginInput input=new MLoginInput();
			
			input.setLoginGroup(MemberConst.LOGIN_GROUP_DEFAULT);
			input.setLoginName(inputParam.getMobile());
			
			input.setManageCode(getManageCode());
			
			
			
			rootResultWeb.inOtherResult(txMemberBase.doUserReginster(input));
			
			
		}
		
		
		if(rootResultWeb.upFlagTrue())
		{
			
	MDataMap mUpdateMap=new MDataMap();
	mUpdateMap.inAllValues("login_name",inputParam.getMobile(),"login_pass",inputParam.getPassword());
			
			DbUp.upTable("mc_login_info").dataUpdate(mUpdateMap, "login_pass", "login_name");
			
		}
		
		
		
		
		
		return rootResultWeb;
	}

}
