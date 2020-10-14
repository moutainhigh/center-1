package com.cmall.groupcenter.wallet.api;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.cmall.groupcenter.service.TraderOperationFilterService;
import com.srnpr.zapcom.basehelper.VersionHelper;
import com.srnpr.zapweb.webmodel.MWebResult;
import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.account.model.AccountBankInfo;
import com.cmall.groupcenter.third.model.GroupPayInput;
import com.cmall.groupcenter.third.model.GroupPayResult;
import com.cmall.groupcenter.wallet.model.BankInfo;
import com.cmall.groupcenter.wallet.model.WithdrawInfoInput;
import com.cmall.groupcenter.wallet.model.WithdrawInfoResult;
import com.cmall.groupcenter.wallet.service.WalletWithdrawService;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.helper.WebSessionHelper;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 该类为钱包返回专属账户提现信息API
 * 
 * @author huangs
 * @date 2015-11-4
 * @param <WithdrawInfoResult>
 * @param <WithdrawInfoInput>
 */
public class ApiWalletWithdrawInfo extends
		RootApiForManage<WithdrawInfoResult, WithdrawInfoInput> {

	@Override
	public WithdrawInfoResult Process(WithdrawInfoInput inputParam,
			MDataMap mRequestMap) {

		// 记录请求
		HttpServletRequest request = WebSessionHelper.create().upHttpRequest();
		MDataMap logMap = new MDataMap();
		logMap.put("request_code", WebHelper.upCode("RWAL"));
		logMap.put("request_target", "walletWithdrawInfo");
		logMap.put("request_url", "http://" + request.getServerName() + ":"
				+ request.getServerPort() + request.getRequestURI());
		logMap.put("request_data",
				new JsonHelper<WithdrawInfoInput>().GsonToJson(inputParam));
		logMap.put("request_time", FormatHelper.upDateTime());
		logMap.put("create_time", FormatHelper.upDateTime());
		DbUp.upTable("lc_wallet_api_log").dataInsert(logMap);
		//
		WithdrawInfoResult withdrawInfoResult = new WithdrawInfoResult();

		//判断用户是否开通了钱包功能
		TraderOperationFilterService traderOperationFilterService = new TraderOperationFilterService();
		MWebResult webResult = traderOperationFilterService.checkOperationWalletByManageCode(getManageCode());
		withdrawInfoResult.inOtherResult(webResult);

		if (withdrawInfoResult.upFlagTrue()){
			WalletWithdrawService service = new WalletWithdrawService();
			String traderCode = service.getTraderCode(getManageCode());
			String memberCode = inputParam.getMemberCode();
			String manageCode = getManageCode();
			if (traderCode == null) {
				withdrawInfoResult.setResultCode(918560003);
				withdrawInfoResult.setResultMessage(bInfo(918560003));
				return withdrawInfoResult;
			}
			String accountCode = service.getAccountCode(memberCode, manageCode);
			if (accountCode == null) {
				withdrawInfoResult.setResultCode(918560002);
				withdrawInfoResult.setResultMessage(bInfo(918560002));
				return withdrawInfoResult;
			}
			MDataMap walletMap = service.findWallet(traderCode, accountCode);
			if (walletMap == null) {
				withdrawInfoResult.setResultCode(918560002);
				withdrawInfoResult.setResultMessage(bInfo(918560002));
				return withdrawInfoResult;
			}
			withdrawInfoResult.setWithdrawMoney(walletMap.get("available_balance"));
			withdrawInfoResult.setFlagWithdraw(Integer.parseInt(walletMap
					.get("account_status")));
			MDataMap map = new MDataMap();
			map.put("account_code", accountCode);
			map.put("flag_enable", "1");
			List<MDataMap> bankInfoMapList = DbUp.upTable("gc_member_bank")
					.queryAll("", "-sort_num,-create_time", "", map);

			if (bankInfoMapList.size() != 0) {
				for (MDataMap bankInfoMap : bankInfoMapList) {
					BankInfo bankInfo = new BankInfo();
					// 信息编号
					bankInfo.setBankCode(bankInfoMap.get("bank_code"));
					// 银行名称
					bankInfo.setBankName(bankInfoMap.get("bank_name"));
					//银行卡类型
					bankInfo.setCardKind(bankInfoMap.get("card_kind"));
					// 银行卡卡号
					String cardcode = bankInfoMap.get("card_code");
					bankInfo.setCardCode(StringUtils.leftPad(
							StringUtils.right(cardcode, 4), cardcode.length(), "*"));
					// 银行预留手机号
					bankInfo.setBankPhone(bankInfoMap.get("bank_phone"));

					withdrawInfoResult.getBankInfoList().add(bankInfo);
				}
			}

			// 更新日志中返回结果
			logMap.put("response_data", new JsonHelper<WithdrawInfoResult>()
					.GsonToJson(withdrawInfoResult));
			logMap.put("response_time", FormatHelper.upDateTime());
			DbUp.upTable("lc_wallet_api_log").dataUpdate(logMap,
					"response_data,response_time", "request_code");
		}

		return withdrawInfoResult;
	}
}