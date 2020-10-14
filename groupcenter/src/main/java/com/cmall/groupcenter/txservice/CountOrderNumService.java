package com.cmall.groupcenter.txservice;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.cmall.groupcenter.util.WgsMailSupport;
import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 获取进入微公社的订单数量
 * 
 * @author lijx
 */

public class CountOrderNumService extends BaseClass{
	
	public void doOrderNum(){
		
		MDataMap mWhereMap=new MDataMap();
		
		Calendar c = Calendar.getInstance();
        c.setTime(DateUtil.addDays(new Date(), 0));
        c.set(Calendar.HOUR_OF_DAY, 00);
        c.set(Calendar.MINUTE, 00);
        c.set(Calendar.SECOND, 00);
        c.set(Calendar.MILLISECOND, 000);
        Date nowTime=c.getTime();
        Date startTime=DateUtil.addDays(nowTime, -1);
		mWhereMap.put("startTime",DateUtil.toString(startTime, DateUtil.DATE_FORMAT_DATETIME));
		mWhereMap.put("endTime",DateUtil.toString(nowTime, DateUtil.DATE_FORMAT_DATETIME));
		
		//新接入订单数量及金额
		
//		int orderNum = DbUp.upTable("gc_reckon_order_info").dataCount("create_time>=:startTime and create_time<:endTime", mWhereMap);
//		
//		String oSql="select SUM(reckon_money) from gc_reckon_order_info where create_time>=:startTime and create_time<:endTime";
		String oSql="SELECT count(DISTINCT(order_code)) AS totalcount,ifnull(sum(reckon_money), 0) AS totalmoney "
				+ "FROM groupcenter.gc_reckon_order_info WHERE create_time >=:startTime AND create_time <:endTime ";
		Map<String,Object> orderSum=DbUp.upTable("gc_reckon_order_info").dataSqlOne(oSql, mWhereMap);
		int orderNum = Integer.valueOf(String.valueOf(orderSum.get("totalcount")));
		BigDecimal gurrantee_use=BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_DOWN);
		if(!("0.00".equals(String.valueOf(orderSum.get("totalmoney"))))){
			//gurrantee_use=new BigDecimal(orderSum.get("SUM(reckon_money)").toString());
			gurrantee_use=new BigDecimal(String.valueOf(orderSum.get("totalmoney")));
		}
		
		//预返利的订单数及总金额
		//正向
//		List<MDataMap> forwardRebateList=DbUp.upTable("gc_reckon_order_step").queryAll(
//				"","","exec_finish_time>:startTime and exec_finish_time<:endTime and "
//						+ "exec_type = '4497465200050003' and flag_success = 1", mWhereMap);
		
		String forwardRebateSql="SELECT count(DISTINCT(order_code)) AS totalcount,ifnull(sum(rebate_money), 0) AS totalmoney "
				+ "FROM groupcenter.gc_rebate_log WHERE create_time >=:startTime AND create_time <:endTime "
				+ "AND rebate_change_type = '4497465200140001' AND flag_status = '1'";
				
		Map<String,Object> forwardRebate=DbUp.upTable("gc_rebate_log").dataSqlOne(forwardRebateSql, mWhereMap);
		int forwardRebateNum = Integer.valueOf(String.valueOf(forwardRebate.get("totalcount")));
		BigDecimal forwardMoney=BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_DOWN);
		if(!("0.00".equals(String.valueOf(forwardRebate.get("totalmoney"))))){
			forwardMoney=new BigDecimal(String.valueOf(forwardRebate.get("totalmoney")));
		}
		
		//逆向
//		List<MDataMap> backwardRebateList=DbUp.upTable("gc_reckon_order_step").queryAll(
//				"","","exec_finish_time>:startTime and exec_finish_time<:endTime and "
//						+ "exec_type = '4497465200050004' and flag_success = 1", mWhereMap);
		
		String backwardRebateSql="SELECT count(DISTINCT(order_code)) AS totalcount,ifnull(sum(rebate_money)* -1, 0) AS totalmoney "
				+ "FROM groupcenter.gc_rebate_log WHERE create_time >=:startTime AND create_time <:endTime "
				+ "AND rebate_change_type = '4497465200140002' AND flag_status = '1'";
		
		Map<String,Object> backwardRebate=DbUp.upTable("gc_rebate_log").dataSqlOne(backwardRebateSql, mWhereMap);
		int backwardRebateNum = Integer.valueOf(String.valueOf(backwardRebate.get("totalcount")));
		BigDecimal backwardMoney=BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_DOWN);
		if(!("0.00".equals(String.valueOf(backwardRebate.get("totalmoney"))))){
			backwardMoney=new BigDecimal(String.valueOf(backwardRebate.get("totalmoney")));
		}
		
		//清分的订单数及总金额
		//正向
