package com.cmall.groupcenter.account.api;

import java.math.BigDecimal;
import java.util.List;

import com.cmall.groupcenter.account.model.AccountInfoResult;
import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.groupcenter.model.AccountRelation;
import com.cmall.groupcenter.support.GroupAccountSupport;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
import com.srnpr.zapweb.webdo.WebTemp;

/**
 * 获取账户信息
 * 
 * @author srnpr
 *
 */
public class ApiAccountInfo extends
		RootApiForToken<AccountInfoResult, RootInput> {

	public AccountInfoResult Process(RootInput inputParam, MDataMap mRequestMap) {
		AccountInfoResult accountInfoResult = new AccountInfoResult();

		String sAccountCode = DbUp.upTable("mc_member_info")
				.oneWhere("account_code", "", "", "member_code", getUserCode())
				.get("account_code");
		MDataMap mGroupAccountMap = DbUp.upTable("gc_group_account").oneWhere(
				"account_reckon_money,account_withdraw_money,account_level",
				"", "", "account_code", sAccountCode);
		
		
		accountInfoResult.setLoginName(getOauthInfo().getLoginName());
		
		
		
		

		String sLevelCode = "";

		// 如果有微公社账户信息
		if (mGroupAccountMap != null) {

			accountInfoResult.setReckonMoney(mGroupAccountMap
					.get("account_reckon_money"));
			accountInfoResult.setWithdrawMoney(mGroupAccountMap
					.get("account_withdraw_money"));
			sLevelCode = mGroupAccountMap.get("account_level");

			// 开始获取用户的所有下线统计
			List<AccountRelation> listRelations = new GroupAccountSupport()
					.upAccountFlower(sAccountCode);
			accountInfoResult.setAllMember(listRelations.size() - 1);

			// 判断是否可绑定上线 如果关系表中有则不可绑定
			accountInfoResult
					.setFlagRelation(Integer
							.parseInt(DbUp
									.upTable("gc_member_relation")
									.dataGet(
											" count(1) ",
											"account_code=:account_code or parent_code=:account_code",
											new MDataMap("account_code",
													sAccountCode)).toString()) > 0 ? 0
							: 1);

			String sCurrentMonth = DateHelper
					.upMonth(FormatHelper.upDateTime());
			MDataMap mActiveMap = DbUp.upTable("gc_active_month").oneWhere(
					"sum_consume,sum_member", "", "", "account_code",
					sAccountCode, "active_month", sCurrentMonth);
			// 开始取出用户的活跃信息
			if (mActiveMap != null) {

				accountInfoResult.setSumConsume(mActiveMap.get("sum_consume"));
				accountInfoResult.setSumMember(Integer.valueOf(mActiveMap
						.get("sum_member")));
			}

		} else {
			sLevelCode = GroupConst.DEFAULT_LEVEL_CODE;
		}

		MDataMap mLevelMap = WebTemp.upTempDataMap("gc_group_level", "",
				"level_code", sLevelCode);
		accountInfoResult.setLevelName(mLevelMap.get("level_name"));

		// -------------------- 开始判断下一级别升级所需
		MDataMap mNextLevel = WebTemp.upTempDataMap("gc_group_level", "",
				"level_code", mLevelMap.get("next_level"));
		if (mNextLevel != null) {

			int iMembers = Integer.valueOf(mLevelMap.get("upgrade_members"))
					- accountInfoResult.getSumMember();
			if (iMembers < 0) {
				iMembers = 0;
			}

			BigDecimal bConsume = new BigDecimal(
					mLevelMap.get("upgrade_consume")).subtract(new BigDecimal(
					accountInfoResult.getSumConsume()));
			if (bConsume.compareTo(BigDecimal.ZERO) < 0) {
				bConsume = BigDecimal.ZERO;
			}

			accountInfoResult.setUpdateRemark(bInfo(918501201,
					mNextLevel.get("level_name"), String.valueOf(iMembers),
					bConsume.toPlainString()));

		}

		return accountInfoResult;
	}

}
