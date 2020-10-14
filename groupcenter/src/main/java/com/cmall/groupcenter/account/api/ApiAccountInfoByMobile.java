package com.cmall.groupcenter.account.api;

import com.cmall.groupcenter.account.model.AccountInfoByMobileInput;
import com.cmall.groupcenter.account.model.AccountInfoResultNew;
import com.cmall.groupcenter.baidupush.core.utility.StringUtility;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 微公社账户信息
 * 根据手机号码查询用户账号信息
 * @author dyc
 *
 */
public class ApiAccountInfoByMobile extends
		RootApiForManage<AccountInfoResultNew, AccountInfoByMobileInput> {

	public AccountInfoResultNew Process(AccountInfoByMobileInput inputParam, MDataMap mRequestMap) {
		AccountInfoResultNew accountInfoResultNew=new AccountInfoResultNew();

		MDataMap member = DbUp.upTable("mc_login_info").one("login_name",inputParam.getMobile());
		String memberCode = "";
		if(member!=null)
			memberCode = member.get("member_code");
		if(StringUtility.isNotNull(memberCode)){
			accountInfoResultNew.setLoginName(inputParam.getMobile());
			ApiAccountInfoNew api = new ApiAccountInfoNew();
			api.qryAccountInfo(accountInfoResultNew, memberCode);
		}
				
		return accountInfoResultNew;
	}

}
