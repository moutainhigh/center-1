package com.cmall.groupcenter.account.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cmall.groupcenter.account.model.ApiTheChartsInput;
import com.cmall.groupcenter.account.model.ApiTheChartsResult;
import com.cmall.groupcenter.account.model.ApiTheChartsResultList;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;

public class ApiTheCharts extends RootApi<ApiTheChartsResult, ApiTheChartsInput>{

	public ApiTheChartsResult Process(ApiTheChartsInput inputParam, MDataMap mRequestMap) {
		ApiTheChartsResult result = new ApiTheChartsResult();
		MDataMap mDataMap = new MDataMap();
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
			String date = "2014-10";
			Date data1 = format.parse(inputParam.getDate());
			Date data2 = format.parse(date);
			/*if(data1.before(data2) || data1.after(new Date())) {
				return result;
			}*/
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		mDataMap.put("active_month", inputParam.getDate());
		String activeSql = "SELECT SUM(g.reckon_money) AS reckon_money ,l.level_name ,m.login_name ,g.account_code FROM groupcenter.gc_reckon_log g JOIN groupcenter.gc_group_account c ON c.account_code = g.account_code JOIN groupcenter.gc_group_level l ON l.level_code = c.account_level JOIN (SELECT a.member_code,a.account_code FROM membercenter.mc_member_info a, (SELECT g.account_code,SUM(reckon_money) AS reckon_money FROM groupcenter.gc_reckon_log g JOIN groupcenter.gc_group_account c ON g.account_code=c.account_code WHERE DATE_FORMAT(g.order_reckon_time, '%Y-%m') =:active_month AND g.reckon_change_type IN ('4497465200030001','4497465200030002') AND c.account_level!='4497465200010006' GROUP BY g.account_code ORDER BY reckon_money DESC LIMIT 10) b WHERE a.account_code =b.account_code GROUP BY a.account_code) o ON o.account_code = c.account_code JOIN membercenter.mc_login_info m ON m.member_code = o.member_code WHERE DATE_FORMAT(g.order_reckon_time, '%Y-%m') =:active_month AND g.reckon_change_type IN ('4497465200030001','4497465200030002') AND c.account_level!='4497465200010006' GROUP BY g.account_code ORDER BY reckon_money DESC LIMIT 10 ";
		List<Map<String, Object>> activeList = DbUp.upTable("gc_reckon_log").dataSqlList(activeSql, mDataMap);
		for(Map<String, Object> map : activeList) {//查询返利金额和account_code
			ApiTheChartsResultList apiTheChartsResultList = new ApiTheChartsResultList();
			apiTheChartsResultList.setMoney(String.valueOf(map.get("reckon_money")));
			apiTheChartsResultList.setLevelName(String.valueOf(map.get("level_name")));
			apiTheChartsResultList.setMobilePhone(String.valueOf(map.get("login_name")));
			result.getList().add(apiTheChartsResultList);
		}
		return result;
	}
}
