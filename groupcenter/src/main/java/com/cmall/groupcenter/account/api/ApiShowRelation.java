package com.cmall.groupcenter.account.api;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.account.model.DayRetion;
import com.cmall.groupcenter.account.model.ShowRelationResult;
import com.cmall.groupcenter.model.AccountRelation;
import com.cmall.groupcenter.support.GroupAccountSupport;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
import com.srnpr.zapweb.webdo.WebConst;

/**
 * 获取用户社友关联的展示
 * 
 * @author srnpr
 * 
 */
public class ApiShowRelation extends
		RootApiForToken<ShowRelationResult, RootInput> {

	public ShowRelationResult Process(RootInput inputParam, MDataMap mRequestMap) {
		ShowRelationResult showRelationResult = new ShowRelationResult();

		String sAccountCode = DbUp.upTable("mc_member_info")
				.oneWhere("account_code", "", "", "member_code", getUserCode())
				.get("account_code");

		// 取出一度社友
		List<MDataMap> listOnes = DbUp.upTable("gc_member_relation")
				.queryByWhere("parent_code", sAccountCode, "flag_enable", "1");

		if (listOnes != null && listOnes.size() > 0) {

			Map<String, DayRetion> mapDay = new ConcurrentHashMap<String, DayRetion>();

			String sToday = upDay(FormatHelper.upDateTime());

			Map<String, Integer> mapRelation = new ConcurrentHashMap<String, Integer>();

			// 定义一度社友
			List<String> listRes = new ArrayList<String>();
			for (MDataMap mDataMap : listOnes) {
				listRes.add(mDataMap.get("account_code"));

				mapRelation.put(mDataMap.get("account_code"), 1);
				String sDay = upDay(mDataMap.get("create_time"));
				if (!mapDay.containsKey(sDay)) {
					mapDay.put(sDay, new DayRetion());
					mapDay.get(sDay).setDate(sDay);
				}
				mapDay.get(sDay).setOne(mapDay.get(sDay).getOne() + 1);

			}
			showRelationResult.setOneSumAccount(listOnes.size());

			// 取出二度社友
			List<MDataMap> listTwos = DbUp.upTable("gc_member_relation")
					.queryInSafe(
							"account_code,create_time",
							"",
							" flag_enable=1 ",
							new MDataMap(),
							-1,
							-1,
							"parent_code",
							StringUtils.join(listRes,
									WebConst.CONST_SPLIT_COMMA));

			if (listTwos != null) {
				for (MDataMap mDataMap : listTwos) {
					mapRelation.put(mDataMap.get("account_code"), 2);
					String sDay = upDay(mDataMap.get("create_time"));
					if (!mapDay.containsKey(sDay)) {
						mapDay.put(sDay, new DayRetion());
						mapDay.get(sDay).setDate(sDay);
					}
					mapDay.get(sDay).setTwo(mapDay.get(sDay).getTwo() + 1);
				}

				showRelationResult.setTwoSumAccount(listTwos.size());
			}

			// -------------------- 开始计算当月活跃
			MDataMap mWhereMap = new MDataMap();
			mWhereMap
					.inAllValues("account_code", sAccountCode, "today", sToday);
			List<MDataMap> listActives = DbUp
					.upTable("gc_active_log")
					.queryAll(
							"order_account_code,consume_money",
							"",
							"account_code=:account_code and left(create_time,10)=:today",
							mWhereMap);
			// 定义已经添加过的社友账户
			MDataMap mExistMap = new MDataMap();
			for (MDataMap mDataMap : listActives) {
				String sRelationAccount = mDataMap.get("order_account_code");
				if (mapRelation.containsKey(sRelationAccount)) {

					if (mapRelation.get(sRelationAccount) == 1) {
						if (!mExistMap.containsKey(sRelationAccount)) {
							showRelationResult
									.setOneActiveAccount(showRelationResult
											.getOneActiveAccount() + 1);
						}

						showRelationResult.setOneActiveMoney(new BigDecimal(
								showRelationResult.getOneActiveMoney()).add(
								new BigDecimal(mDataMap.get("consume_money")))
								.toString());

					} else if (mapRelation.get(sRelationAccount) == 2) {
						if (!mExistMap.containsKey(sRelationAccount)) {
							showRelationResult
									.setTwoActiveAccount(showRelationResult
											.getTwoActiveAccount() + 1);
						}

						showRelationResult.setTwoActiveMoney(new BigDecimal(
								showRelationResult.getTwoActiveMoney()).add(
								new BigDecimal(mDataMap.get("consume_money")))
								.toString());
					}

				}

				mExistMap.put(sRelationAccount, sRelationAccount);

			}

			int iOneSum = 0;
			int iTwoSum = 0;
			for (Entry<String, DayRetion> entry : sortMapByKey(mapDay)
					.entrySet()) {

				iOneSum = iOneSum + entry.getValue().getOne();
				iTwoSum = iTwoSum + entry.getValue().getTwo();

				entry.getValue().setOne(iOneSum);
				entry.getValue().setTwo(iTwoSum);

				showRelationResult.getDayRetions().add(0,entry.getValue());
			}

			/*
			 * for (String sKey : sortMapByKey(mapDay).keySet()) {
			 * showRelationResult.getDayRetions().add(mapDay.get(sKey)); }
			 */

			showRelationResult
					.setSumRelation(showRelationResult.getOneSumAccount()
							+ showRelationResult.getTwoSumAccount());

		}

		return showRelationResult;
	}

	public static Map<String, DayRetion> sortMapByKey(Map<String, DayRetion> map) {
		if (map == null || map.isEmpty()) {
			return null;
		}
		Map<String, DayRetion> sortMap = new TreeMap<String, DayRetion>(
				new MapKeyComparator());
		sortMap.putAll(map);
		return sortMap;
	}

	/**
	 * 获取时间
	 * 
	 * @param sDate
	 * @return
	 */
	private String upDay(String sDate) {
		return StringUtils.substringBefore(sDate, " ");
	}

}
