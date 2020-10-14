package com.cmall.groupcenter.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.groupcenter.model.MPageData;
import com.cmall.groupcenter.model.PageOption;
import com.cmall.groupcenter.model.PageResults;
import com.cmall.groupcenter.pc.model.PcAccountAssetsResult;
import com.cmall.groupcenter.pc.model.PcAccountRecordInfo;
import com.cmall.groupcenter.pc.model.PcAccountRecordInput;
import com.cmall.groupcenter.pc.model.PcAccountRecordResult;
import com.cmall.groupcenter.pc.model.PcAccountWithdrawRecordInput;
import com.cmall.groupcenter.pc.model.PcAccountWithdrawRecordResult;
import com.cmall.groupcenter.pc.model.PcConsumeRecordInfo;
import com.cmall.groupcenter.pc.model.PcConsumeRecordInput;
import com.cmall.groupcenter.pc.model.PcConsumeRecordResult;
import com.cmall.groupcenter.pc.model.PcCutPaymentRecordInfo;
import com.cmall.groupcenter.pc.model.PcCutPaymentRecordInput;
import com.cmall.groupcenter.pc.model.PcCutPaymentRecordResult;
import com.cmall.groupcenter.pc.model.PcFridensInfo;
import com.cmall.groupcenter.pc.model.PcFriendsListInput;
import com.cmall.groupcenter.pc.model.PcFriendsListResult;
import com.cmall.groupcenter.pc.model.PcRebateRecordInfo;
import com.cmall.groupcenter.pc.model.PcRebateRecordInput;
import com.cmall.groupcenter.pc.model.PcRebateRecordResult;
import com.cmall.groupcenter.pc.model.PcVirtualPager;
import com.cmall.groupcenter.pc.model.PcWithdrawRecordInfo;
import com.cmall.groupcenter.util.DataPaging;
import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebTemp;

/**
 * 微公社PC版本相关
 * @author GaoYang
 * @CreateDate 2015年7月23日上午10:17:03
 *
 */
public class GroupPcService extends BaseClass{

	/**
	 * 用户资产
	 * @param accountCode
	 * @param inputParam
	 * @return
	 */
	public PcAccountAssetsResult ShowPcAccountAssets(String accountCode,
			RootInput inputParam) {
		
		PcAccountAssetsResult assetsResult = new PcAccountAssetsResult();
		//账户余额
		String accountWithdrawMoney="0.00";
		//累计返利
		String totalRebateMoney = "0.00";
		//累计提现
		String totalWithdrawMoney = "0.00";
		
		MDataMap acMap=DbUp.upTable("gc_group_account").one("account_code",accountCode);
		if(acMap != null ){
			accountWithdrawMoney = acMap.get("account_withdraw_money");
			totalRebateMoney = acMap.get("total_rebate_money");
		}
		
		String sWhereString = " account_code = '"+accountCode+"'";
		Object sumWithdrawMoney = DbUp.upTable("gc_pay_order_info").dataGet("sum(withdraw_money)", sWhereString+" and pay_status = '4497465200070004'",null);
		if(sumWithdrawMoney != null){
			totalWithdrawMoney = String.valueOf(sumWithdrawMoney);
		}
		
		assetsResult.setAccountWithdrawMoney(accountWithdrawMoney);
		assetsResult.setTotalRebateMoney(totalRebateMoney);
		assetsResult.setTotalWithdrawMoney(totalWithdrawMoney);
		
		return assetsResult;
	}

	/**
	 * 返利明细
	 * @param accountCode
	 * @param inputParam
	 * @return
	 */
	public PcRebateRecordResult ShowPcRebateRecord(String accountCode,
			PcRebateRecordInput inputParam) {
		PcRebateRecordResult pcRebateRecord=new PcRebateRecordResult();
		List<PcRebateRecordInfo> rebateList=new ArrayList<PcRebateRecordInfo>();
		DecimalFormat decimalFormat=new DecimalFormat("0.00");
		//返利状态
		String selRebateStatus = inputParam.getRebateStatus();
		//开始时间
		String selStartTime = inputParam.getStartTime();
		//结束时间
		String selEndTime = inputParam.getEndTime();
		
		//统计已返利笔数
		int countRebateNum = 0;
		//统计已返利金额
		double countRebateMoney = 0d;
		//统计预计返利笔数
		int countExpectRebateNum = 0;
		//统计预计返利金额
		double countExpectRebateMoney = 0d;
		//统计取消返利笔数
		int countCancelRebateNum = 0;
		//统计取消返利金额
		double countCancelRebateMoney = 0d;
		
		//取注册日期作为页面查询时提示的开始日期
		String tipsBeginTime = "";
		String tipTimeSql="select create_time from mc_login_info where member_code in(select member_code from mc_member_info where account_code=:account_code) order by create_time limit 1";
		Map<String, Object> memberMap=DbUp.upTable("mc_login_info").dataSqlOne(tipTimeSql, new MDataMap("account_code",accountCode));
		if(memberMap!=null&&StringUtils.isNotBlank(String.valueOf(memberMap.get("create_time")))){
			tipsBeginTime=memberMap.get("create_time").toString().substring(0, 10);
		}
		pcRebateRecord.setTipsBeginTime(tipsBeginTime);
		//取系统日期作为页面查询时提示的结束日期
		String tipsEndTime = DateUtil.getSysDateString();
		pcRebateRecord.setTipsEndTime(tipsEndTime);
		
		MDataMap accountMap=DbUp.upTable("gc_group_account").one("account_code",accountCode);
		if(accountMap!=null){
			//预计返利
			pcRebateRecord.setTotalExpectMoney(accountMap.get("account_rebate_money"));
			//累计返利 
			pcRebateRecord.setTotalRebateMoney(accountMap.get("total_withdraw_money"));
		}
		
		//汇总数据SQL(不带分页)
		StringBuffer sCountWhereBuf = new StringBuffer();
		sCountWhereBuf.append(" select rebate_money,rebate_status from  gc_rebate_order ");
		
		//返利明细查询条件
		MDataMap whereMap = new MDataMap();
		String sWhere = " account_code ='"+accountCode+"' ";
		sCountWhereBuf.append(" where account_code = '").append(accountCode).append("' ");
		
		if(StringUtils.isNotBlank(selStartTime)){
			sWhere += " and  left(order_create_time,10) >= '" + selStartTime +"' " ;
			sCountWhereBuf.append(" and  left(order_create_time,10) >= '").append(selStartTime).append("' ");
		}
		
		if(StringUtils.isNotBlank(selEndTime)){
			sWhere += " and  left(order_create_time,10) <= '" + selEndTime +"' " ;
			sCountWhereBuf.append(" and  left(order_create_time,10) <= '").append(selEndTime).append("' ");
		}
		
		String sWhereInField = "rebate_status";
		String sWhereInFieldVal = "";
		if("1".equals(selRebateStatus)){
			//预计返利
			sWhereInFieldVal = "4497465200170001,4497465200170002";
			sCountWhereBuf.append(" and  rebate_status in('4497465200170001','4497465200170002') ");
		}else if("2".equals(selRebateStatus)){
			//已返利
			sWhereInFieldVal = "4497465200170004";
			sCountWhereBuf.append(" and  rebate_status ='4497465200170004' ");
		}else if("3".equals(selRebateStatus)){
			//取消返利
			sWhereInFieldVal = "4497465200170003";
			sCountWhereBuf.append(" and  rebate_status ='4497465200170003' ");
		}
		
		//汇总数据
		List<Map<String, Object>> countRecordList=DbUp.upTable("gc_rebate_order").dataSqlList(sCountWhereBuf.toString(), new MDataMap());
		if(countRecordList != null && countRecordList.size()>0){
			for(int i = 0;i<countRecordList.size();i++){
				String rebateMoney = String.valueOf(countRecordList.get(i).get("rebate_money"));
				String rebateStatus = String.valueOf(countRecordList.get(i).get("rebate_status"));
		        //汇总预计返利数据
		        if("4497465200170001".equals(rebateStatus) || "4497465200170002".equals(rebateStatus)){
		        	countExpectRebateNum = countExpectRebateNum+1;
		        	countExpectRebateMoney = addMoney(countExpectRebateMoney,Double.parseDouble(rebateMoney));
		        }
		        
		        //汇总已返利数据
		        if("4497465200170004".equals(rebateStatus)){
		        	countRebateNum = countRebateNum +1;
		        	countRebateMoney = addMoney(countRebateMoney,Double.parseDouble(rebateMoney));
		        }
		        
		        //汇总取消返利数据
		        if("4497465200170003".equals(rebateStatus)){
		        	countCancelRebateNum = countCancelRebateNum +1;
		        	countCancelRebateMoney = addMoney(countCancelRebateMoney,Double.parseDouble(rebateMoney));
		        }
			}
			
			//统计预计返利笔数
			pcRebateRecord.setCountExpectRebateNum(String.valueOf(countExpectRebateNum));
			//统计预计返利金额
			pcRebateRecord.setCountExpectRebateMoney(String.valueOf(decimalFormat.format(countExpectRebateMoney)));
			
			//统计已返利笔数
			pcRebateRecord.setCountRebateNum(String.valueOf(countRebateNum));
			//统计已返利金额
			pcRebateRecord.setCountRebateMoney(String.valueOf(decimalFormat.format(countRebateMoney)));
			
			//统计取消返利笔数
			pcRebateRecord.setCountCancelRebateNum(String.valueOf(countCancelRebateNum));
			//统计取消返利金额
			pcRebateRecord.setCountCancelRebateMoney(String.valueOf(decimalFormat.format(countCancelRebateMoney)));
		}

		
		//返利记录
		MPageData mPageData= DataPaging.upPageDataQueryIn("gc_rebate_order", "", "order_create_time desc",sWhere, whereMap, inputParam.getPageOption(),sWhereInField,sWhereInFieldVal);
		
		List<MDataMap> rebateOrderList=mPageData.getListData();
		if(rebateOrderList!=null&&rebateOrderList.size()>0){
			for(MDataMap orderMap:rebateOrderList){
				
				PcRebateRecordInfo rebateRecordInfo=new PcRebateRecordInfo();
				
		        //下单时间
				rebateRecordInfo.setOrderCreateTime(orderMap.get("order_create_time"));
				
		        //返利金额
				String rebateMoney = orderMap.get("rebate_money");
				rebateRecordInfo.setRebateMoney(rebateMoney);
				
		        //订单状态
				String orderStatus = orderMap.get("order_status");
				if("4497153900010001".equals(orderStatus)){
					rebateRecordInfo.setOrderStatus("待付款");
				}else if("4497153900010002".equals(orderStatus)){
					rebateRecordInfo.setOrderStatus("待发货");
				}else{
					rebateRecordInfo.setOrderStatus(WebTemp.upTempDataOne("sc_define", "define_name", "define_code",orderStatus));
				}
				
		        //返利状态
		        String rebateStatus = orderMap.get("rebate_status");
		        String description="";
		        String preDay="";
		        //返利状态是未付款or已付款时，为"预计xx-xx返利"
		        if("4497465200170001".equals(rebateStatus) || "4497465200170002".equals(rebateStatus)){
		        	if(StringUtils.isNotBlank(orderMap.get("order_finish_time"))){
			        	preDay=DateHelper.upDateTimeAdd(DateHelper.parseDate(orderMap.get("order_finish_time")),Calendar.DATE,7);
			        	description="预计"+preDay.substring(5, 7)+"月"+preDay.substring(8, 10)+"日"+"返利";
			        	rebateRecordInfo.setRebateStatus(description);
		        	}else{
		        		rebateRecordInfo.setRebateStatus("预计返利");
		        	}
		        }else{
		        	rebateRecordInfo.setRebateStatus(WebTemp.upTempDataOne("sc_define", "define_name", "define_code",rebateStatus));
		        }
		        
		        rebateList.add(rebateRecordInfo);
			}
			
			//返利列表数据
			pcRebateRecord.setRebateRecordList(rebateList);
		}
		
		pcRebateRecord.setPageResults(mPageData.getPageResults());
		
		return pcRebateRecord;
	}

