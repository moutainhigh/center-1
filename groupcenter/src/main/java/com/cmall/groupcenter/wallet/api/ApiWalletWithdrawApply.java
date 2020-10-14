package com.cmall.groupcenter.wallet.api;

import com.cmall.groupcenter.service.TraderOperationFilterService;
import com.cmall.groupcenter.txservice.TxTraderWalletService;
import com.cmall.groupcenter.wallet.model.WithdrawApplyInput;
import com.cmall.groupcenter.wallet.model.WithdrawApplyResult;
import com.cmall.groupcenter.wallet.service.WalletWithdrawService;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.VersionHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.webmodel.MWebResult;

import javax.jws.WebResult;

/**
 * 微公社钱包提现申请
 * @author panwei
 *
 */
public class ApiWalletWithdrawApply extends RootApiForManage<WithdrawApplyResult, WithdrawApplyInput>{

	@Override
	public WithdrawApplyResult Process(WithdrawApplyInput inputParam,
			MDataMap mRequestMap) {

		WithdrawApplyResult result = new WithdrawApplyResult();

		//判断用户是否开通了钱包功能
		TraderOperationFilterService traderOperationFilterService = new TraderOperationFilterService();
		MWebResult webResult = traderOperationFilterService.checkOperationWalletByManageCode(getManageCode());
		result.inOtherResult(webResult);

		if (result.upFlagTrue()){
			TxTraderWalletService txTraderWalletService = BeansHelper
					.upBean("bean_com_cmall_groupcenter_txservice_TxTraderWalletService");
			if(VersionHelper.checkServerVersion("11.9.41.59")){
				result=txTraderWalletService.doWalletWithdrawApplyNew(getManageCode(),inputParam);
			}
			else{
				result=txTraderWalletService.doWalletWithdrawApply(getManageCode(),inputParam);
			}
			
		}
		return result;
	}

}
