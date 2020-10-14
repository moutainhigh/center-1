package com.cmall.groupcenter.account.api;


import com.cmall.groupcenter.account.model.AccountInfoByMobileInput;
import com.cmall.groupcenter.account.model.AccountInfoResult;
import com.cmall.groupcenter.baidupush.core.utility.StringUtility;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 获取账户信息
 * 
 * @author fls
 *
 */
public class ApiAccountByMobile extends
    RootApiForManage<AccountInfoResult, AccountInfoByMobileInput> {

	public AccountInfoResult Process(AccountInfoByMobileInput inputParam, MDataMap mRequestMap) {
		AccountInfoResult accountInfoResult=new AccountInfoResult();
		MDataMap member = DbUp.upTable("mc_login_info").one("login_name",inputParam.getMobile());
		String memberCode = "";
		if(member!=null)
			memberCode = member.get("member_code");
		if(StringUtility.isNotNull(memberCode)){
			accountInfoResult.setLoginName(inputParam.getMobile());
			qryAccountInfo(accountInfoResult, memberCode);
		}
		return accountInfoResult;
	}
	
	
	public void qryAccountInfo(AccountInfoResult accountInfoResult,String memberCode){
		
		String sAccountCode = DbUp.upTable("mc_member_info")
				.oneWhere("account_code", "", "", "member_code", memberCode)
				.get("account_code");
		MDataMap mGroupAccountMap = DbUp.upTable("gc_group_account").oneWhere(
				"account_reckon_money,account_withdraw_money,account_level",
				"", "", "account_code", sAccountCode);
		// 如果有微公社账户信息
		if (mGroupAccountMap != null) {
			accountInfoResult.setReckonMoney(mGroupAccountMap
					.get("account_reckon_money"));
			accountInfoResult.setWithdrawMoney(mGroupAccountMap
					.get("account_withdraw_money"));
		}

	}

}