	/**
	 * 金额累加
	 * @param d1
	 * @param d2
	 * @return
	 */
	private double addMoney(double d1, double d2) {
		BigDecimal b1 = new BigDecimal(d1);
		BigDecimal b2 = new BigDecimal(d2);
		return b1.add(b2).doubleValue();
	}

	/**
	 * 扣款明细
	 * @param accountCode
	 * @param inputParam
	 * @return
	 */
	public PcCutPaymentRecordResult ShowPcCutPaymentRecord(String accountCode,
			PcCutPaymentRecordInput inputParam) {
		
		PcCutPaymentRecordResult pcCutPaymentRecord=new PcCutPaymentRecordResult();
		List<PcCutPaymentRecordInfo> cutPaymentRecordList=new ArrayList<PcCutPaymentRecordInfo>();
		DecimalFormat decimalFormat=new DecimalFormat("0.00");
		//账户余额
		String accountWithdrawMoney="0.00";
		//累计扣款
		String totalCutPaymentMoney = "0.00";
		
		//开始时间
		String selStartTime = inputParam.getStartTime();
		//结束时间
		String selEndTime = inputParam.getEndTime();
		
		//统计退货扣款笔数
		int countReturnCutNum = 0;
		//统计退货扣款金额
		double countReturnCutMoney = 0d;
		//统计平台扣减笔数
		int countPlatformCutNum = 0;
		//统计平台扣减金额
		double countPlatformCutMoney = 0d;
		
		//取注册日期作为页面查询时提示的开始日期
		String tipsBeginTime = "";
		String tipTimeSql="select create_time from mc_login_info where member_code in(select member_code from mc_member_info where account_code=:account_code) order by create_time limit 1";
		Map<String, Object> memberMap=DbUp.upTable("mc_login_info").dataSqlOne(tipTimeSql, new MDataMap("account_code",accountCode));
		if(memberMap!=null&&StringUtils.isNotBlank(String.valueOf(memberMap.get("create_time")))){
			tipsBeginTime=memberMap.get("create_time").toString().substring(0, 10);
		}
		pcCutPaymentRecord.setTipsBeginTime(tipsBeginTime);
		//取系统日期作为页面查询时提示的结束日期
		String tipsEndTime = DateUtil.getSysDateString();
		pcCutPaymentRecord.setTipsEndTime(tipsEndTime);
		
		MDataMap acMap=DbUp.upTable("gc_group_account").one("account_code",accountCode);
		if(acMap != null ){
			accountWithdrawMoney = acMap.get("account_withdraw_money");
		}
		pcCutPaymentRecord.setAccountWithdrawMoney(accountWithdrawMoney);
		
		String sWhereString = " account_code = '"+accountCode+"'";
		Object sumCutMoney = DbUp.upTable("gc_withdraw_log").dataGet("sum(withdraw_money)", sWhereString+" and withdraw_change_type in('4497465200040003','4497465200040011')",null);
		if(sumCutMoney != null){
			totalCutPaymentMoney = String.valueOf(sumCutMoney);
		}
		pcCutPaymentRecord.setTotalCutPaymentMoney(totalCutPaymentMoney);
		
		//汇总数据SQL(不带分页)
		StringBuffer sCountWhereBuf = new StringBuffer();
		sCountWhereBuf.append(" select withdraw_money,withdraw_change_type from  gc_withdraw_log ");
		
		//扣款明细查询条件
		MDataMap whereMap = new MDataMap();
		String sWhere = " account_code ='"+accountCode+"' ";
		sCountWhereBuf.append(" where account_code = '").append(accountCode).append("' ");
		
		if(StringUtils.isNotBlank(selStartTime)){
			sWhere += " and  left(create_time,10) >= '" + selStartTime +"' " ;
			sCountWhereBuf.append(" and  left(create_time,10) >= '").append(selStartTime).append("' ");
		}
		
		if(StringUtils.isNotBlank(selEndTime)){
			sWhere += " and  left(create_time,10) <= '" + selEndTime +"' " ;
			sCountWhereBuf.append(" and  left(create_time,10) <= '").append(selEndTime).append("' ");
		}
		
		String sWhereInField = "withdraw_change_type";
		String sWhereInFieldVal = "4497465200040003,4497465200040011";
		sCountWhereBuf.append(" and  withdraw_change_type in('4497465200040003','4497465200040011') ");
		
		//汇总数据
		List<Map<String, Object>> countRecordList=DbUp.upTable("gc_withdraw_log").dataSqlList(sCountWhereBuf.toString(), new MDataMap());
		if(countRecordList != null && countRecordList.size()>0){
			for(int i = 0;i<countRecordList.size();i++){
				String withdrawMoney = String.valueOf(countRecordList.get(i).get("withdraw_money"));
				String withdrawChangeType = String.valueOf(countRecordList.get(i).get("withdraw_change_type"));
				
				if("4497465200040003".equals(withdrawChangeType)){
					countReturnCutNum = countReturnCutNum+1;
					countReturnCutMoney = addMoney(countReturnCutMoney,Double.parseDouble(withdrawMoney));
				}else if("4497465200040011".equals(withdrawChangeType)){
					countPlatformCutNum = countPlatformCutNum +1;
					countPlatformCutMoney = addMoney(countPlatformCutMoney,Double.parseDouble(withdrawMoney));
				}
			}
			
			//退货扣款笔数
			pcCutPaymentRecord.setCountReturnGoodsCutNum(String.valueOf(countReturnCutNum));
			//退货扣款金额
			pcCutPaymentRecord.setCountReturnGoodsCutMoney(String.valueOf(decimalFormat.format(countReturnCutMoney)));
			//平台扣减笔数
			pcCutPaymentRecord.setCountPlatformCutNum(String.valueOf(countPlatformCutNum));
			//平台扣减金额
			pcCutPaymentRecord.setCountPlatformCutMoney(String.valueOf(decimalFormat.format(countPlatformCutMoney)));
		}
		
		//扣款记录
		MPageData mPageData= DataPaging.upPageDataQueryIn("gc_withdraw_log", "", "create_time desc",sWhere, whereMap, inputParam.getPageOption(),sWhereInField,sWhereInFieldVal);
		
		List<MDataMap> cutPaymentList=mPageData.getListData();
		if(cutPaymentList!=null&&cutPaymentList.size()>0){
			for(MDataMap cutMap:cutPaymentList){
				PcCutPaymentRecordInfo cutPayMentInfo = new PcCutPaymentRecordInfo();
				
		        //扣款时间
				cutPayMentInfo.setCreateTime(cutMap.get("create_time"));
		        //扣款金额
				String withdrawMoney = cutMap.get("withdraw_money");
				cutPayMentInfo.setWithdrawMoney(withdrawMoney);
		        //扣款原因
				String withdrawChangeType = cutMap.get("withdraw_change_type");
				if("4497465200040003".equals(withdrawChangeType)){
					cutPayMentInfo.setWithdrawChangeType("退货扣款");
				}else if("4497465200040011".equals(withdrawChangeType)){
					cutPayMentInfo.setWithdrawChangeType("平台扣款");
				}
				cutPaymentRecordList.add(cutPayMentInfo);
			}
			
			pcCutPaymentRecord.setCutPaymentRecordList(cutPaymentRecordList);
		}
		
		pcCutPaymentRecord.setPageResults(mPageData.getPageResults());
		
		return pcCutPaymentRecord;
	}

