package com.cmall.groupcenter.third.api;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.account.model.DelBankInput;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 删除银行卡信息
 * 
 * @author srnpr
 * 
 */
public class GroupDelBankApi extends RootApiForToken<RootResultWeb, DelBankInput> {

	public RootResultWeb Process(DelBankInput inputParam, MDataMap mRequestMap) {
		RootResultWeb rootResultWeb = new RootResultWeb();

		if (rootResultWeb.upFlagTrue()) {

			String sAccountCode = DbUp
					.upTable("mc_member_info")
					.oneWhere("account_code", "", "", "member_code",
							getUserCode()).get("account_code");

			if (StringUtils.isNotEmpty(inputParam.getBankCode())) {

				MDataMap mBankMap = DbUp.upTable("gc_member_bank").one(
						"account_code", sAccountCode, "bank_code",
						inputParam.getBankCode());
				if (mBankMap != null) {

					mBankMap.put("flag_enable", "0");
					mBankMap.put("delete_time", FormatHelper.upDateTime());
					DbUp.upTable("gc_member_bank").dataUpdate(mBankMap,
							"flag_enable,delete_time", "zid");

				}
			} else {

				// 将所有已绑定的置为未绑定
				MDataMap mBankMap = new MDataMap();
				mBankMap.put("account_code", sAccountCode);
				mBankMap.put("flag_enable", "0");
				mBankMap.put("delete_time", FormatHelper.upDateTime());

				DbUp.upTable("gc_member_bank").dataUpdate(mBankMap,
						"flag_enable,delete_time", "account_code");
			}

		}

		return rootResultWeb;
	}

}
