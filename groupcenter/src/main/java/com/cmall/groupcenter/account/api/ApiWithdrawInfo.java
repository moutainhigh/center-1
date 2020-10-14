package com.cmall.groupcenter.account.api;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.account.model.AccountBankInfo;
import com.cmall.groupcenter.account.model.AccountInfoResult;
import com.cmall.groupcenter.account.model.WithdrawInfoResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 提现
 * 
 * @author srnpr
 * 
 */
public class ApiWithdrawInfo extends
		RootApiForToken<WithdrawInfoResult, RootInput> {

	public WithdrawInfoResult Process(RootInput inputParam, MDataMap mRequestMap) {

		WithdrawInfoResult withdrawInfoResult = new WithdrawInfoResult();

		String sAccountCode = DbUp.upTable("mc_member_info")
				.oneWhere("account_code", "", "", "member_code", getUserCode())
				.get("account_code");

		MDataMap mGroupAccountMap = DbUp.upTable("gc_group_account").oneWhere(
				"account_withdraw_money", "", "", "account_code", sAccountCode);

		if (mGroupAccountMap != null) {

			withdrawInfoResult.setWithdrawMoney(mGroupAccountMap
					.get("account_withdraw_money"));

			MDataMap mBankInfoMap = DbUp.upTable("gc_member_bank").oneWhere("",
					"-sort_num,-create_time", "", "account_code", sAccountCode,
					"flag_enable", "1");

			if (mBankInfoMap != null) {

				AccountBankInfo accountBankInfo = new AccountBankInfo();
				accountBankInfo.setBankCode(mBankInfoMap.get("bank_code"));
				accountBankInfo.setBankName(mBankInfoMap.get("bank_name"));
				// accountBankInfo.setBankPhone("");

				String sBankCode = mBankInfoMap.get("card_code");

				accountBankInfo.setCardCode(StringUtils.leftPad(
						StringUtils.right(sBankCode, 4), sBankCode.length(),
						"*"));

				withdrawInfoResult.getBankInfoList().add(accountBankInfo);

				if (new BigDecimal(withdrawInfoResult.getWithdrawMoney())
						.compareTo(BigDecimal.ZERO) > 0) {
					withdrawInfoResult.setFlagWithdraw(1);
				}

			}

		}

		return withdrawInfoResult;

	}
}
