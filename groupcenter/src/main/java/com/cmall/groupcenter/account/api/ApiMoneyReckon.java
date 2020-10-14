package com.cmall.groupcenter.account.api;

import java.util.List;

import com.cmall.groupcenter.account.model.MoneyReckonItem;
import com.cmall.groupcenter.account.model.MoneyReckonResult;
import com.cmall.groupcenter.account.model.MoneyWithdrawResult;
import com.cmall.groupcenter.groupdo.GroupConst;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

public class ApiMoneyReckon extends
		RootApiForToken<MoneyReckonResult, RootInput> {

	public MoneyReckonResult Process(RootInput inputParam, MDataMap mRequestMap) {
		MoneyReckonResult moneyReckonResult = new MoneyReckonResult();

		String sAccountCode = DbUp.upTable("mc_member_info")
				.oneWhere("account_code", "", "", "member_code", getUserCode())
				.get("account_code");

		MDataMap mAccountMap = DbUp.upTable("gc_group_account").one(
				"account_code", sAccountCode);
		if (mAccountMap != null) {
			moneyReckonResult.setReckonMoney(mAccountMap
					.get("account_reckon_money"));

			MDataMap mWhereMap = new MDataMap();
			mWhereMap.inAllValues("account_code", sAccountCode);

			String sActiveTime = DateHelper
					.upDateTimeAdd(GroupConst.RECKON_AUTO_CONVERT_DAY);
			mWhereMap.inAllValues("order_reckon_time", sActiveTime);

			List<MDataMap> listMaps = DbUp
					.upTable("gc_reckon_log")
					.query("",
							"-create_time",
							"account_code=:account_code and order_reckon_time>=:order_reckon_time and reckon_change_type in('4497465200030001','4497465200030002')",
							mWhereMap, -1, -1);

			if (listMaps != null) {
				for (MDataMap mDataMap : listMaps) {
					MoneyReckonItem item = new MoneyReckonItem();
					item.setAccountName(mDataMap.get("order_account_code"));
					item.setReckonTime(mDataMap.get("order_reckon_time"));
					item.setReckonMoney(mDataMap.get("reckon_money"));
					item.setRelation(mDataMap.get("relation_level"));
					item.setReckonType(mDataMap.get("reckon_change_type"));
					moneyReckonResult.getItems().add(item);
				}
			}

		}

		return moneyReckonResult;
	}
}
