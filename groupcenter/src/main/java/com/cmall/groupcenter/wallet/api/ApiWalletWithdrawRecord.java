package com.cmall.groupcenter.wallet.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.cmall.groupcenter.service.TraderOperationFilterService;
import com.cmall.groupcenter.wallet.model.WithdrawRecord;
import com.cmall.groupcenter.wallet.model.WithdrawRecordInput;
import com.cmall.groupcenter.wallet.model.WithdrawRecordResult;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basehelper.VersionHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.helper.WebSessionHelper;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.webmodel.MWebResult;

public class ApiWalletWithdrawRecord extends
		RootApiForManage<WithdrawRecordResult, WithdrawRecordInput> {

	@Override
	public WithdrawRecordResult Process(WithdrawRecordInput inputParam,
			MDataMap mRequestMap) {

		// 记录请求
		HttpServletRequest request = WebSessionHelper.create().upHttpRequest();
		MDataMap logMap = new MDataMap();
		logMap.put("request_code", WebHelper.upCode("RWAL"));
		logMap.put("request_target", "walletWithdrawRecord");
		logMap.put("request_url", "http://" + request.getServerName() + ":"
				+ request.getServerPort() + request.getRequestURI());
		logMap.put("request_data",
				new JsonHelper<WithdrawRecordInput>().GsonToJson(inputParam));
		logMap.put("request_time", FormatHelper.upDateTime());
		logMap.put("create_time", FormatHelper.upDateTime());
		DbUp.upTable("lc_wallet_api_log").dataInsert(logMap);

		WithdrawRecordResult withdrawRecordResult = new WithdrawRecordResult();

		//判断用户是否开通了钱包功能
		TraderOperationFilterService traderOperationFilterService = new TraderOperationFilterService();
		MWebResult webResult = traderOperationFilterService.checkOperationWalletByManageCode(getManageCode());
		withdrawRecordResult.inOtherResult(webResult);

		if (withdrawRecordResult.upFlagTrue()){
		/*WalletWithdrawService service = new WalletWithdrawService();
		String traderCode = service.getTraderCode(getManageCode());
		String memberCode = inputParam.getMemberCode();
		String manageCode = getManageCode();
		if (traderCode == null) {
			withdrawRecordResult.setResultCode(918560003);
			withdrawRecordResult.setResultMessage(bInfo(918560003));
			return withdrawRecordResult;
		}
		String accountCode = service.getAccountCode(memberCode, manageCode);
		if (accountCode == null) {
			withdrawRecordResult.setResultCode(918560002);
			withdrawRecordResult.setResultMessage(bInfo(918560002));
			return withdrawRecordResult;
		}

		MDataMap map = new MDataMap();
		map.put("trader_code", traderCode);
		map.put("account_code", accountCode);
		List<MDataMap> withdrawInfoList = DbUp.upTable(
				"gc_wallet_withdraw_info")
				.queryAll("", "-create_time", "", map);*/
			List<MDataMap> withdrawInfoList=DbUp.upTable("gc_wallet_withdraw_info")
					.queryIn(
							"",
							"",
							"",
							new MDataMap(),
							0,
							0,
							"withdraw_code",
							inputParam.getWithdrawCode());
			if (withdrawInfoList.size() != 0) {
				for (MDataMap withdrawInfo : withdrawInfoList) {
					WithdrawRecord withdrawRecord = new WithdrawRecord();
					String withdraw_status=withdrawInfo.get("withdraw_status");
					String withdrawStatusCode="";
					if("4497476000010001".equals(withdraw_status) || "4497476000010002".equals(withdraw_status) || "4497476000010003".equals(withdraw_status) || "4497476000010004".equals(withdraw_status)){
						withdraw_status="提现中";
						withdrawStatusCode="4497476000010001";
					}
					if("4497476000010005".equals(withdraw_status)){
						withdraw_status="提现成功";
						withdrawStatusCode="4497476000010005";
					}
					if("4497476000010006".equals(withdraw_status)){
						withdraw_status="提现失败";
						withdrawStatusCode="4497476000010006";
					}
					withdrawRecord.setWithdrawStatus(withdraw_status);
					withdrawRecord.setWithdrawStatusCode(withdrawStatusCode);
					withdrawRecord.setWithdrawTime(withdrawInfo.get("create_time"));
					withdrawRecord.setWithdrawAccount(withdrawInfo.get("account_code"));
					withdrawRecord.setWithdrawMoney(withdrawInfo
							.get("withdraw_money"));
					withdrawRecord.setThirdWithdrawCode(withdrawInfo.get("third_withdraw_code"));
					withdrawRecord.setWithdrawCode(withdrawInfo.get("withdraw_code"));
					withdrawRecordResult.getWithdrawRecordList().add(withdrawRecord);
				}
			}

			// 更新日志中返回结果
			logMap.put("response_data", new JsonHelper<WithdrawRecordResult>()
					.GsonToJson(withdrawRecordResult));
			logMap.put("response_time", FormatHelper.upDateTime());
			DbUp.upTable("lc_wallet_api_log").dataUpdate(logMap,
					"response_data,response_time", "request_code");
		}

		return withdrawRecordResult;
	}

}
