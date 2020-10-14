package com.cmall.groupcenter.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cmall.groupcenter.util.WgsMailSupport;
import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 检测用户订单金额信息（用户账户金额与日志中的金额是否一致）
 * 
 * @author lijx
 *
 */

public class CheckUserMoneyService extends BaseClass{
	
	public void doUserMoney(){
		
		MDataMap mWhereMap=new MDataMap();
		
		//邮件title
		String time=DateUtil.toString(DateUtil.addDays(new Date(), -1), DateUtil.sdfDateOnly);
		String title = "[微公社]" + time + "日，账户可提现金额有问的账户编号";
		
		
		//获取账户编号、可提现账户金额		
		String wSql = "SELECT g.account_code as accountCode,g.account_withdraw_money,li.withdrawMoney from gc_group_account g "
				+ "LEFT JOIN(SELECT SUM(w.withdraw_money) as withdrawMoney,w.account_code as accountCode "
				+ "FROM gc_withdraw_log w GROUP BY w.account_code) li on "
				+ "li.accountCode=g.account_code where li.withdrawMoney != g.account_withdraw_money";
		List<Map<String, Object>> withdrawData =DbUp.upTable("gc_group_account").dataSqlList(wSql, mWhereMap);
		
		int withdrawNum = withdrawData.size();		
		String withdrawNumcontent="用户的可提现金额与可提现日志表不一致的用户有"+withdrawNum+"人，分别为：<br/>";
		String withdrawMoneycontent="";
		for(int i=0;i<withdrawData.size();i++){
			Map<String,Object> withdrawMap = withdrawData.get(i);
			String withdraw=String.valueOf(withdrawMap.get("accountCode"));
			String accountMoney = String.valueOf(withdrawMap.get("account_withdraw_money"));
			String withdrawMoney = String.valueOf(withdrawMap.get("withdrawMoney"));
						
			withdrawMoneycontent += "用户编号为："+withdraw+"，其中账户表中的可提现金额为："+accountMoney+"，可提现日志表中的可提现金额为："+withdrawMoney+"<br/>";

		}
				
		//获取账户编号、返利金额
		String rebateSql = "SELECT g.account_code as accountCode,g.account_rebate_money,li.rebateMoney from "
				+ "gc_group_account g LEFT JOIN(SELECT r.account_code as accountCode ,SUM(r.rebate_money) as rebateMoney from "
				+ "gc_rebate_log r where r.flag_status='1' GROUP BY r.account_code) li on "
				+ "li.accountCode=g.account_code where li.rebateMoney != g.account_rebate_money";
		List<Map<String, Object>> rebateData =DbUp.upTable("gc_group_account").dataSqlList(rebateSql, mWhereMap);
		
		int rebateNum = rebateData.size();
		String rebateNumContent = "用户的返利金额与返利日志表不一致的用户有"+rebateNum+"人，分别为：<br/>";
		String rebateMoneyContent = "";
		for(int j=0;j<rebateData.size();j++){
			Map<String,Object> rebateMap = rebateData.get(j);
			String rebate = String.valueOf(rebateMap.get("accountCode"));
			String accountRebate = String.valueOf(rebateMap.get("account_rebate_money"));
			String logRebate = String.valueOf(rebateMap.get("rebateMoney"));
			
			rebateMoneyContent += "用户编号为："+rebate+"，其中账户表中的返利金额为："+accountRebate+"，返利日志表中的返利金额为："+logRebate+"<br/>";
		}

		//System.out.println(rebateList);
		
		
		//获取账户编号、清分金额
		String reckonSql = "SELECT g.account_code as accountCode,g.account_reckon_money,li.reckonMoney from "
				+ "gc_group_account g LEFT JOIN(SELECT l.account_code as accountCode,SUM(l.reckon_money) as reckonMoney from "
				+ "gc_reckon_log l where l.flag_status='1' GROUP BY l.account_code) li on "
				+ "li.accountCode=g.account_code where li.reckonMoney != g.account_reckon_money;";
		List<Map<String, Object>> reckonData =DbUp.upTable("gc_group_account").dataSqlList(reckonSql, mWhereMap);
		
		int reckonNum = reckonData.size();
		String reckonNumContent = "用户的清分金额与清分日志表不一致的用户有"+reckonNum+"人，分别为：<br/>";
		String reckonMonryContent = "";
		for(int n=0;n<reckonData.size();n++){
			Map<String,Object> reckonMap = reckonData.get(n);
			String reckon = String.valueOf(reckonMap.get("accountCode"));
			String accountReckon = String.valueOf(reckonMap.get("account_reckon_money"));
			String logReckon = String.valueOf(reckonMap.get("reckonMoney"));
			
			reckonMonryContent += "用户编号为："+reckon+"，其中账户表中的清分金额为："+accountReckon+"，清分日志表中的清分金额为："+logReckon+"<br/>";
			
		}

		//System.out.println(reckonList);
		
		
//		//发送邮件
//		String time=DateUtil.toString(DateUtil.addDays(new Date(), -1), DateUtil.sdfDateOnly);
//		String title = "[微公社]" + time + "日，账户可提现金额有问的账户编号";
//		String content = "用户的可提现金额与可提现日志表不一致的用户有"+withdrawNum+"人，分别为：</br>"+withdrawList+
//				"</br>其中账户表中的可提现金额分别为："+accountMoneyList+
//				"</br>可提现日志表中的可提现金额分别为："+withdrawMoneyList
//				+"</br></br>用户的返利金额与返利日志表不一致的用户有"+rebateNum+"人，分别为：</br>"+rebateList+
//				"</br>其中账户表中的返利金额分别为："+accountRebateList+
//				"</br>返利日志表中的返利金额分别为："+logRebateList
//				+"</br></br>用户的清分金额与清分日志表不一致的用户有"+reckonNum+"人，分别为：</br>"+reckonList+
//				"</br>其中账户表中的清分金额分别为："+accountReckonList+
//				"</br>清分日志表中的清分金额分别为："+logReckonList;
				
		//WgsMailSupport.INSTANCE.sendMail("每日检测账户各项金额", title,content);
		//System.out.println(content);
		
		//发送邮件
		String content=withdrawNumcontent+withdrawMoneycontent+"<br/><br/>"+
				rebateNumContent+rebateMoneyContent+"<br/><br/>"+
				reckonNumContent+reckonMonryContent;
		
		WgsMailSupport.INSTANCE.sendMail("每日检测账户各项金额", title,content);
	
	}
}
