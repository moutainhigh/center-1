package com.cmall.groupcenter.payorder.api;

import com.cmall.groupcenter.model.api.WithdrawApiInput;
import com.cmall.groupcenter.model.api.WithdrawApiResult;
import com.cmall.groupcenter.txservice.TxWithdrawCashService;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 提现接口
 * @author chenbin@ichsy.com
 *
 */
public class WithdrawApiProcess extends
		RootApiForToken<WithdrawApiResult, WithdrawApiInput> {

	public WithdrawApiResult Process(WithdrawApiInput inputParam,
			MDataMap mRequestMap) {
		
		String sAccountCode = DbUp.upTable("mc_member_info")
				.oneWhere("account_code", "", "", "member_code", getUserCode())
				.get("account_code");
		
		
		// TODO Auto-generated method stub
		TxWithdrawCashService txWithdrawCashService = BeansHelper
				.upBean("bean_com_cmall_groupcenter_txservice_TxWithdrawCashService");
	    
		return txWithdrawCashService.doWithdrawCash(
				getUserCode(), sAccountCode,
				inputParam.getWithdrawAmount(),inputParam.getBankCode());
	}

}
