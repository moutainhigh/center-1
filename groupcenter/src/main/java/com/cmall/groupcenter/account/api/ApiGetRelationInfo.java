package com.cmall.groupcenter.account.api;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.account.model.DayRetion;
import com.cmall.groupcenter.account.model.GetRelationInfo;
import com.cmall.groupcenter.account.model.GetRelationInfoInput;
import com.cmall.groupcenter.account.model.GetRelationInfoResult;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webdo.WebTemp;

public class ApiGetRelationInfo extends RootApi<GetRelationInfoResult, GetRelationInfoInput>{

	public GetRelationInfoResult Process(GetRelationInfoInput inputParam,
			MDataMap mRequestMap) {
		GetRelationInfoResult getRelationInfoResult=new GetRelationInfoResult();
		List<GetRelationInfo> list=new ArrayList<GetRelationInfo>();
		String[] ary = inputParam.getMobile().split(",");
		for(String custId:ary){
			GetRelationInfo getRelationInfo=new GetRelationInfo();
			getRelationInfo.setMobile(custId);
			
			MDataMap memberDataMap=DbUp.upTable("mc_login_info").one("login_name",custId);
			if(memberDataMap==null){
				getRelationInfo.setFlag("2");//本条记录失败
				list.add(getRelationInfo);
				continue;
			}
			String memberCode=memberDataMap.get("member_code");
			MDataMap accountCodedDataMap=DbUp.upTable("mc_member_info").one("member_code",memberCode);
			if(accountCodedDataMap==null){
				getRelationInfo.setFlag("2");
				list.add(getRelationInfo);
				continue;
			}
			String accountCode=accountCodedDataMap.get("account_code");
		    MDataMap accountDataMap=DbUp.upTable("gc_group_account").one("account_code",accountCode);
		    if(accountDataMap==null){
		    	getRelationInfo.setFlag("2");
		    	list.add(getRelationInfo);
		    	continue;
		    }
		    //返现总金额
		    getRelationInfo.setTotalAmount(accountDataMap.get("total_reckon_money"));
		    MDataMap mLevelMap = WebTemp.upTempDataMap("gc_group_level", "",
					"level_code", accountDataMap.get("account_level"));
		    //返现级别
		    getRelationInfo.setUserLevel(mLevelMap.get("level_name"));
		    //升级描述
		    MDataMap mNextLevel = WebTemp.upTempDataMap("gc_group_level", "",
					"level_code", mLevelMap.get("next_level"));
		    String sCurrentMonth = DateHelper
					.upMonth(FormatHelper.upDateTime());
			MDataMap mActiveMap = DbUp.upTable("gc_active_month").oneWhere(
					"sum_consume,sum_member", "", "", "account_code",
					accountCode, "active_month", sCurrentMonth);
			if (mNextLevel != null && mActiveMap!=null) {

				int iMembers = Integer.valueOf(mLevelMap.get("upgrade_members"))
						- Integer.valueOf(mActiveMap.get("sum_member"));
				if (iMembers < 0) {
					iMembers = 0;
				}

				BigDecimal bConsume = new BigDecimal(
						mLevelMap.get("upgrade_consume")).subtract(new BigDecimal(
								mActiveMap.get("sum_consume")));
				if (bConsume.compareTo(BigDecimal.ZERO) < 0) {
					bConsume = BigDecimal.ZERO;
				}
				
				
				getRelationInfo.setLevelBased(bInfo(918501201, mNextLevel.get("level_name"),String.valueOf(iMembers),bConsume.toPlainString()));
				
				

			}

		    //返现已领金额
		    BigDecimal already=new BigDecimal(accountDataMap.get("total_withdraw_money")).subtract(new BigDecimal(accountDataMap.get("account_withdraw_money")));
		    getRelationInfo.setAlreadyAmount(already.toString());
		    //返现未领金额
		    BigDecimal notready=new BigDecimal(accountDataMap.get("total_reckon_money")).subtract(already);
		    getRelationInfo.setNotreadyAmount(notready.toString());
		    //一度、二度好友
		    List<MDataMap> listOnes = DbUp.upTable("gc_member_relation")
					.queryByWhere("parent_code", accountCode, "flag_enable", "1");

		    getRelationInfo.setFirstFriend("0");
		    getRelationInfo.setSecondFriend("0");
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
				//一度好友数量
				getRelationInfo.setFirstFriend(String.valueOf(listOnes.size()));

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
                    //二度好友数量
					getRelationInfo.setSecondFriend(String.valueOf(listTwos.size()));
				}
			}
			MDataMap sqlDataMap=new MDataMap();
			sqlDataMap.put("account_code", accountCode);
			//一度好友总返现金额
			String firstSql="select ifnull(sum(reckon_money),0) as money from gc_reckon_log where account_code=:account_code and relation_level=1 and (reckon_change_type=4497465200030001 or reckon_change_type=4497465200030002)";
			String firstTotalAmount=DbUp.upTable("gc_reckon_log").dataSqlOne(firstSql, sqlDataMap).get("money").toString();
			getRelationInfo.setFirstTotalAmount(firstTotalAmount);
			//二度好友总返现金额
			String secondSql="select ifnull(sum(reckon_money),0) as money from gc_reckon_log where account_code=:account_code and relation_level=2 and (reckon_change_type=4497465200030001 or reckon_change_type=4497465200030002)";
			String secondTotalAmount=DbUp.upTable("gc_reckon_log").dataSqlOne(secondSql, sqlDataMap).get("money").toString();
			getRelationInfo.setSecondTotalAmount(secondTotalAmount);
			//近30天内总返现金额
			String totalSql="select ifnull(sum(reckon_money),0) as money from gc_reckon_log where account_code=:account_code and DATE_SUB(CURDATE(), INTERVAL 30 DAY) <= date(order_reckon_time)  and (reckon_change_type=4497465200030001 or reckon_change_type=4497465200030002)";
			String timeTotalAmount=DbUp.upTable("gc_reckon_log").dataSqlOne(totalSql, sqlDataMap).get("money").toString();
			getRelationInfo.setTimeTotalAmount(timeTotalAmount);
			getRelationInfo.setFlag("1");//本条记录成功
			list.add(getRelationInfo);
		}
		getRelationInfoResult.setGetRelationInfoList(list);
		return getRelationInfoResult;
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
