package com.cmall.groupcenter.wallet.api;

import javax.servlet.http.HttpServletRequest;

import com.cmall.groupcenter.service.TraderOperationFilterService;
import com.cmall.groupcenter.wallet.model.CheckBlanceInput;
import com.cmall.groupcenter.wallet.model.CheckBlanceResult;
import com.cmall.groupcenter.wallet.model.WalletAccountCheckResult;
import com.cmall.groupcenter.wallet.model.WithdrawRecordInput;
import com.cmall.groupcenter.wallet.service.WalletWithdrawService;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basehelper.VersionHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.helper.WebSessionHelper;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 钱包账户余额接口
 * @author lijingxin
 *
 */
public class ApiCheckBlance extends RootApiForManage<CheckBlanceResult, CheckBlanceInput>{

	public CheckBlanceResult Process(CheckBlanceInput inputParam,
			MDataMap mRequestMap) {
		
		// 记录请求
		HttpServletRequest request = WebSessionHelper.create().upHttpRequest();
		MDataMap logMap = new MDataMap();
		logMap.put("request_code", WebHelper.upCode("RWAL"));
		logMap.put("request_target", "walletCheckBlance");
		logMap.put("request_url", "http://" + request.getServerName() + ":"
				+ request.getServerPort() + request.getRequestURI());
		logMap.put("request_data",
				new JsonHelper<CheckBlanceInput>().GsonToJson(inputParam));
		logMap.put("request_time", FormatHelper.upDateTime());
		logMap.put("create_time", FormatHelper.upDateTime());
		DbUp.upTable("lc_wallet_api_log").dataInsert(logMap);
		
		CheckBlanceResult result = new CheckBlanceResult();

		//判断用户是否开通了钱包功能
		TraderOperationFilterService traderOperationFilterService = new TraderOperationFilterService();
		MWebResult webResult = traderOperationFilterService.checkOperationWalletByManageCode(getManageCode());
		result.inOtherResult(webResult);


		if (result.upFlagTrue()){

			WalletWithdrawService service =new WalletWithdrawService();

			String traderCode = service.getTraderCode(getManageCode());

			if(traderCode==null){
				result.setResultCode(918560003);
				result.setResultMessage(bInfo(918560003));
				return result;
			}

			String accountCode = service.getAccountCode(inputParam.getMemberCode(), getManageCode());
			if(accountCode == null){
				result.setResultCode(918560002);
				result.setResultMessage(bInfo(918560002));
				return result;
			}

			if(result.upFlagTrue()){

				MDataMap traderDataMap = service.findWallet(traderCode, accountCode);

				if(traderDataMap==null){
					result.setResultCode(918560002);
					result.setResultMessage(bInfo(918560002));
					return result;
				}

				result.setMemberCode(inputParam.getMemberCode());

				result.setBlanceAccount(traderDataMap.get("available_balance"));


			}

			// 更新日志中返回结果
			logMap.put("response_data", new JsonHelper<CheckBlanceResult>()
					.GsonToJson(result));
			logMap.put("response_time", FormatHelper.upDateTime());
			DbUp.upTable("lc_wallet_api_log").dataUpdate(logMap,
					"response_data,response_time", "request_code");
		}

		
		return result;
	}

}
