package com.cmall.groupcenter.account.api;

import java.math.BigDecimal;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

import org.apache.activemq.filter.function.makeListFunction;
import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.account.model.MoneyWithdrawItem;
import com.cmall.groupcenter.account.model.MoneyWithdrawResult;
import com.cmall.groupcenter.model.AccountRelation;
import com.cmall.groupcenter.support.GroupAccountSupport;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
import com.srnpr.zapweb.webdo.WebTemp;

public class ApiMoneyWithdraw extends
		RootApiForToken<MoneyWithdrawResult, RootInput> {

	public MoneyWithdrawResult Process(RootInput inputParam,
			MDataMap mRequestMap) {

		String sAccountCode = DbUp.upTable("mc_member_info")
				.oneWhere("account_code", "", "", "member_code", getUserCode())
				.get("account_code");

		MoneyWithdrawResult moneyWithdrawResult = new MoneyWithdrawResult();

		MDataMap mAccountMap = DbUp.upTable("gc_group_account").one(
				"account_code", sAccountCode);
		if (mAccountMap != null) {

			moneyWithdrawResult.setWithdrawCurrent(mAccountMap
					.get("account_withdraw_money"));
			moneyWithdrawResult.setWithdrawTotal(mAccountMap
					.get("total_withdraw_money"));
			moneyWithdrawResult.setWithdrawPayed(new BigDecimal(
					moneyWithdrawResult.getWithdrawTotal()).subtract(
					new BigDecimal(moneyWithdrawResult.getWithdrawCurrent()))
					.toString());
			
			
			GroupAccountSupport groupAccountSupport=new GroupAccountSupport();
			 List<AccountRelation> listRelations= groupAccountSupport.upAccountFlower(sAccountCode);
			

			// -------------------- 开始处理明细信息
			MDataMap mWhereMap = new MDataMap();
			mWhereMap.inAllValues("account_code", sAccountCode);
			List<MDataMap> listMaps = DbUp.upTable("gc_withdraw_log").query("",
					"-create_time", "", mWhereMap, 0, 0);

			if (listMaps != null && listMaps.size() > 0) {

				List<MoneyWithdrawItem> items = new ArrayList<MoneyWithdrawItem>();
				// 定义当前最后一条的索引
				int iLength = -1;
				// 定义最后一个月的那一条记录的索引
				int iLastMonth = -1;
				for (MDataMap mDataMap : listMaps) {

					String sMonth = DateHelper.upMonth(mDataMap
							.get("create_time"));

					// 判断如果刚开始或者是新的一个月 开始添加月份记录
					if (iLength == -1
							|| !StringUtils.equals(items.get(iLength)
									.getMonth(), sMonth)) {
						MoneyWithdrawItem monehMonth = new MoneyWithdrawItem();
						monehMonth.setMonth(sMonth);
						monehMonth.setShowType(1);
						monehMonth.setTextOne("0.00");
						monehMonth.setTextTwo("0.00");
						monehMonth.setTitleName(sMonth);
						monehMonth.setTitleOne(bInfo(918501202));
						monehMonth.setTitleOne(bInfo(918501203));
						items.add(monehMonth);
						iLastMonth = items.size() - 1;
					}

					MoneyWithdrawItem moneyItem = new MoneyWithdrawItem();
					moneyItem.setMonth(sMonth);
					moneyItem.setShowType(0);
					moneyItem.setTextOne(mDataMap.get("create_time"));
					BigDecimal bMoney = new BigDecimal(
							mDataMap.get("withdraw_money"));
					// 如果大于0则计入月度收入 否则计入月底支出
					if (bMoney.compareTo(BigDecimal.ZERO) > 0) {
						items.get(iLastMonth).setTextOne(
								new BigDecimal(items.get(iLastMonth)
										.getTextOne()).add(bMoney).toString());

					} else {
						items.get(iLastMonth).setTextTwo(
								new BigDecimal(items.get(iLastMonth)
										.getTextTwo()).add(bMoney).toString());
					}
					moneyItem.setTextTwo(mDataMap.get("withdraw_money"));
					moneyItem.setTitleName("");
					
					//moneyItem.setTitleOne(mDataMap.get("remark"));
					
					moneyItem.setTitleOne("1");
					
					
					
					
					
					
					moneyItem.setTitleTwo(WebTemp.upTempDataOne("sc_define",
							"define_name", "define_code",
							mDataMap.get("withdraw_change_type")));

					items.add(moneyItem);

					iLength = items.size() - 1;

				}

				moneyWithdrawResult.setItems(items);

			}
		}

		return moneyWithdrawResult;

	}
}
