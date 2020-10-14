package com.cmall.groupcenter.payorder.api;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.cmall.dborm.txmodel.groupcenter.GcReckonLog;
import com.srnpr.zapcom.baseface.IBaseInput;
import com.srnpr.zapcom.baseface.IBaseResult;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;

public class HandlePayOrderDetail extends RootApi<RootResult, RootInput> {

	public RootResult Process(RootInput inputParam, MDataMap mRequestMap) {
		RootResult rootResult = new RootResult();

		// List<MDataMap>
		// payOrderList=DbUp.upTable("gc_pay_order_info").queryByWhere("order_status","4497153900120002","pay_status","4497465200070004");

		List<MDataMap> payOrderList = DbUp
				.upTable("gc_pay_order_info")
				.queryAll(
						"",
						"create_time",
						" order_status!='4497153900120003' and pay_status!='4497465200070003' ",
						new MDataMap());

		for (MDataMap payMap : payOrderList) {
			// 避免重复处理
			List<MDataMap> detailList = DbUp.upTable("gc_pay_order_detail")
					.queryByWhere("pay_order_code",
							payMap.get("pay_order_code"));
			if (detailList != null && detailList.size() > 0) {
				continue;
			}

			// 取出所有清分金额大于已提现金额 并且 返现类型等于清分 并且没有逆向流程的 并且已转入可提现账户的
			List<MDataMap> logList = DbUp
					.upTable("gc_reckon_log")
					.queryAll(
							"",
							"create_time",
							"reckon_money>payed_money and reckon_change_type='4497465200030001' and order_code not in(select order_code from groupcenter.gc_reckon_order_step where exec_type='4497465200050002') and flag_withdraw=0  and account_code='"
									+ payMap.get("account_code") + "'",
							new MDataMap());
			
			
			BigDecimal newWithdrawMoney = new BigDecimal(
					payMap.get("withdraw_money"));
			for (MDataMap logMap : logList) {
				BigDecimal money = new BigDecimal(logMap.get("reckon_money"))
						.subtract(new BigDecimal(logMap.get("payed_money")));
				if (money.compareTo(newWithdrawMoney) == -1) {
					MDataMap updateDataMap = new MDataMap();
					updateDataMap.put("zid", logMap.get("zid"));
					updateDataMap
							.put("payed_money",
									money.add(
											new BigDecimal(logMap
													.get("payed_money")))
											.toString());
					DbUp.upTable("gc_reckon_log").dataUpdate(updateDataMap,
							"payed_money", "zid");
					MDataMap insertDataMap = new MDataMap();
					insertDataMap.put("pay_order_code",
							payMap.get("pay_order_code"));
					insertDataMap.put("order_code", logMap.get("order_code"));
					insertDataMap.put("reckon_money", money.toString());
					insertDataMap.put("create_time", FormatHelper.upDateTime());
					DbUp.upTable("gc_pay_order_detail").dataInsert(
							insertDataMap);
					newWithdrawMoney = newWithdrawMoney.subtract(money);
				}

				else if (money.compareTo(newWithdrawMoney) == 1
						|| money.compareTo(newWithdrawMoney) == 0) {
					MDataMap updateDataMap = new MDataMap();
					updateDataMap.put("zid", logMap.get("zid"));
					updateDataMap.put(
							"payed_money",
							newWithdrawMoney.add(
									new BigDecimal(logMap.get("payed_money")))
									.toString());
					DbUp.upTable("gc_reckon_log").dataUpdate(updateDataMap,
							"payed_money", "zid");
					MDataMap insertDataMap = new MDataMap();
					insertDataMap.put("pay_order_code",
							payMap.get("pay_order_code"));
					insertDataMap.put("order_code", logMap.get("order_code"));
					insertDataMap.put("reckon_money",
							newWithdrawMoney.toString());
					insertDataMap.put("create_time", FormatHelper.upDateTime());
					DbUp.upTable("gc_pay_order_detail").dataInsert(
							insertDataMap);
					break;
				}
			}

		}

		rootResult.setResultMessage("成功");

		return rootResult;
	}

}