//		List<MDataMap> forwardReckonList=DbUp.upTable("gc_reckon_order_step").queryAll(
//				"","","exec_finish_time>:startTime and exec_finish_time<:endTime and "
//						+ "exec_type = '4497465200050001' and flag_success = 1", mWhereMap);
		
		String forwardReckonSql="SELECT count(DISTINCT(order_code)) AS totalcount,ifnull(sum(reckon_money), 0) AS totalmoney "
				+ "FROM groupcenter.gc_reckon_log WHERE create_time >=:startTime AND create_time <:endTime "
				+ "AND reckon_change_type = '4497465200030001' AND flag_status = '1'";
		
		Map<String,Object> forwardReckonSum=DbUp.upTable("gc_reckon_log").dataSqlOne(forwardReckonSql, mWhereMap);
		int forwardReckonNum = Integer.valueOf(String.valueOf(forwardReckonSum.get("totalcount")));
		BigDecimal forwardReckonMoney=BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_DOWN);
		if(!("0.00".equals(String.valueOf(forwardReckonSum.get("totalmoney"))))){
			forwardReckonMoney=new BigDecimal(String.valueOf(forwardReckonSum.get("totalmoney")));
		}
		
		//逆向
//		List<MDataMap> backwardReckonList=DbUp.upTable("gc_reckon_order_step").queryAll(
//				"","","exec_finish_time>:startTime and exec_finish_time<:endTime and "
//						+ "exec_type = '4497465200050001' and flag_success = 1", mWhereMap);
		
		String backwardReckonSql="SELECT count(DISTINCT(order_code)) AS totalcount,ifnull(sum(reckon_money)* -1, 0) AS totalmoney "
				+ "FROM groupcenter.gc_reckon_log WHERE create_time >=:startTime AND create_time <:endTime "
				+ "AND reckon_change_type = '4497465200030002' AND flag_status = '1'";
		
		Map<String,Object> backwardReckonSum=DbUp.upTable("gc_reckon_log").dataSqlOne(backwardReckonSql, mWhereMap);
		int backwardReckonNum = Integer.valueOf(String.valueOf(backwardReckonSum.get("totalcount")));
		BigDecimal backwardReckonMoney=BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_DOWN);
		if(!("0.00".equals(String.valueOf(backwardReckonSum.get("totalmoney"))))){
			backwardReckonMoney=new BigDecimal(String.valueOf(backwardReckonSum.get("totalmoney")));
		}
		
		//可以转可提现账户的数量及总金额
		String wSql="SELECT count(DISTINCT(order_code)) AS totalcount,ifnull(sum(reckon_money), 0) AS totalmoney "
				+ "FROM groupcenter.gc_reckon_log WHERE create_time >=:startTime AND create_time <:endTime "
				+ "AND reckon_change_type = '4497465200030004' AND flag_status = '1'";
		Map<String,Object> withdrawSum=DbUp.upTable("gc_reckon_log").dataSqlOne(wSql, mWhereMap);
		int withdraw = Integer.valueOf(String.valueOf(withdrawSum.get("totalcount")));
		BigDecimal withdrawMoney=BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_DOWN);
		if(!("0.00".equals(String.valueOf(withdrawSum.get("totalmoney"))))){
			withdrawMoney=new BigDecimal(String.valueOf(withdrawSum.get("totalmoney")));
		}
		
				
		//发送邮件
		String time=DateUtil.toString(DateUtil.addDays(new Date(), -1), DateUtil.sdfDateOnly);
		
		//String title= bConfig("groupcenter.wgs_order_info_title");
		String title = "[微公社]" + time + "日订单信息统计";
		
		String mailList="";
		
		mailList="新接入订单数为:"+orderNum+",订单金额为:"+gurrantee_use+"元;"
				+ "</br>正向返利订单数为:"+forwardRebateNum+",正向返利金额为:"+forwardMoney+"元;"
						+ "</br>逆向返利订单数为:"+backwardRebateNum+",逆向返利金额为:"+backwardMoney+"元;"
								+ "</br>正向清分订单数为:"+forwardReckonNum+",正向清分金额为:"+forwardReckonMoney+"元;"
										+ "</br>逆向清分订单数为:"+backwardReckonNum+",逆向清分金额为:"+backwardReckonMoney+"元;"
												+ "<br>可提现的订单数为:"+withdraw+",可提现金额为:"+withdrawMoney+"元.";
		
		WgsMailSupport.INSTANCE.sendMail("统计当日订单信息", title,mailList);
		
		//System.out.println(mailList);

	}
	
	
}