	/**
	 * 账户明细
	 * @param accountCode
	 * @param inputParam
	 * @return
	 */
	public PcAccountRecordResult showPcAccountRecord(String accountCode,
			PcAccountRecordInput inputParam) {
		
		DecimalFormat decimalFormat=new DecimalFormat("0.00");
		//返回结果
		PcAccountRecordResult pcAccountResult = new PcAccountRecordResult();
		List<PcAccountRecordInfo> accountList=new ArrayList<PcAccountRecordInfo>();
		
		//累计入账
		double totalInComeMoney = 0d;
		//累计提现
		double totalWithdrawMoney = 0d;
		//累计扣款
		double totalCutPaymentMoney = 0d;
		//累计购物
		double totalPayMoney = 0d;
		
		//统计入账笔数
		int countInComeNum = 0;
		//统计入账金额
		double countInComeMoney = 0d;
		//统计成功提现笔数
		int countWithdrawNum = 0;
		//统计成功提现金额
		double countWithdrawMoney = 0d;
		//统计扣款笔数
		int countCutPaymentNum = 0;
		//统计扣款金额
		double countCutPaymentMoney = 0d;
		//统计购物笔数
		int conutPayNum = 0;
		//统计购物金额
		double conutPayMoney = 0d;
		
		//类型(1:入账 2：提现 3：扣款 4：购物)
		String selType = inputParam.getSearchType();
		//开始时间
		String selStartTime = inputParam.getStartTime();
		//结束时间
		String selEndTime = inputParam.getEndTime();
		
		//取注册日期作为页面查询时提示的开始日期
		String tipsBeginTime = "";
		String tipTimeSql="select create_time from mc_login_info where member_code in(select member_code from mc_member_info where account_code=:account_code) order by create_time limit 1";
		Map<String, Object> memberMap=DbUp.upTable("mc_login_info").dataSqlOne(tipTimeSql, new MDataMap("account_code",accountCode));
		if(memberMap!=null&&StringUtils.isNotBlank(String.valueOf(memberMap.get("create_time")))){
			tipsBeginTime=memberMap.get("create_time").toString().substring(0, 10);
		}
		pcAccountResult.setTipsBeginTime(tipsBeginTime);
		//取系统日期作为页面查询时提示的结束日期
		String tipsEndTime = DateUtil.getSysDateString();
		pcAccountResult.setTipsEndTime(tipsEndTime);
		
		//获取累计信息
		String sWhereString = " account_code = '"+accountCode+"'";
		
		//累计入账
		Object tInComeMoney = DbUp.upTable("gc_withdraw_log").dataGet("sum(withdraw_money)", sWhereString+" and withdraw_change_type in('4497465200040001','4497465200040009','4497465200040010','4497465200040012')",null);
		if(tInComeMoney != null){
			totalInComeMoney = Double.parseDouble(String.valueOf(tInComeMoney));
		}
		
		//累计成功提现
//		Object tWithdrawMoney = DbUp.upTable("gc_withdraw_log").dataGet("sum(withdraw_money)", sWhereString+" and withdraw_change_type in('4497465200040002')",null);
//		if(tWithdrawMoney != null){
//			totalWithdrawMoney = Double.parseDouble(String.valueOf(tWithdrawMoney));
//		}
		Object arrivalMoney = DbUp.upTable("gc_pay_order_info").dataGet("sum(withdraw_money)", "", new MDataMap("account_code",accountCode,"pay_status","4497465200070004"));
		if(arrivalMoney != null){
			totalWithdrawMoney = Double.parseDouble(String.valueOf(arrivalMoney));
		}
		
		//累计扣款
		Object tCutPaymentMoney = DbUp.upTable("gc_withdraw_log").dataGet("sum(withdraw_money)", sWhereString+" and withdraw_change_type in('4497465200040003','4497465200040011')",null);
		if(tCutPaymentMoney != null){
			totalCutPaymentMoney = Double.parseDouble(String.valueOf(tCutPaymentMoney));
		}
		
		//累计购物
		Object tPayMoney = DbUp.upTable("gc_withdraw_log").dataGet("sum(withdraw_money)", sWhereString+" and withdraw_change_type ='4497465200040008'",null);
		if(tPayMoney != null){
			totalPayMoney = Double.parseDouble(String.valueOf(tPayMoney));
		}
		
		//计算人工调整( 金额>0时 为入账;金额<0时 为扣款 )
		double tempAdd = 0d;
		double tempSubs = 0d;
		List<MDataMap> manualSettingList=DbUp.upTable("gc_withdraw_log").queryByWhere("account_code",accountCode,"withdraw_change_type","4497465200040004");
		if(manualSettingList != null && manualSettingList.size() >0){
			for(int i = 0;i <manualSettingList.size();i++){
				String withdrawMoney = manualSettingList.get(i).get("withdraw_money");
				BigDecimal bMoney=BigDecimal.ZERO;
				bMoney = new BigDecimal(withdrawMoney);
				if(bMoney.compareTo(BigDecimal.ZERO) < 0){
					tempSubs = addMoney(tempSubs,Double.parseDouble(withdrawMoney));
				}else{
					tempAdd = addMoney(tempAdd,Double.parseDouble(withdrawMoney));
				}
			}
		}
		
		//累计入账=totalInComeMoney+人工调整类型中 金额>0的金额
		pcAccountResult.setTotalInComeMoney(String.valueOf(decimalFormat.format(addMoney(totalInComeMoney,tempAdd))));
		//累计提现=totalWithdrawMoney
		pcAccountResult.setTotalWithdrawMoney(String.valueOf(decimalFormat.format(totalWithdrawMoney)));
		//累计扣款=totalCutPaymentMoney+人工调整类型中 金额<0的金额
		pcAccountResult.setTotalCutPaymentMoney(String.valueOf(decimalFormat.format(addMoney(totalCutPaymentMoney,tempSubs))));
		//累计购物=totalPayMoney
		pcAccountResult.setTotalPayMoney(String.valueOf(decimalFormat.format(totalPayMoney)));
		
		//汇总数据SQL(不带分页)
		StringBuffer sCountWhereBuf = new StringBuffer();
		sCountWhereBuf.append(" select withdraw_money,withdraw_change_type,change_codes,create_time from  gc_withdraw_log ");
		sCountWhereBuf.append(" where 1=1 ");
		
		//为了查询人工调整数据这都SQL必须放在前面 然后在拼接其他的条件 否则结果数据错误
		if("1".equals(selType)){
			//4497465200040004 人工调整  金额>0时 为入账
			sCountWhereBuf.append(" and (account_code = '").append(accountCode).append("' and withdraw_change_type='4497465200040004' and withdraw_money>0 ");
			if(StringUtils.isNotBlank(selStartTime)){
				sCountWhereBuf.append(" and  left(create_time,10) >= '").append(selStartTime).append("' ");
			}
			if(StringUtils.isNotBlank(selEndTime)){
				sCountWhereBuf.append(" and  left(create_time,10) <= '").append(selEndTime).append("' ");
			}
			sCountWhereBuf.append(" ) or account_code ='").append(accountCode).append("' ");
			
		}else if("3".equals(selType)){
			//4497465200040004 人工调整  金额<0时 为扣款
			sCountWhereBuf.append(" and (account_code = '").append(accountCode).append("' and withdraw_change_type='4497465200040004' and withdraw_money<0 ");
			if(StringUtils.isNotBlank(selStartTime)){
				sCountWhereBuf.append(" and  left(create_time,10) >= '").append(selStartTime).append("' ");
			}
			if(StringUtils.isNotBlank(selEndTime)){
				sCountWhereBuf.append(" and  left(create_time,10) <= '").append(selEndTime).append("' ");
			}
			sCountWhereBuf.append(" ) or account_code ='").append(accountCode).append("' ");
			
		}else{
			sCountWhereBuf.append(" and account_code ='").append(accountCode).append("' ");
			if(StringUtils.isNotBlank(selStartTime)){
				sCountWhereBuf.append(" and  left(create_time,10) >= '").append(selStartTime).append("' ");
			}
			if(StringUtils.isNotBlank(selEndTime)){
				sCountWhereBuf.append(" and  left(create_time,10) <= '").append(selEndTime).append("' ");
			}
		}

		if("1".equals(selType)){
			//入账(返利;人工加钱;任务奖励)
			sCountWhereBuf.append(" and  withdraw_change_type in ('4497465200040001','4497465200040009','4497465200040010','4497465200040012') ");
			if(StringUtils.isNotBlank(selStartTime)){
				sCountWhereBuf.append(" and  left(create_time,10) >= '").append(selStartTime).append("' ");
			}
			if(StringUtils.isNotBlank(selEndTime)){
				sCountWhereBuf.append(" and  left(create_time,10) <= '").append(selEndTime).append("' ");
			}
		}else if("2".equals(selType)){
			//提现(用户提现;提现单审核失败;提现单支付失败;提现账户异常数据)
			sCountWhereBuf.append(" and  withdraw_change_type in ('4497465200040002','4497465200040005','4497465200040006','4497465200040007') ");
		}else if("3".equals(selType)){
			//扣款(订单退换货;支付退款;人工减钱)
			sCountWhereBuf.append(" and  withdraw_change_type in ('4497465200040003','4497465200040011') ");
			if(StringUtils.isNotBlank(selStartTime)){
				sCountWhereBuf.append(" and  left(create_time,10) >= '").append(selStartTime).append("' ");
			}
			if(StringUtils.isNotBlank(selEndTime)){
				sCountWhereBuf.append(" and  left(create_time,10) <= '").append(selEndTime).append("' ");
			}
		}else if("4".equals(selType)){
			//购物(支付)
			sCountWhereBuf.append(" and  withdraw_change_type in ('4497465200040008') ");

		}
		sCountWhereBuf.append(" order by create_time desc ");
		
		//账户明细记录
		List<Map<String, Object>> acRecordList=DbUp.upTable("gc_withdraw_log").dataSqlList(sCountWhereBuf.toString(), new MDataMap());
		if(acRecordList != null && acRecordList.size()>0){
			
			for(int i = 0;i<acRecordList.size();i++){
				boolean addFlag = true;//添加列表标记 false:不添加 true:添加
				BigDecimal bigMoney=BigDecimal.ZERO;
				String createTime = String.valueOf(acRecordList.get(i).get("create_time"));
				String withdrawMoney = String.valueOf(acRecordList.get(i).get("withdraw_money"));
				bigMoney = new BigDecimal(withdrawMoney);
				String withdrawChangeType = String.valueOf(acRecordList.get(i).get("withdraw_change_type"));
				String changeCodes =  String.valueOf(acRecordList.get(i).get("change_codes"));
				
				PcAccountRecordInfo acInfo = new PcAccountRecordInfo();
				//时间
				acInfo.setCreateTime(createTime);
				//金额,
				acInfo.setMoney(withdrawMoney);
				
				//入账--返利
				if("4497465200040001".equals(withdrawChangeType)){
					acInfo.setType("入账");
					acInfo.setRemark("返利");
					
					countInComeNum = countInComeNum+1;
					countInComeMoney = addMoney(countInComeMoney,Double.parseDouble(withdrawMoney));
				}else if("4497465200040002".equals(withdrawChangeType)){
					acInfo.setType("提现");
					acInfo.setRemark("提现");
					if(StringUtils.isNotBlank(changeCodes) && changeCodes.startsWith("WGS")){
						//判断是否是正在提现的状态
						MDataMap payOrderMap=DbUp.upTable("gc_pay_order_info").one("pay_order_code", changeCodes);
						if(payOrderMap != null){
							String payStatus = payOrderMap.get("pay_status");
							//过滤提现不成功的数据
							if(!"4497465200070004".equals(payStatus)){
								addFlag = false;
							}else{
								//提现成功笔数和金额累加
								countWithdrawNum = countWithdrawNum+1;
								countWithdrawMoney = addMoney(countWithdrawMoney,Double.parseDouble(withdrawMoney));
							}
						}
					}
					
				}else if("4497465200040003".equals(withdrawChangeType)){
					acInfo.setType("扣款");
					acInfo.setRemark("退换货");
					
					//扣款--退换货
					countCutPaymentNum = countCutPaymentNum+1;
					countCutPaymentMoney = addMoney(countCutPaymentMoney,Double.parseDouble(withdrawMoney));
				}else if("4497465200040004".equals(withdrawChangeType)){
					if(bigMoney.compareTo(BigDecimal.ZERO) < 0){
						if(!"1".equals(selType)){
							acInfo.setType("扣款");
							acInfo.setRemark("平台扣减");
							
							//扣款--平台扣减
							countCutPaymentNum = countCutPaymentNum+1;
							countCutPaymentMoney = addMoney(countCutPaymentMoney,Double.parseDouble(withdrawMoney));
						}
					}else{
						if(!"3".equals(selType)){
							acInfo.setType("入账");
							acInfo.setRemark("平台赠送");
							
							//入账--平台赠送
							countInComeNum = countInComeNum+1;
							countInComeMoney = addMoney(countInComeMoney,Double.parseDouble(withdrawMoney));
						}
					}
				}else if("4497465200040005".equals(withdrawChangeType)||"4497465200040006".equals(withdrawChangeType)||"4497465200040007".equals(withdrawChangeType)){
					acInfo.setType("提现");
					acInfo.setRemark("提现失败");
					acInfo.setLabel("提现失败");
				}else if("4497465200040008".equals(withdrawChangeType)){
					acInfo.setType("购物");
					String remark = "";
					String typeFrom = "";//来源
					if(StringUtils.isNotBlank(changeCodes)){
						MDataMap vpayMap=DbUp.upTable("gc_vpay_order").one("trade_code",changeCodes);
						if(vpayMap != null){
							String businessCode = vpayMap.get("business_code");
							if(StringUtils.isNotBlank(businessCode)){
								if(businessCode.startsWith("SI")){
									MDataMap appMap=DbUp.upTable("uc_appinfo").one("app_code",businessCode);
									if(appMap != null){
										typeFrom = appMap.get("app_name");
										remark = "支付 " + typeFrom;
									}
								}else if(businessCode.startsWith("APPM")){
									MDataMap appMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",businessCode);
									if(appMap != null){
										typeFrom = appMap.get("app_name");
										remark = "支付 " + typeFrom;
									}
								}else if(businessCode.startsWith("SG")){
									MDataMap appMap=DbUp.upTable("gc_trader_info").one("trader_code",businessCode);
									if(appMap != null){
										typeFrom = appMap.get("trader_name");
										remark = "支付 " + typeFrom;
									}
								}
							}
						}
					}
					acInfo.setRemark(remark);
					
					//购物--支付
					conutPayNum = conutPayNum+1;
					conutPayMoney = addMoney(conutPayMoney,Double.parseDouble(withdrawMoney));
					
				}else if("4497465200040009".equals(withdrawChangeType)){
					
					acInfo.setType("入账");
					String remark = "";
					String typeFrom = "";//来源
					if(StringUtils.isNotBlank(changeCodes)){
						MDataMap vpayMap=DbUp.upTable("gc_vpay_order").one("trade_code",changeCodes);
						if(vpayMap != null){
							String businessCode = vpayMap.get("business_code");
							if(StringUtils.isNotBlank(businessCode)){
								if(businessCode.startsWith("SI")){
									MDataMap appMap=DbUp.upTable("uc_appinfo").one("app_code",businessCode);
									if(appMap != null){
										typeFrom = appMap.get("app_name");
										remark = "退款 " + typeFrom;
									}
								}else if(businessCode.startsWith("APPM")){
									MDataMap appMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",businessCode);
									if(appMap != null){
										typeFrom = appMap.get("app_name");
										remark = "退款 " + typeFrom;
									}
								}else if(businessCode.startsWith("SG")){
									MDataMap appMap=DbUp.upTable("gc_trader_info").one("trader_code",businessCode);
									if(appMap != null){
										typeFrom = appMap.get("trader_name");
										remark = "退款 " + typeFrom;
									}
								}
							}
						}
					}
					acInfo.setRemark(remark);
					
					//入账--退款
					countInComeNum = countInComeNum+1;
					countInComeMoney = addMoney(countInComeMoney,Double.parseDouble(withdrawMoney));
					
				}else if("4497465200040010".equals(withdrawChangeType)){
					acInfo.setType("入账");
					acInfo.setRemark("平台赠送");
					
					//入账--平台赠送
					countInComeNum = countInComeNum+1;
					countInComeMoney = addMoney(countInComeMoney,Double.parseDouble(withdrawMoney));
				}else if("4497465200040011".equals(withdrawChangeType)){
					acInfo.setType("扣款");
					acInfo.setRemark("平台扣减");
					
					//扣款--平台扣减
					countCutPaymentNum = countCutPaymentNum+1;
					countCutPaymentMoney = addMoney(countCutPaymentMoney,Double.parseDouble(withdrawMoney));
				}else if("4497465200040012".equals(withdrawChangeType)){
					acInfo.setType("入账");
					acInfo.setRemark("任务奖励");
					
					//入账--任务奖励
					countInComeNum = countInComeNum+1;
					countInComeMoney = addMoney(countInComeMoney,Double.parseDouble(withdrawMoney));
				}
				if(addFlag){
					accountList.add(acInfo);
				}
				
			}
			
			//入账笔数
			pcAccountResult.setCountInComeNum(String.valueOf(countInComeNum));
			//入账金额
			pcAccountResult.setCountInComeMoney(String.valueOf(decimalFormat.format(countInComeMoney)));
			
			//提现笔数
			pcAccountResult.setCountWithdrawNum(String.valueOf(countWithdrawNum));
			//提现金额
			pcAccountResult.setCountWithdrawMoney(String.valueOf(decimalFormat.format(countWithdrawMoney)));
			
			//扣款笔数
			pcAccountResult.setCountCutPaymentNum(String.valueOf(countCutPaymentNum));
			//扣款金额
			pcAccountResult.setCountCutPaymentMoney(String.valueOf(decimalFormat.format(countCutPaymentMoney)));
			
			//购物笔数
			pcAccountResult.setConutPayNum(String.valueOf(conutPayNum));
			//购物金额
			pcAccountResult.setConutPayMoney(String.valueOf(decimalFormat.format(conutPayMoney)));
			
			//只包含最终状态(提现成功和提现失败)的数据列表,并虚拟分页
			if(accountList != null && accountList.size() > 0){
				int totalDataCount = accountList.size();
				int pageIndex = inputParam.getPageOption().getOffset()+1;
				if(pageIndex <= 0){
					pageIndex = 1;
				}
				int pageSize = inputParam.getPageOption().getLimit();
				if(pageSize <= 0){
					pageSize = 10;
				}
				HashMap<String, Object> pageMap= new HashMap<String, Object>();
				List<PcAccountRecordInfo> newCounRecordList = new ArrayList<PcAccountRecordInfo>();
				PcVirtualPager<PcAccountRecordInfo> pager = new PcVirtualPager<PcAccountRecordInfo>(totalDataCount,pageIndex,pageSize);
				pageMap.put("dataIndex", pager.getSkipResults());
				pageMap.put("pageSize", pager.getMaxResults());
				//虚拟分页
				newCounRecordList = ShowVirtualAccountList(pageMap,accountList);
				//列表数据
				pcAccountResult.setAccountRecordList(newCounRecordList);
				
				MPageData mVPageData = new MPageData();
				PageResults pageResults = new PageResults();
				//总条数
				pageResults.setTotal(totalDataCount);
				//返回的条数
				pageResults.setCount(newCounRecordList.size());
				//判断是否还有更多数据
				pageResults.setMore((pageSize * (pageIndex-1) + pageResults.getCount()) < pageResults.getTotal() ? 1 : 0);
				
				mVPageData.setPageResults(pageResults);
				
				pcAccountResult.setPageResults(mVPageData.getPageResults());
			}
		}
		
		return pcAccountResult;
	}

