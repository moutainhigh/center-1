package com.cmall.groupcenter.wallet.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.cmall.groupcenter.service.TraderOperationFilterService;
import com.cmall.groupcenter.wallet.model.WalletAccountCheckInput;
import com.cmall.groupcenter.wallet.model.WalletAccountCheckResult;
import com.cmall.groupcenter.wallet.model.WalletAccountCheckResultList;
import com.cmall.groupcenter.wallet.model.WithdrawRecordInput;
import com.cmall.groupcenter.wallet.model.WithdrawRecordResult;
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
 * 钱包对账接口
 * @author lijingxin
 *
 */

public class ApiWalletAccountCheck extends RootApiForManage<WalletAccountCheckResult, WalletAccountCheckInput> {

	public WalletAccountCheckResult Process(WalletAccountCheckInput inputParam,
			MDataMap mRequestMap) {

		// 记录请求
		HttpServletRequest request = WebSessionHelper.create().upHttpRequest();
		MDataMap logMap = new MDataMap();
		logMap.put("request_code", WebHelper.upCode("RWAL"));
		logMap.put("request_target", "walletAccountCheck");
		logMap.put("request_url", "http://" + request.getServerName() + ":"
				+ request.getServerPort() + request.getRequestURI());
		logMap.put("request_data",
				new JsonHelper<WalletAccountCheckInput>().GsonToJson(inputParam));
		logMap.put("request_time", FormatHelper.upDateTime());
		logMap.put("create_time", FormatHelper.upDateTime());
		DbUp.upTable("lc_wallet_api_log").dataInsert(logMap);

		WalletAccountCheckResult result = new WalletAccountCheckResult();



		//判断用户是否开通了钱包功能
		TraderOperationFilterService traderOperationFilterService = new TraderOperationFilterService();
		MWebResult webResult = traderOperationFilterService.checkOperationWalletByManageCode(getManageCode());
		result.inOtherResult(webResult);


		if (result.upFlagTrue()){
			WalletWithdrawService service =new WalletWithdrawService();

			String traderCode = service.getTraderCode(getManageCode());
			String startTime = inputParam.getStartDate();
			String endTime = inputParam.getEndDate();

			if(traderCode==null){
				result.setResultCode(918560003);
				result.setResultMessage(bInfo(918560003));
				return result;
			}

			if (result.upFlagTrue()) {

				MDataMap mWhereMap=new MDataMap();
				mWhereMap.put("trader_code", traderCode);
				mWhereMap.put("start_time",startTime);
				mWhereMap.put("end_time",endTime);

				if (startTime.compareTo(endTime) > 0) {
					result.inErrorMessage(918519006);
					return result;
				}

				List<MDataMap> traderDataMap = DbUp.upTable("gc_trader_wallet_change_log").queryAll(
						"", "", "trader_code=:trader_code and create_time >:start_time and create_time<:end_time", mWhereMap);

				for(int index=0; index<traderDataMap.size(); index++){

					WalletAccountCheckResultList resultList = new WalletAccountCheckResultList();

					resultList.setAccountCheckType(String.valueOf(traderDataMap.get(index).get("amount_change_type")));

					resultList.setMoney(String.valueOf(traderDataMap.get(index).get("amount_occurred")));

					resultList.setSerialNumber(String.valueOf(traderDataMap.get(index).get("log_code")));

					resultList.setTime(String.valueOf(traderDataMap.get(index).get("create_time")));

					result.getList().add(resultList);

				}
			}

			// 更新日志中返回结果
			logMap.put("response_data", new JsonHelper<WalletAccountCheckResult>()
					.GsonToJson(result));
			logMap.put("response_time", FormatHelper.upDateTime());
			DbUp.upTable("lc_wallet_api_log").dataUpdate(logMap,
					"response_data,response_time", "request_code");
		}

		return result;
	}
}