	/**
	 * 消费明细
	 * @param accountCode
	 * @param inputParam
	 * @return
	 */
	public PcConsumeRecordResult showPcConsumeRecord(String accountCode,
			PcConsumeRecordInput inputParam) {
		
		DecimalFormat decimalFormat=new DecimalFormat("0.00");
		String currentMonth = DateHelper.upMonth(FormatHelper.upDateTime());//本月
		
		//返回结果
		PcConsumeRecordResult pcConsumeResult = new PcConsumeRecordResult();
		List<PcConsumeRecordInfo> consumeList=new ArrayList<PcConsumeRecordInfo>();
		
		//累计消费
		double totalConsumeMoney = 0d;
		//本月消费
		double currentMonthConsumeMoney = 0d;
		//本月活跃好友数
		int currentMonthActiveNum = 0;
		//本月升级还需消费
		BigDecimal gapConsume=BigDecimal.ZERO;
		//汇总统计--消费总计
		double countConsumeMoney = 0d;
		//查询条件
		String selStartYearMonth = inputParam.getStartYearMonth();
		String selEndYearMonth = inputParam.getEndYearMonth();
		
		//获取累计消费
//		String sWhereString = " account_code = '"+accountCode+"'";
//		Object tSumConsume = DbUp.upTable("gc_active_month").dataGet("ifnull(sum(sum_consume),0)", sWhereString,null);
//		if(tSumConsume != null){
//			totalConsumeMoney = Double.parseDouble(String.valueOf(tSumConsume));
//		}
		
		//取注册日期作为页面查询时提示的开始日期
		String tipsBeginTime = "";
		//取系统日期作为页面查询时提示的结束日期
		String tipsEndTime = DateUtil.getSysDateString();
		pcConsumeResult.setTipsEndTime(tipsEndTime);
		
		MDataMap queryMap=new MDataMap();
		queryMap.put("accountCode", accountCode);
		String consumeSql=" select ifnull(sum(consume_money),0) as totalConsume from gc_active_log where account_code=:accountCode ";
		Map<String, Object> totalConsumeMap= DbUp.upTable("gc_active_log").dataSqlOne(consumeSql, queryMap);
		if(totalConsumeMap!=null){
			totalConsumeMoney = Double.parseDouble(totalConsumeMap.get("totalConsume").toString());
		}
		pcConsumeResult.setTotalConsumeMoney(String.valueOf(decimalFormat.format(totalConsumeMoney)));
		
		//本月消费,本月活跃好友数
		MDataMap mActiveMap = DbUp.upTable("gc_active_month").oneWhere(
				"sum_consume,sum_member", "", "", "account_code",
				accountCode, "active_month", currentMonth);
		
		if (mActiveMap != null) {
			currentMonthConsumeMoney = Double.parseDouble(mActiveMap.get("sum_consume"));
			currentMonthActiveNum = Integer.parseInt(mActiveMap.get("sum_member"));
		}
		pcConsumeResult.setCurrentMonthConsumeMoney(String.valueOf(decimalFormat.format(currentMonthConsumeMoney)));
		pcConsumeResult.setCurrentMonthActiveNum(String.valueOf(currentMonthActiveNum));
		
		//本月升级还需消费
		MDataMap accountMap = DbUp.upTable("gc_group_account").one(
				"account_code", accountCode);
		String sLevelCode = GroupConst.DEFAULT_LEVEL_CODE;
		if(accountMap!=null){
			sLevelCode=accountMap.get("account_level");
		}
		
		MDataMap mLevelMap = WebTemp.upTempDataMap("gc_group_level", "",
				"level_code", sLevelCode);
		
		//土豪级别时，特殊处理
		MDataMap topLevelMap=WebTemp.upTempDataMap("gc_group_level", "", "level_code","4497465200010004");
		if(sLevelCode.equals(GroupConst.TOP_LEVEL_CODE)){
			gapConsume=new BigDecimal(topLevelMap.get("upgrade_consume")).subtract(new BigDecimal(currentMonthConsumeMoney));
		}
		else{
			gapConsume = new BigDecimal(
					mLevelMap.get("upgrade_consume")).subtract(new BigDecimal(currentMonthConsumeMoney));
		}
		
		if (gapConsume.compareTo(BigDecimal.ZERO) < 0) {
			gapConsume = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		}
		pcConsumeResult.setNextLevelGapConsume(String.valueOf(gapConsume));
		
		//消费记录
		MDataMap inMap=new MDataMap();
		inMap.put("accountCode", accountCode);
		StringBuffer sWhereBuf = new StringBuffer();
		sWhereBuf.append(" select * from gc_active_month ");
		sWhereBuf.append(" where account_code =:accountCode ");

		if(StringUtils.isNotBlank(selStartYearMonth)){
			sWhereBuf.append(" and  active_month >=:searchStartYM ");
			inMap.put("searchStartYM", selStartYearMonth);
		}
		
		if(StringUtils.isNotBlank(selEndYearMonth)){
			sWhereBuf.append(" and  active_month <=:searchEndYM ");
			inMap.put("searchEndYM", selEndYearMonth);
		}
		sWhereBuf.append(" order by  create_time desc ");
		
		Map<String, String> monthConsumeMap=new HashMap<String, String>();
		Map<String, String> monthActiveNumMap=new HashMap<String, String>();
		List<Map<String, Object>> monthDataList = new ArrayList<Map<String, Object>>();
		
//		MPageData mPageData= DataPaging.upPageData("gc_active_month", "", "-create_time",sWhereBuf.toString(),null,inputParam.getPageOption());
		
//		List<MDataMap> mConsumeList=mPageData.getListData();
//		if(mConsumeList!=null&&mConsumeList.size()>0){
//			for(MDataMap mMap:mConsumeList){
//				
//				String activeMonth = mMap.get("active_month");
//				String monthConsume = mMap.get("sum_consume");
//				String monthMember = mMap.get("sum_member");
//				monthConsumeMap.put(activeMonth, monthConsume);
//				monthActiveNumMap.put(activeMonth, monthMember);
//			}
//		}
		
		monthDataList=DbUp.upTable("gc_active_month").dataSqlList(sWhereBuf.toString(), inMap);
		if(monthDataList != null && monthDataList.size() > 0){
			for(int i = 0;i < monthDataList.size();i++){
				String activeMonth = String.valueOf(monthDataList.get(i).get("active_month"));
				String monthConsume = String.valueOf(monthDataList.get(i).get("sum_consume"));
				String monthMember = String.valueOf(monthDataList.get(i).get("sum_member"));
				monthConsumeMap.put(activeMonth, monthConsume);
				monthActiveNumMap.put(activeMonth, monthMember);
			}
		}
		
		//当前月
		String beginMonth="";
		String endMonth=DateHelper
				.upMonth(FormatHelper.upDateTime());
		//取注册日期
		String memberSql="select create_time from mc_login_info where member_code in(select member_code from mc_member_info where account_code=:account_code) order by create_time limit 1";
		Map<String, Object> memberMap=DbUp.upTable("mc_login_info").dataSqlOne(memberSql, new MDataMap("account_code",accountCode));
		if(memberMap!=null&&StringUtils.isNotBlank(String.valueOf(memberMap.get("create_time")))){
			beginMonth=memberMap.get("create_time").toString().substring(0, 7);
			tipsBeginTime=memberMap.get("create_time").toString().substring(0, 10);
		}
		//页面查询时提示的开始日期
		pcConsumeResult.setTipsBeginTime(tipsBeginTime);
		
		if(StringUtils.isNotBlank(beginMonth)){
			
			List<String> monthList=DateHelper.getMonthList(beginMonth, endMonth);
			Collections.reverse(monthList);
			
			//每月等级推算
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
			Map<String,String> monthLevelMap=new HashMap<String, String>();
			monthLevelMap.put(currentMonth,sLevelCode);
			//由后往前推等级
			for(String monthsString:monthList){
				if(monthsString.equals(currentMonth)){
					continue;
				}
				String nextMonth="";
				MDataMap monthMap=DbUp.upTable("gc_level_log").oneWhere("", "-create_time,-zid", "account_code=:account_code and left(create_time,7)=:month", "account_code",accountCode,"month",monthsString);
			    if(monthMap!=null){
			    	monthLevelMap.put(monthsString, monthMap.get("current_member_level"));
			    }else{
			    	 try {
						Date date = format.parse(monthsString);
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(date);
						calendar.add(Calendar.MONTH, 1);
						nextMonth=format.format(calendar.getTime());
						MDataMap nextMap=DbUp.upTable("gc_level_log").oneWhere("", "create_time", "account_code=:account_code and left(create_time,7)=:nextmonth", "account_code",accountCode,"nextmonth",nextMonth);
						if(nextMap!=null){
							monthLevelMap.put(monthsString, nextMap.get("last_member_level"));
						}
						else{
							monthLevelMap.put(monthsString, monthLevelMap.get(nextMonth));
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
			    }
			}
			
			//不用组织数据的场景
			boolean searchFlag = true;
			if(StringUtils.isNotBlank(selStartYearMonth) && StringUtils.isNotBlank(selEndYearMonth)){
				//查询开始年月>结束年月，
				if(!DateUtil.compareDate(selStartYearMonth + "-01", selEndYearMonth + "-01")){
					searchFlag = false;
				}
				//开始年月小于注册年月，结束年月小于注册年月
				if(!DateUtil.compareDate(beginMonth + "-01", selStartYearMonth + "-01") && !DateUtil.compareDate(beginMonth + "-01", selEndYearMonth + "-01")){
					searchFlag = false;
				}
				//开始年月大于当前月，结束年月大于当前月
				if(DateUtil.compareDate(endMonth + "-01", selStartYearMonth + "-01") && DateUtil.compareDate(endMonth + "-01", selEndYearMonth + "-01")){
					searchFlag = false;
				}
			}
			
			//开始组织数据
			if(searchFlag){
				//页面选择日期查询的情况，开始年月和结束年月必须同时有且在注册日期和当前月之间
				if(StringUtils.isNotBlank(selStartYearMonth)){
					if(DateUtil.compareDate(beginMonth + "-01", selStartYearMonth + "-01") && DateUtil.compareDate(selStartYearMonth + "-01", endMonth + "-01")){
						beginMonth = selStartYearMonth;
					}
				}
				if(StringUtils.isNotBlank(selEndYearMonth)){
					if(DateUtil.compareDate(beginMonth + "-01", selEndYearMonth + "-01") && DateUtil.compareDate(selEndYearMonth + "-01", endMonth + "-01")){
						endMonth = selEndYearMonth;
					}
				}
				
				List<String> searchMonthList=DateHelper.getMonthList(beginMonth, endMonth);
				Collections.reverse(searchMonthList);
				
				for(String oneMonth:searchMonthList){
					
					PcConsumeRecordInfo consumeInfo = new PcConsumeRecordInfo();
					//年月
					consumeInfo.setYearMonth(oneMonth);
					//当月消费
					String monthConsume = "0.00";
					if(monthConsumeMap.get(oneMonth) != null){
						monthConsume = String.valueOf(monthConsumeMap.get(oneMonth));
					}
					consumeInfo.setMonthConsumeMoney(monthConsume);
					//汇总统计--消费总计
					countConsumeMoney = addMoney(countConsumeMoney,Double.parseDouble(monthConsume));
					//当月活跃好友数
					String monthActive = "0";
					if(monthActiveNumMap.get(oneMonth) != null){
						monthActive =  String.valueOf(monthActiveNumMap.get(oneMonth));
					}
					consumeInfo.setMonthActiveNum(monthActive);
					//当月级别
					String monthLevelName = "";
					if(monthLevelMap.get(oneMonth) != null){
						monthLevelName = WebTemp.upTempDataOne("gc_group_level", "level_name", "level_code",monthLevelMap.get(oneMonth));
					}
					consumeInfo.setMonthLevel(monthLevelName);
					
					consumeList.add(consumeInfo);
				}
			}
			
		}
		
		//虚拟分页
		int totalDataCount = consumeList.size();
		int pageIndex = inputParam.getPageOption().getOffset()+1;
		if(pageIndex <= 0){
			pageIndex = 1;
		}
		int pageSize = inputParam.getPageOption().getLimit();
		if(pageSize <= 0){
			pageSize = 10;
		}
		HashMap<String, Object> pageMap= new HashMap<String, Object>();
		List<PcConsumeRecordInfo> newConsumeList = new ArrayList<PcConsumeRecordInfo>();
		PcVirtualPager<PcConsumeRecordInfo> pager = new PcVirtualPager<PcConsumeRecordInfo>(totalDataCount,pageIndex,pageSize);
		pageMap.put("dataIndex", pager.getSkipResults());
		pageMap.put("pageSize", pager.getMaxResults());
		newConsumeList = ShowVirtualConsumeList(pageMap,consumeList);
		
		pcConsumeResult.setCountConsumeMoney(String.valueOf(decimalFormat.format(countConsumeMoney)));
		pcConsumeResult.setConsumeRecordList(newConsumeList);
		
		MPageData mVPageData = new MPageData();
		PageResults pageResults = new PageResults();
		//总条数
		pageResults.setTotal(totalDataCount);
		//返回的条数
		pageResults.setCount(newConsumeList.size());
		//判断是否还有更多数据
		pageResults.setMore((pageSize * (pageIndex-1) + pageResults.getCount()) < pageResults.getTotal() ? 1 : 0);

		mVPageData.setPageResults(pageResults);
		pcConsumeResult.setPageResults(mVPageData.getPageResults());
		
		return pcConsumeResult;
	}

	/**
	 * 账户明细虚拟分页
	 * @param pageIndex
	 * @param pageSize
	 * @param consumeList
	 * @return
	 */
	private List<PcAccountRecordInfo> ShowVirtualAccountList(HashMap<String, Object> pageMap,List<PcAccountRecordInfo> accountList) {
		
		int marker = 0;
		int dataIndex = (Integer) pageMap.get("dataIndex");
		int pageSize = (Integer) pageMap.get("pageSize");
		List<PcAccountRecordInfo> newList = new ArrayList<PcAccountRecordInfo>();
		if(accountList == null || accountList.size() == 0){
			return newList;
		}
		
		for (int i = 0; i < accountList.size(); i++){
			//取当前指定的index
			if (i >= dataIndex) {
				//取够了条数，就停止终止查询
				if (marker == pageSize) {
					break;
				}else{
					PcAccountRecordInfo d = accountList.get(i);
					newList.add(d);
					marker++;
				}
			}
		}
		
		return newList;
	}
	
	/**
	 * 消费明细虚拟分页
	 * @param pageIndex
	 * @param pageSize
	 * @param consumeList
	 * @return
	 */
	private List<PcConsumeRecordInfo> ShowVirtualConsumeList(HashMap<String, Object> pageMap,List<PcConsumeRecordInfo> consumeList) {
		
		int marker = 0;
		int dataIndex = (Integer) pageMap.get("dataIndex");
		int pageSize = (Integer) pageMap.get("pageSize");
		List<PcConsumeRecordInfo> newList = new ArrayList<PcConsumeRecordInfo>();
		if(consumeList == null || consumeList.size() == 0){
			return newList;
		}
		
		for (int i = 0; i < consumeList.size(); i++){
			//取当前指定的index
			if (i >= dataIndex) {
				//取够了条数，就停止终止查询
				if (marker == pageSize) {
					break;
				}else{
					PcConsumeRecordInfo d = consumeList.get(i);
					newList.add(d);
					marker++;
				}
			}
		}
		
		return newList;
	}

	/**
	 * PC版本获取好友列表
	 * @param accountCode
	 * @param inputParam
	 * @return
	 */
	public PcFriendsListResult ShowPcFriendsList(String accountCode,
			PcFriendsListInput inputParam) {
		
		DecimalFormat decimalFormat=new DecimalFormat("0.00");
		PcFriendsListResult friendsListResult = new PcFriendsListResult();
		List<PcFridensInfo> friendsInfoList=new ArrayList<PcFridensInfo>();
		
		//筛选条件 1:本月活跃 2:一度好友 3:二度好友
		String selectionType = inputParam.getSelectionType();
		
		String sysFormat = "yyyy-MM"; // 年/月
		SimpleDateFormat sFormat = new SimpleDateFormat(sysFormat);
		//系统时间
		java.sql.Timestamp timestamp = DateUtil.getSysDateTimestamp();
		//现在时间
		String nowYM = sFormat.format(timestamp);
		
		PageResults pageResults = new PageResults();
		PageOption pageOption=inputParam.getPageOption();
		int start=pageOption.getLimit() * pageOption.getOffset();
	    int pageLimit=pageOption.getLimit();
	    String limitString="";
	    if (start> -1 && pageLimit > 0) {
			limitString=" limit " + String.valueOf(start) + ","
					+ String.valueOf(pageLimit);
		}
	    
	    //活跃好友数量
	    int activeFriendsNumber = 0;
		//一度好友数量
		int oneFridensNumber = 0;
		//二度好友数量
		int twoFridensNumber = 0;
		
		//活跃好友人数
		List<Map<String, Object>> totalActiveLogList = new ArrayList<Map<String, Object>>();
		MDataMap inMap=new MDataMap("account_code",accountCode,"flag_enable", "1","active_time",nowYM);
		String totalAcSql="select DISTINCT(ac.order_account_code) from gc_active_log ac inner join gc_member_relation re "
				+ "ON ac.order_account_code = re.account_code and ac.account_code=:account_code and ac.order_account_code !=:account_code  and left(ac.active_time,7)=:active_time ";
		totalActiveLogList=DbUp.upTable("gc_active_log").dataSqlList(totalAcSql, inMap);
		if(totalActiveLogList != null && totalActiveLogList.size()>0){
			activeFriendsNumber = totalActiveLogList.size();
		}
		friendsListResult.setActiveFriendsNumber(String.valueOf(activeFriendsNumber));
		
		//一度好友人数
	    List<Map<String, Object>> oneTotalRelationList = new ArrayList<Map<String, Object>>();
		MDataMap in1Map=new MDataMap("parent_code",accountCode,"flag_enable", "1");
		String totalRel1Sql="select account_code  from gc_member_relation where parent_code=:parent_code and flag_enable =:flag_enable ";
		oneTotalRelationList=DbUp.upTable("gc_member_relation").dataSqlList(totalRel1Sql, in1Map);
		if(oneTotalRelationList != null && oneTotalRelationList.size()>0){
			oneFridensNumber = oneTotalRelationList.size();
		}
		friendsListResult.setOneLevelFriendsNumber(String.valueOf(oneFridensNumber));
		
		//二度好友人数
		List<Map<String, Object>> twoTotalRelationList = new ArrayList<Map<String, Object>>();
		MDataMap in2Map=new MDataMap("account_code",accountCode);
		String totalRel2Sql="select account_code from gc_member_relation where parent_code in (select account_code from gc_member_relation where parent_code=:account_code and flag_enable=1) and flag_enable=1";
		twoTotalRelationList=DbUp.upTable("gc_member_relation").dataSqlList(totalRel2Sql, in2Map);
		if(twoTotalRelationList != null && twoTotalRelationList.size()>0){
			twoFridensNumber = twoTotalRelationList.size();
		}
		friendsListResult.setTwoLevelFriendsNumber(String.valueOf(twoFridensNumber));
		
	    //活跃好友列表
	    if("1".equals(selectionType)){
	    	
		    List<Map<String, Object>> activeLogList = new ArrayList<Map<String, Object>>();
	    	
			String acSql="select DISTINCT(ac.order_account_code),ac.relation_level from gc_active_log ac inner join gc_member_relation re "
					+ "ON ac.order_account_code = re.account_code and ac.account_code=:account_code and ac.order_account_code !=:account_code  and left(ac.active_time,7)=:active_time "
					+ "order by re.create_time " + limitString;
			activeLogList=DbUp.upTable("gc_active_log").dataSqlList(acSql, inMap);
	    	
			//总条数
			if(totalActiveLogList != null && totalActiveLogList.size()>0){
				pageResults.setTotal(Integer.valueOf(totalActiveLogList.size()));
			}else{
				pageResults.setTotal(0);
			}
			
			//返回的条数
			pageResults.setCount(activeLogList==null?0:activeLogList.size());

			//判断是否还有更多数据
			pageResults.setMore((pageOption.getLimit() * pageOption.getOffset() + pageResults
							.getCount()) < pageResults.getTotal() ? 1 : 0);
			
			if(activeLogList != null && activeLogList.size()>0){
				for(int l=0;l<activeLogList.size();l++){
					PcFridensInfo ymInfo = new PcFridensInfo();
					String taAccountCode = String.valueOf(activeLogList.get(l).get("order_account_code"));
					String relationLevel = String.valueOf(activeLogList.get(l).get("relation_level"));
					
					//用户编号
					String memberCode = "";
					MDataMap membCodeMapGp=DbUp.upTable("mc_member_info").one("account_code",taAccountCode,"manage_code","SI2011");
					if(membCodeMapGp != null){
						memberCode = membCodeMapGp.get("member_code");
						//获取好友头像
						String headSql=" SELECT e.member_code,e.head_icon_url,e.nickname "
								+ " FROM membercenter.mc_extend_info_groupcenter e "
								+ " INNER JOIN membercenter.mc_member_info m ON e.member_code = m.member_code AND m.manage_code = 'SI2011' AND m.account_code ='"+ taAccountCode +"'";
						
						List<Map<String, Object>> aListMap=DbUp.upTable("mc_member_info").dataSqlList(headSql, new MDataMap());
						
						if(aListMap != null && aListMap.size() > 0){
							String headIconUrl = String.valueOf(aListMap.get(0).get("head_icon_url"));								
							ymInfo.setHeadIconUrl(headIconUrl);
						}
					}else{
						MDataMap membCodeMap=DbUp.upTable("mc_member_info").one("account_code",taAccountCode);
						if(membCodeMap != null){
							memberCode = membCodeMap.get("member_code");
						}
					}
					
					//好友等级
					String levelName = "";
					String acLevelSql = "SELECT le.level_name FROM gc_group_level le INNER JOIN gc_group_account  ac WHERE le.level_code = ac.account_level AND ac.account_code =:account_code";
					MDataMap acLevelMap = new MDataMap();
					acLevelMap.put("account_code", taAccountCode);
					Map<String, Object> levelNameMap = DbUp.upTable("gc_group_level").dataSqlOne(acLevelSql, acLevelMap);
					if(levelNameMap != null){
						levelName = String.valueOf(levelNameMap.get("level_name"));
					}
					ymInfo.setFridenLevel(levelName);
					
					//好友关系级别
					ymInfo.setRelationLevel(relationLevel);
					
					//好友手机号
					String fridenMobile = "";
					MDataMap mUserMap = DbUp.upTable("mc_login_info").one("member_code", memberCode);
					if(mUserMap != null){
						fridenMobile = mUserMap.get("login_name");
					}
					if(StringUtils.isNotBlank(fridenMobile)){
						fridenMobile = fridenMobile.substring(0, 3) + "****" + fridenMobile.substring(7);
					}
					ymInfo.setFridenMobile(fridenMobile);

					//一度好友本月返利金额
					if("1".equals(relationLevel)){
						//查询TA的一度好友(一度好友的一度好友)
						String taSql1="SELECT re.parent_code,re.account_code FROM gc_member_relation re WHERE re.parent_code = '"
								+ taAccountCode +"' ";
						List<Map<String, Object>> oneFridenListMap=DbUp.upTable("gc_member_relation").dataSqlList(taSql1, new MDataMap());
						
						//TA的一度好友人数
						int oneFridenCount = oneFridenListMap.size();
						List<String> taOneAdd = new ArrayList<String>();
						if(oneFridenListMap != null && oneFridenCount > 0){
							for(int i = 0;i<oneFridenCount;i++){
								//TA的所有一度好友
								taOneAdd.add("'"+oneFridenListMap.get(i).get("account_code")+"'");
							}
						}
						
						//TA的本月返利
						String taMonthRebate = GetTaMonthRebate(accountCode,taAccountCode,nowYM);
						//TA的一度好友本月返利
						String taOneFridendMonthRebate = "0";
						taOneFridendMonthRebate = GetFridendMonthRebate(accountCode,taOneAdd,nowYM);
						//本月返利 =TA的本月返利+TA的一度好友本月返利
						float monthRebateMoney = 0;
						monthRebateMoney = Float.parseFloat(taMonthRebate) + Float.parseFloat(taOneFridendMonthRebate);
						String strMonthRebateMoney = String.valueOf(decimalFormat.format(monthRebateMoney));
						
						ymInfo.setActiveMonthRebateMoney(strMonthRebateMoney);
					}else if("2".equals(relationLevel)){
						//TA的本月返利
						String taMonthRebate = GetTaMonthRebate(accountCode,taAccountCode,nowYM);
						ymInfo.setActiveMonthRebateMoney(taMonthRebate);
					}
					
					friendsInfoList.add(ymInfo);
				}
			}
	    }else if("2".equals(selectionType)){
	    	
	    	//一度好友列表
    		List<Map<String, Object>> oneFridendsList = new ArrayList<Map<String, Object>>();
			//一度好友信息
			String relSql="select account_code from gc_member_relation where parent_code=:parent_code and flag_enable =:flag_enable order by create_time "+limitString;
			oneFridendsList=DbUp.upTable("gc_member_relation").dataSqlList(relSql, in1Map);
			
			//一度好友总条数
			if(oneTotalRelationList != null && oneTotalRelationList.size()>0){
				pageResults.setTotal(Integer.valueOf(oneTotalRelationList.size()));
			}else{
				pageResults.setTotal(0);
			}
			
			//返回的条数
			pageResults.setCount(oneFridendsList==null?0:oneFridendsList.size());
			//判断是否还有更多数据
			pageResults.setMore((pageOption.getLimit() * pageOption.getOffset() + pageResults
							.getCount()) < pageResults.getTotal() ? 1 : 0);
    		
			if(oneFridendsList != null && oneFridendsList.size()>0){
				for(int i=0;i<oneFridendsList.size();i++){
					
					PcFridensInfo oneInfo = new PcFridensInfo();
					String taAccountCode = String.valueOf(oneFridendsList.get(i).get("account_code"));
					
					//用户编号
					String memberCode = "";
					MDataMap membCodeMapGp=DbUp.upTable("mc_member_info").one("account_code",taAccountCode,"manage_code","SI2011");
					if(membCodeMapGp != null){
						memberCode = membCodeMapGp.get("member_code");
						//获取好友头像
						String headSql=" SELECT e.member_code,e.head_icon_url,e.nickname "
								+ " FROM membercenter.mc_extend_info_groupcenter e "
								+ " INNER JOIN membercenter.mc_member_info m ON e.member_code = m.member_code AND m.manage_code = 'SI2011' AND m.account_code ='"+ taAccountCode +"'";
						
						List<Map<String, Object>> aListMap=DbUp.upTable("mc_member_info").dataSqlList(headSql, new MDataMap());
						
						if(aListMap != null && aListMap.size() > 0){
							String headIconUrl = String.valueOf(aListMap.get(0).get("head_icon_url"));								
							oneInfo.setHeadIconUrl(headIconUrl);
						}
					}else{
						MDataMap membCodeMap=DbUp.upTable("mc_member_info").one("account_code",taAccountCode);
						if(membCodeMap != null){
							memberCode = membCodeMap.get("member_code");
						}
					}
					
					//好友等级
					String levelName = "";
					String acLevelSql = "SELECT le.level_name FROM gc_group_level le INNER JOIN gc_group_account  ac WHERE le.level_code = ac.account_level AND ac.account_code =:account_code";
					MDataMap acLevelMap = new MDataMap();
					acLevelMap.put("account_code", taAccountCode);
					Map<String, Object> levelNameMap = DbUp.upTable("gc_group_level").dataSqlOne(acLevelSql, acLevelMap);
					if(levelNameMap != null){
						levelName = String.valueOf(levelNameMap.get("level_name"));
					}
					oneInfo.setFridenLevel(levelName);
					
					//好友关系级别
					oneInfo.setRelationLevel("1");
					
					//好友手机号
					String fridenMobile = "";
					MDataMap mUserMap = DbUp.upTable("mc_login_info").one("member_code", memberCode);
					if(mUserMap != null){
						fridenMobile = mUserMap.get("login_name");
					}
					if(StringUtils.isNotBlank(fridenMobile)){
						fridenMobile = fridenMobile.substring(0, 3) + "****" + fridenMobile.substring(7);
					}
					oneInfo.setFridenMobile(fridenMobile);
					
					//最近下单时间
					String orderTime = "";
					MDataMap taOrderMap=new MDataMap("acCode",taAccountCode);
					String taOrderSql="select order_code,order_create_time from gc_reckon_order_info where account_code=:acCode order by order_create_time desc ";
					Map<String, Object> taOrdertTimeMap=DbUp.upTable("gc_reckon_order_info").dataSqlOne(taOrderSql, taOrderMap);
					if(taOrdertTimeMap != null){
						orderTime = String.valueOf(taOrdertTimeMap.get("order_create_time"));
					}
					oneInfo.setOrderTime(orderTime);
					
					//好友累计返利金额
					//查询TA的一度好友(一度好友的一度好友)
					String taSql1="SELECT re.parent_code,re.account_code FROM gc_member_relation re WHERE re.parent_code = '"
							+ taAccountCode +"' ";
					List<Map<String, Object>> taOneFridenListMap=DbUp.upTable("gc_member_relation").dataSqlList(taSql1, new MDataMap());
					
					//TA的一度好友人数
					int taOneFridenCount = taOneFridenListMap.size();
					List<String> taOneAdd = new ArrayList<String>();
					if(taOneFridenListMap != null && taOneFridenCount > 0){
						for(int j = 0;j<taOneFridenCount;j++){
							//TA的所有一度好友
							taOneAdd.add("'"+taOneFridenListMap.get(j).get("account_code")+"'");
						}
					}
					
					//TA的总返利
					String taTotalRebate = GetTaTotalRebate(accountCode,taAccountCode);
					//TA的一度好友总返利
					String taOneFridendTotalRebate = "0";
					taOneFridendTotalRebate = GetFridendTotalRebate(accountCode,taOneAdd);
					
					//总返利 =TA的总返利+TA的一度好友总返利
					float totalRebateMoney = 0;
					totalRebateMoney = Float.parseFloat(taTotalRebate) + Float.parseFloat(taOneFridendTotalRebate);
					String strTotalRebateMoney = String.valueOf(decimalFormat.format(totalRebateMoney));
					oneInfo.setTotalRebateMoney(strTotalRebateMoney);
					
					friendsInfoList.add(oneInfo);
				}
			}
	    	
	    }else if("3".equals(selectionType)){
    		//二度好友列表
    		List<Map<String, Object>> twoFridendsList = new ArrayList<Map<String, Object>>();
    		String rel2Sql="select account_code from gc_member_relation where parent_code in (select account_code from gc_member_relation where parent_code=:account_code and flag_enable=1) and flag_enable=1 order by create_time "+limitString;
    		twoFridendsList=DbUp.upTable("gc_member_relation").dataSqlList(rel2Sql, in2Map);
    		
			//二度好友总条数
			if(twoTotalRelationList != null && twoTotalRelationList.size()>0){
				pageResults.setTotal(Integer.valueOf(twoTotalRelationList.size()));
			}else{
				pageResults.setTotal(0);
			}
			
			//返回的条数
			pageResults.setCount(twoFridendsList==null?0:twoFridendsList.size());
			//判断是否还有更多数据
			pageResults.setMore((pageOption.getLimit() * pageOption.getOffset() + pageResults
							.getCount()) < pageResults.getTotal() ? 1 : 0);
    		
			if(twoFridendsList != null && twoFridendsList.size()>0){
				for(int i=0;i<twoFridendsList.size();i++){
					
					PcFridensInfo twoInfo = new PcFridensInfo();
					String taAccountCode = String.valueOf(twoFridendsList.get(i).get("account_code"));
					
					//用户编号
					String memberCode = "";
					MDataMap membCodeMapGp=DbUp.upTable("mc_member_info").one("account_code",taAccountCode,"manage_code","SI2011");
					if(membCodeMapGp != null){
						memberCode = membCodeMapGp.get("member_code");
						//获取好友头像
						String headSql=" SELECT e.member_code,e.head_icon_url,e.nickname "
								+ " FROM membercenter.mc_extend_info_groupcenter e "
								+ " INNER JOIN membercenter.mc_member_info m ON e.member_code = m.member_code AND m.manage_code = 'SI2011' AND m.account_code ='"+ taAccountCode +"'";
						
						List<Map<String, Object>> aListMap=DbUp.upTable("mc_member_info").dataSqlList(headSql, new MDataMap());
						
						if(aListMap != null && aListMap.size() > 0){
							String headIconUrl = String.valueOf(aListMap.get(0).get("head_icon_url"));								
							twoInfo.setHeadIconUrl(headIconUrl);
						}
					}else{
						MDataMap membCodeMap=DbUp.upTable("mc_member_info").one("account_code",taAccountCode);
						if(membCodeMap != null){
							memberCode = membCodeMap.get("member_code");
						}
					}
					
					//好友等级
					String levelName = "";
					String acLevelSql = "SELECT le.level_name FROM gc_group_level le INNER JOIN gc_group_account  ac WHERE le.level_code = ac.account_level AND ac.account_code =:account_code";
					MDataMap acLevelMap = new MDataMap();
					acLevelMap.put("account_code", taAccountCode);
					Map<String, Object> levelNameMap = DbUp.upTable("gc_group_level").dataSqlOne(acLevelSql, acLevelMap);
					if(levelNameMap != null){
						levelName = String.valueOf(levelNameMap.get("level_name"));
					}
					twoInfo.setFridenLevel(levelName);
					
					//好友关系级别
					twoInfo.setRelationLevel("2");
					
					//好友手机号
					String fridenMobile = "";
					MDataMap mUserMap = DbUp.upTable("mc_login_info").one("member_code", memberCode);
					if(mUserMap != null){
						fridenMobile = mUserMap.get("login_name");
					}
					if(StringUtils.isNotBlank(fridenMobile)){
						fridenMobile = fridenMobile.substring(0, 3) + "****" + fridenMobile.substring(7);
					}
					twoInfo.setFridenMobile(fridenMobile);
					
					//最近下单时间
					String orderTime = "";
					MDataMap taOrderMap=new MDataMap("acCode",taAccountCode);
					String taOrderSql="select order_code,order_create_time from gc_reckon_order_info where account_code=:acCode order by order_create_time desc ";
					Map<String, Object> taOrdertTimeMap = DbUp.upTable("gc_reckon_order_info").dataSqlOne(taOrderSql, taOrderMap);
					if(taOrdertTimeMap != null){
						orderTime = String.valueOf(taOrdertTimeMap.get("order_create_time"));
					}
					twoInfo.setOrderTime(orderTime);
					
					//TA的总返利
					String taTotalRebate = GetTaTotalRebate(accountCode,taAccountCode);
					twoInfo.setTotalRebateMoney(taTotalRebate);
					
					friendsInfoList.add(twoInfo);
				}
			}
    	}
		
	    //列表信息
	    friendsListResult.setFriendsInfoList(friendsInfoList);
		//分页信息
		friendsListResult.setPageResults(pageResults);
		
		return friendsListResult;
	}

	/**
	 * 好友总返利
	 * @param accountCode
	 * @param fridenAdd 好友列表
	 * @return
	 */
	private String GetFridendTotalRebate(String accountCode,
			List<String> fridenAdd) {
		
		String fridendTotalRebate = "0.00";
		StringBuffer fridendTotalRebateSql = new StringBuffer();
		if(fridenAdd.size() > 0){
			fridendTotalRebateSql.append(" select IFNULL(sum(abs(reckon_money)),0) as fridendTotalRebate ");
			fridendTotalRebateSql.append(" from gc_reckon_log ");
			fridendTotalRebateSql.append(" where account_code = '").append(accountCode).append("' ");
			fridendTotalRebateSql.append(" and order_account_code in (").append(StringUtils.join(fridenAdd, ",")).append(") ");//好友
			fridendTotalRebateSql.append(" and reckon_change_type = '4497465200030004' ");
			List<Map<String, Object>> fridendTotalRebateList=DbUp.upTable("gc_reckon_log").dataSqlList(fridendTotalRebateSql.toString(), new MDataMap());
			if(fridendTotalRebateList != null){
				fridendTotalRebate = String.valueOf((fridendTotalRebateList.get(0).get("fridendTotalRebate")));
			}
		}
		return fridendTotalRebate;
	}

	/**
	 * TA的总返利
	 * @param accountCode
	 * @param taAccountCode
	 * @param nowYM
	 * @return
	 */
	private String GetTaTotalRebate(String accountCode, String taAccountCode) {
		
		String taTotalRebate = "0.00";
		MDataMap paramMap = new MDataMap();
		paramMap.put("accountCode", accountCode);
		paramMap.put("taAccountCode", taAccountCode);
		
		String taTotalRebateSql = "select IFNULL(sum(abs(reckon_money)),0) as taTotalRebate "
				+ " from gc_reckon_log "
				+ " where account_code =:accountCode "
				+ " and order_account_code =:taAccountCode " 
				+ " and reckon_change_type = '4497465200030004' "; //转入提现账户
			
		List<Map<String, Object>> taTotalRebateList=DbUp.upTable("gc_reckon_log").dataSqlList(taTotalRebateSql, paramMap);
		if(taTotalRebateList != null){
			taTotalRebate = String.valueOf((taTotalRebateList.get(0).get("taTotalRebate")));
		}
		return taTotalRebate;
	}

	/**
	 * 好友月返利
	 * @param accountCode
	 * @param fridenAdd
	 * @param nowYM
	 * @return
	 */
	private String GetFridendMonthRebate(String accountCode,
			List<String> fridenAdd, String nowYM) {
		
		String fridendMonthRebate = "0.00";
		StringBuffer fridendMonthRebateSql = new StringBuffer();
		if(fridenAdd.size() > 0){
			fridendMonthRebateSql.append(" select IFNULL(sum(abs(reckon_money)),0) as fridendMonthRebate ");
			fridendMonthRebateSql.append(" from gc_reckon_log ");
			fridendMonthRebateSql.append(" where account_code = '").append(accountCode).append("' ");
			fridendMonthRebateSql.append(" and order_account_code in (").append(StringUtils.join(fridenAdd, ",")).append(") ");//好友
			fridendMonthRebateSql.append(" and reckon_change_type = '4497465200030004' ");//转入提现账户
			fridendMonthRebateSql.append(" and left(order_reckon_time,7) = '").append(nowYM).append("' ");
			List<Map<String, Object>> fridendMonthList=DbUp.upTable("gc_reckon_log").dataSqlList(fridendMonthRebateSql.toString(), new MDataMap());
			if(fridendMonthList != null){
				fridendMonthRebate = String.valueOf((fridendMonthList.get(0).get("fridendMonthRebate")));
			}
		}
		return fridendMonthRebate;
	}

	/**
	 * 获取TA的月返利
	 * @param accountCode
	 * @param taAccountCode
	 * @param nowYM
	 * @return
	 */
	private String GetTaMonthRebate(String accountCode,
			String taAccountCode, String nowYM) {
		
		String taMonthRebate = "0.00";
		MDataMap paramMap = new MDataMap();
		paramMap.put("accountCode", accountCode);
		paramMap.put("taAccountCode", taAccountCode);
		paramMap.put("nowYM", nowYM);
		
		String taMonthRebateSql = "select IFNULL(sum(abs(reckon_money)),0) as taMonthRebate "
				+ " from gc_reckon_log "
				+ " where account_code =:accountCode "
				+ " and order_account_code =:taAccountCode " 
				+ " and reckon_change_type = '4497465200030004' " //转入提现账户
				+ " and left(order_reckon_time,7) =:nowYM ";
			
		List<Map<String, Object>> taMonthRebateList=DbUp.upTable("gc_reckon_log").dataSqlList(taMonthRebateSql, paramMap);
		if(taMonthRebateList != null){
			taMonthRebate = String.valueOf((taMonthRebateList.get(0).get("taMonthRebate")));
		}
		return taMonthRebate;
	}

	/**
	 * 提款记录
	 * @param accountCode
	 * @param withdrawRecordInput
	 * @return
	 */
	public PcAccountWithdrawRecordResult ShowPcWithdrawRecord(String accountCode, PcAccountWithdrawRecordInput withdrawRecordInput) {
		
		PcAccountWithdrawRecordResult withdrawRecordResult=new PcAccountWithdrawRecordResult();
		List<PcWithdrawRecordInfo> withdrawList=new ArrayList<PcWithdrawRecordInfo>();
		
		//取注册日期作为页面查询时提示的开始日期
		String tipsBeginTime = "";
		String tipTimeSql="select create_time from mc_login_info where member_code in(select member_code from mc_member_info where account_code=:account_code) order by create_time limit 1";
		Map<String, Object> memberMap=DbUp.upTable("mc_login_info").dataSqlOne(tipTimeSql, new MDataMap("account_code",accountCode));
		if(memberMap!=null&&StringUtils.isNotBlank(String.valueOf(memberMap.get("create_time")))){
			tipsBeginTime=memberMap.get("create_time").toString().substring(0, 10);
		}
		withdrawRecordResult.setTipsBeginTime(tipsBeginTime);
		//取系统日期作为页面查询时提示的结束日期
		String tipsEndTime = DateUtil.getSysDateString();
		withdrawRecordResult.setTipsEndTime(tipsEndTime);
		
		MDataMap accountMap=DbUp.upTable("gc_group_account").one("account_code",accountCode);
		if(accountMap!=null){
			//账户余额 
			withdrawRecordResult.setAccountWithdrawMoney(accountMap.get("account_withdraw_money"));
			
			//提现金额 
			withdrawRecordResult.setWithdrawedMoney(new BigDecimal(accountMap.get("total_withdraw_money")).subtract(new BigDecimal(accountMap.get("account_withdraw_money"))).toString());
			
			//已经提现到账的总额
			Object arrivalMoney = DbUp.upTable("gc_pay_order_info").dataGet("sum(withdraw_money)", "", new MDataMap("account_code",accountCode,"pay_status","4497465200070004"));
			withdrawRecordResult.setArrivalMoney(arrivalMoney!=null ? arrivalMoney.toString() : "0.00") ;
		}
		//组织参数 start
		//统计用查询条件
		String sCountWhere = " account_code = '"+accountCode+"' ";
		//查询记录用条件
		String sWhereString = " account_code = '"+accountCode+"' ";
		if(StringUtils.isNotBlank(withdrawRecordInput.getStartDate())){
			sWhereString += " and  left(create_time,10) >= '" + withdrawRecordInput.getStartDate()+"' " ;
			sCountWhere += " and  left(create_time,10) >= '" + withdrawRecordInput.getStartDate()+"' " ;
		}
		
		if(StringUtils.isNotBlank(withdrawRecordInput.getEndDate())){
			sWhereString += " and  left(create_time,10) <= '" + withdrawRecordInput.getEndDate()+"' " ;
			sCountWhere += " and  left(create_time,10) <= '" + withdrawRecordInput.getEndDate()+"' " ;
		}
		
		//正在提现，订单状态: 审核不通过
		if(withdrawRecordInput.getStatus().equals("4497465200070001")){
			sWhereString += " and  pay_status  =  '" + withdrawRecordInput.getStatus()+"' and order_status !='4497153900120003' " ;
			sCountWhere += " and  pay_status  =  '" + withdrawRecordInput.getStatus()+"' and order_status !='4497153900120003' " ;
			
			Object searchReadyMoney = DbUp.upTable("gc_pay_order_info").dataGet("sum(withdraw_money)", sCountWhere,null);//正在提现金额
			Object searchReadyNum= DbUp.upTable("gc_pay_order_info").dataGet("count(1)", sCountWhere,null);//正在提现笔数
			withdrawRecordResult.setSearchReadyMoney(searchReadyMoney!=null ? searchReadyMoney.toString() : "0.00") ;
			withdrawRecordResult.setSearchReadyNum(searchReadyNum!=null ? searchReadyNum.toString() : "0");
		}else if(withdrawRecordInput.getStatus().equals("4497465200070003")){
			//提现失败，订单状态 失败: 
			sWhereString += " and  (pay_status  =  '" + withdrawRecordInput.getStatus()+"' or order_status ='4497153900120003' ) " ;
			sCountWhere += " and  (pay_status  =  '" + withdrawRecordInput.getStatus()+"' or order_status ='4497153900120003' ) " ;
			
			Object searchErrorMoney = DbUp.upTable("gc_pay_order_info").dataGet("sum(withdraw_money)", sCountWhere,null);//提现失败金额
			Object searchErrorNum= DbUp.upTable("gc_pay_order_info").dataGet("count(1)", sCountWhere,null);//提现失败笔数
			withdrawRecordResult.setSearchErrorMoney(searchErrorMoney!=null ? searchErrorMoney.toString() : "0.00") ;
			withdrawRecordResult.setSearchErrorNum(searchErrorNum!=null ? searchErrorNum.toString() : "0");
		}else if(withdrawRecordInput.getStatus().equals("4497465200070004")){
			//提现成功
			sWhereString += " and  pay_status  =  '" + withdrawRecordInput.getStatus()+"' " ;
			sCountWhere += " and  pay_status  =  '" + withdrawRecordInput.getStatus()+"' " ;
			
			Object searchArrivalMoney = DbUp.upTable("gc_pay_order_info").dataGet("sum(withdraw_money)", sCountWhere,null);//提现到账金额
			Object searchArrivalNum= DbUp.upTable("gc_pay_order_info").dataGet("count(1)", sCountWhere,null);//提现到账笔数
			withdrawRecordResult.setSearchArrivalMoney(searchArrivalMoney!=null ? searchArrivalMoney.toString() : "0.00") ;
			withdrawRecordResult.setSearchArrivalNum(searchArrivalNum!=null ? searchArrivalNum.toString() : "0");
		}else{
			//统计查询条件不带提现状态的数据
			//正在提现的统计
			String sCountReadyWhere = sCountWhere + " and  pay_status  =  '4497465200070001' and order_status !='4497153900120003' ";
			Object searchReadyMoney = DbUp.upTable("gc_pay_order_info").dataGet("sum(withdraw_money)", sCountReadyWhere,null);//正在提现金额
			Object searchReadyNum= DbUp.upTable("gc_pay_order_info").dataGet("count(1)", sCountReadyWhere,null);//正在提现笔数
			withdrawRecordResult.setSearchReadyMoney(searchReadyMoney!=null ? searchReadyMoney.toString() : "0.00") ;
			withdrawRecordResult.setSearchReadyNum(searchReadyNum!=null ? searchReadyNum.toString() : "0");
			
			//提现失败的统计
			String sCountErrorWhere = sCountWhere + " and  (pay_status  =  '4497465200070003' or order_status ='4497153900120003' ) ";
			Object searchErrorMoney = DbUp.upTable("gc_pay_order_info").dataGet("sum(withdraw_money)", sCountErrorWhere,null);//提现失败金额
			Object searchErrorNum= DbUp.upTable("gc_pay_order_info").dataGet("count(1)", sCountErrorWhere,null);//提现失败笔数
			withdrawRecordResult.setSearchErrorMoney(searchErrorMoney!=null ? searchErrorMoney.toString() : "0.00") ;
			withdrawRecordResult.setSearchErrorNum(searchErrorNum!=null ? searchErrorNum.toString() : "0");
			
			//提现成功的统计
			String sCountArrivalWhere = sCountWhere + " and  pay_status  =  '4497465200070004' ";
			Object searchArrivalMoney = DbUp.upTable("gc_pay_order_info").dataGet("sum(withdraw_money)", sCountArrivalWhere,null);//提现到账金额
			Object searchArrivalNum= DbUp.upTable("gc_pay_order_info").dataGet("count(1)", sCountArrivalWhere,null);//提现到账笔数
			withdrawRecordResult.setSearchArrivalMoney(searchArrivalMoney!=null ? searchArrivalMoney.toString() : "0.00") ;
			withdrawRecordResult.setSearchArrivalNum(searchArrivalNum!=null ? searchArrivalNum.toString() : "0");
		}
		//组织参数 end 
		
		//取提现列表
		MPageData mPageData= DataPaging.upPageData("gc_pay_order_info", "", "-create_time",sWhereString,null,withdrawRecordInput.getPageOption());
		List<MDataMap> payOrderList=mPageData.getListData();
		if(payOrderList!=null&&payOrderList.size()>0){
			for(MDataMap payOrderMap:payOrderList){
				PcWithdrawRecordInfo withdrawRecordInfo=new PcWithdrawRecordInfo();
				//提现状态,提现到账
				if(payOrderMap.get("order_status").equals("4497153900120002")&&payOrderMap.get("pay_status").equals("4497465200070004")){
					withdrawRecordInfo.setStatus("提现到账");
					withdrawRecordInfo.setMoney("-"+payOrderMap.get("withdraw_money"));
					MDataMap logMap=DbUp.upTable("gc_pay_order_log").one("pay_order_code",payOrderMap.get("pay_order_code"),"pay_status","4497465200070004");
					if(logMap!=null){
						if(StringUtils.isNotBlank(logMap.get("update_time"))){
							withdrawRecordInfo.setDescription(logMap.get("update_time").substring(5,7)+"月"+logMap.get("update_time").substring(8, 10)+"日"+"成功");
						}
					}
				}
				//提现失败
				else if(payOrderMap.get("order_status").equals("4497153900120003")||payOrderMap.get("pay_status").equals("4497465200070003")){
					withdrawRecordInfo.setStatus("提现失败");
					withdrawRecordInfo.setMoney(payOrderMap.get("withdraw_money"));
					if(payOrderMap.get("order_status").equals("4497153900120003")){
						if(StringUtils.isNotBlank(payOrderMap.get("audit_time"))){
							withdrawRecordInfo.setDescription(payOrderMap.get("audit_time").substring(5, 7)+"月"+payOrderMap.get("audit_time").substring(8, 10)+"日"+"失败");
						}
						
					}
					else if(payOrderMap.get("pay_status").equals("4497465200070003")){
						MDataMap logMap=DbUp.upTable("gc_pay_order_log").one("pay_order_code",payOrderMap.get("pay_order_code"),"pay_status","4497465200070003");
						if(logMap!=null){
							if(StringUtils.isNotBlank(logMap.get("update_tome"))){
								withdrawRecordInfo.setDescription(logMap.get("update_time").substring(5,7)+"月"+logMap.get("update_time").substring(8, 10)+"日"+"失败");
							}
							
						} 
					}
					
				}
				//正在提现
				else{
					withdrawRecordInfo.setStatus("正在提现");
					withdrawRecordInfo.setMoney("-"+payOrderMap.get("withdraw_money"));
					String preDay=DateHelper.upDateTimeAdd(DateHelper.parseDate(payOrderMap.get("create_time")),Calendar.DATE,7);
					withdrawRecordInfo.setDescription("预计"+preDay.substring(5, 7)+"月"+preDay.substring(8,10)+"日"+"到账 ");
					
				}
				 //pc 返回字符串
				withdrawRecordInfo.setPcTime(String.valueOf(payOrderMap.get("create_time")));
				//提现时间
				withdrawRecordInfo.setTime(GetTimeDescription(payOrderMap.get("create_time")));
				withdrawList.add(withdrawRecordInfo);
			}
		}
		withdrawRecordResult.setWithdrawRecordList(withdrawList);
		withdrawRecordResult.setPageResults(mPageData.getPageResults());
		
		return withdrawRecordResult;
	}
	
	/**
	 * 获取今天、昨天特定展现形式
	 * @param dateTime
	 * @return
	 */
	public String GetTimeDescription(String dateTime){
		String timeDescription=dateTime; 
        String today=FormatHelper.upDateTime();
        String dateTimeBefore=DateHelper.upDateTimeAdd(DateHelper.parseDate(dateTime), Calendar.DATE,1);
        if(today.substring(0, 10).equals(dateTime.substring(0, 10))){
        	timeDescription="今天 "+dateTime.substring(11,16);
        }
        else if(today.substring(0, 10).equals(dateTimeBefore.substring(0, 10))){
        	timeDescription="昨天 "+dateTime.substring(11,16);
        }
        else{
        	timeDescription=dateTime.substring(0, 16);
        }
		return timeDescription;
	}

}
