package com.cmall.groupcenter.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.pool.impl.FromLargestCachePoolEvictor;

import org.apache.commons.lang.StringUtils;
import org.omg.CORBA.DATA_CONVERSION;

import scala.annotation.varargs;

import com.cmall.groupcenter.account.model.AccountPushSetInfoResult;
import com.cmall.groupcenter.account.model.AccountPushSetInput;
import com.cmall.groupcenter.account.model.AccountPushTypeInfo;
import com.cmall.groupcenter.account.model.AccountRecordInfo;
import com.cmall.groupcenter.account.model.AccountRecordInput;
import com.cmall.groupcenter.account.model.AccountRecordResult;
import com.cmall.groupcenter.account.model.ConsumeDetail;
import com.cmall.groupcenter.account.model.GetConsumeDetailInput;
import com.cmall.groupcenter.account.model.GetConsumeDetailResult;
import com.cmall.groupcenter.account.model.MoneyHistoryDetail;
import com.cmall.groupcenter.account.model.OrderSkuRebateMoneyInfo;
import com.cmall.groupcenter.account.model.OrderTransactionHistoryInfo;
import com.cmall.groupcenter.account.model.RebateRecordDetailInput;
import com.cmall.groupcenter.account.model.RebateRecordDetailResult;
import com.cmall.groupcenter.account.model.RebateRecordNewVersionResult;
import com.cmall.groupcenter.account.model.RebateRecordResult;
import com.cmall.groupcenter.account.model.ShowMoneyHistoryResult;
import com.cmall.groupcenter.account.model.WithdrawRecordInfo;
import com.cmall.groupcenter.account.model.WithdrawRecordInput;
import com.cmall.groupcenter.account.model.WithdrawRecordNewVersionInfo;
import com.cmall.groupcenter.account.model.WithdrawRecordNewVersionInput;
import com.cmall.groupcenter.account.model.WithdrawRecordResult;
import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.groupcenter.model.MPageData;
import com.cmall.groupcenter.third.model.GroupRebateRecordInput;
import com.cmall.groupcenter.third.model.GroupRebateRecordList;
import com.cmall.groupcenter.third.model.GroupRebateRecordResult;
import com.cmall.groupcenter.third.model.RebateProductDetail;
import com.cmall.groupcenter.util.DataPaging;
import com.cmall.membercenter.helper.NickNameHelper;
import com.cmall.productcenter.model.FlashsalesSkuInfo;
import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.LogHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebTemp;

/**
 * 微公社相关
 * @author chenbin
 *
 */
public class GroupService extends BaseClass{

	/**
	 * 财产明细
	 * @param accountCode
	 * @return
	 */
	public ShowMoneyHistoryResult ShowMoneyHistory(String accountCode){
		ShowMoneyHistoryResult showMoneyHistoryResult=new ShowMoneyHistoryResult();
		MDataMap accountMap = DbUp.upTable("gc_group_account").one(
				"account_code", accountCode);
		String currentMonth = DateHelper
				.upMonth(FormatHelper.upDateTime());
		//当前月份
		showMoneyHistoryResult.setCurrentMonth(currentMonth.substring(5));
		String sLevelCode = GroupConst.DEFAULT_LEVEL_CODE;
		if(accountMap!=null){
			sLevelCode=accountMap.get("account_level");
		}
		String sql="select ifnull(sum(withdraw_money),0) as monthRebate from gc_withdraw_log where account_code=:account_code and (withdraw_change_type='4497465200040001' or withdraw_change_type='4497465200040003') "
				+ " and left(create_time,7)=:current_month";
		Map<String, Object> rebateMap=DbUp.upTable("gc_withdraw_log").dataSqlOne(sql, new MDataMap("account_code",accountCode,"current_month",currentMonth));
		if(rebateMap!=null){
			//当月返利
			showMoneyHistoryResult.setCurrentRebateMoney(rebateMap.get("monthRebate").toString());
		}
		MDataMap mActiveMap = DbUp.upTable("gc_active_month").oneWhere(
				"sum_consume,sum_member", "", "", "account_code",
				accountCode, "active_month", currentMonth);
		// 当月消费
		if (mActiveMap != null) {
		    showMoneyHistoryResult.setCurrentConsume(mActiveMap.get("sum_consume"));
		}
		MDataMap mLevelMap = WebTemp.upTempDataMap("gc_group_level", "",
				"level_code", sLevelCode);
		//当前级别
		showMoneyHistoryResult.setCurrentLevelName(mLevelMap.get("level_name"));
		BigDecimal gapConsume=BigDecimal.ZERO;
		//土豪级别时，特殊处理
		MDataMap topLevelMap=WebTemp.upTempDataMap("gc_group_level", "", "level_code","4497465200010004");
		if(sLevelCode.equals(GroupConst.TOP_LEVEL_CODE)){
			gapConsume=new BigDecimal(topLevelMap.get("upgrade_consume")).subtract(new BigDecimal(showMoneyHistoryResult.getCurrentConsume()));
		}
		else{
			gapConsume = new BigDecimal(
					mLevelMap.get("upgrade_consume")).subtract(new BigDecimal(
					showMoneyHistoryResult.getCurrentConsume()));
		}
		
		if (gapConsume.compareTo(BigDecimal.ZERO) < 0) {
			gapConsume = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		}
		//距离升级还需消费
		showMoneyHistoryResult.setGapConsume(gapConsume.toString());
		//历史数据
		List<MoneyHistoryDetail> historyList=new ArrayList<MoneyHistoryDetail>();
		List<MoneyHistoryDetail> historyYearList=new ArrayList<MoneyHistoryDetail>();
		List<String> monthLogList=new ArrayList<String>();
		String beginMonth="";
		String twoBeginMonth="";
		String endMonth=DateHelper
				.upMonth(DateHelper.upDateTimeAdd("-1M"));
		//取注册日期
		String memberSql="select create_time from mc_login_info where member_code in(select member_code from mc_member_info where account_code=:account_code) order by create_time limit 1";
		Map<String, Object> memberMap=DbUp.upTable("mc_login_info").dataSqlOne(memberSql, new MDataMap("account_code",accountCode));
		if(memberMap!=null&&memberMap.get("create_time")!=null){
			beginMonth=memberMap.get("create_time").toString().substring(0, 7);
		}
		//历史返利
		String logSql="select sum(withdraw_money) as totalmoney,left(create_time,7) as onemonth from groupcenter.gc_withdraw_log where left(create_time,7)!=left(NOW(),7) and "
            +"withdraw_change_type in ('4497465200040001','4497465200040003') and account_code='"+accountCode+"' GROUP BY onemonth ORDER BY onemonth asc";
		List<Map<String, Object>> logList=DbUp.upTable("gc_reckon_log").dataSqlList(logSql, new MDataMap());
		//先将有返利的月份加入
		if(logList!=null&&logList.size()>0){
			twoBeginMonth=logList.get(0).get("onemonth").toString();
			for(Map<String, Object> logMap:logList){
				monthLogList.add(logMap.get("onemonth").toString());
				MoneyHistoryDetail moneyHistoryDetail=new MoneyHistoryDetail();
				moneyHistoryDetail.setRebateMoney(logMap.get("totalmoney").toString());
				moneyHistoryDetail.setMonth(logMap.get("onemonth").toString());
				MDataMap mDataMap=DbUp.upTable("gc_active_month").one("account_code",accountCode,"active_month",logMap.get("onemonth").toString());
				if(mDataMap!=null){
					moneyHistoryDetail.setConsumeMoney(mDataMap.get("sum_consume"));
				}
				historyList.add(moneyHistoryDetail);
			}
			
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		if(beginMonth!=""&&twoBeginMonth!=""){
			try {
				Date date1=format.parse(beginMonth);
				Date date2=format.parse(twoBeginMonth);
				if(date2.before(date1)){
					beginMonth=twoBeginMonth;
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		if(beginMonth!=""){
			if(!beginMonth.equals(currentMonth)){
			List<String> monthList=DateHelper.getMonthList(beginMonth, endMonth);
			Collections.reverse(monthList);
			//计算各个月消费、返利金额
			for(String oneMonth:monthList){
				if(!monthLogList.contains(oneMonth)){
					MoneyHistoryDetail moneyHistoryDetail=new MoneyHistoryDetail();
					//月份
					moneyHistoryDetail.setMonth(oneMonth);
					MDataMap mDataMap=DbUp.upTable("gc_active_month").one("account_code",accountCode,"active_month",oneMonth);
					if(mDataMap!=null){
						//消费
						moneyHistoryDetail.setConsumeMoney(mDataMap.get("sum_consume"));
					}
					historyList.add(moneyHistoryDetail);
				}
			}
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
			    }
			    else{
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
						// TODO Auto-generated catch block
						e.printStackTrace();
					}  
			         
			    }
			}
			//计算各个月等级变化描述
			for(MoneyHistoryDetail moneyHistoryDetail:historyList){
				String curMonth=moneyHistoryDetail.getMonth();
				String nextMonth="";
				try {
					Date date = format.parse(curMonth);
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(date);
					calendar.add(Calendar.MONTH, -1);
					nextMonth=format.format(calendar.getTime());
				} catch (ParseException e) {
					e.printStackTrace();
				}
				BigDecimal curLevel=new BigDecimal(monthLevelMap.get(curMonth)) ;
				moneyHistoryDetail.setLevelName(WebTemp.upTempDataOne("gc_group_level", "level_name", "level_code",monthLevelMap.get(curMonth)));
				if(monthLevelMap.get(nextMonth)!=null){
					BigDecimal nextLevel=new BigDecimal(monthLevelMap.get(nextMonth));
					if(curLevel.compareTo(nextLevel)==1){
						moneyHistoryDetail.setLevelDescription("升级成功");
					}
					else if(curLevel.compareTo(nextLevel)==0){
						moneyHistoryDetail.setLevelDescription("再接再厉");
					}
					else{
						moneyHistoryDetail.setLevelDescription("保级失败");
					}
				}
				else{
					moneyHistoryDetail.setLevelDescription("");
				}
				
				
			}
			//时间逆序排序
			Collections.sort(historyList, new Comparator<Object>() {
			      public int compare(Object moneyHistoryDetail1, Object moneyHistoryDetail2) {
			    	  String one = ((MoneyHistoryDetail)moneyHistoryDetail1).getMonth();
			    	  String two = ((MoneyHistoryDetail)moneyHistoryDetail2).getMonth();
			        return two.compareTo(one);
			      }
			    });
			//当前年份
			String currentYear = DateHelper
					.upMonth(FormatHelper.upDateTime()).substring(0,4);
			int iLength = -1;
			//最终列表拼凑
			for(MoneyHistoryDetail moneyHistoryDetail:historyList){
				String year = moneyHistoryDetail.getMonth().substring(0,4);
				if(!currentYear.equals(year)&&(iLength==-1||!StringUtils.equals(historyYearList.get(iLength).getMonth().substring(0, 4),year))){
					MoneyHistoryDetail newMoneyHistoryDetail=new MoneyHistoryDetail();
					newMoneyHistoryDetail.setYear(year);
					historyYearList.add(newMoneyHistoryDetail);
				}
				historyYearList.add(moneyHistoryDetail);
				iLength = historyYearList.size() - 1;
				
			}
			showMoneyHistoryResult.setList(historyYearList);
		    }
		}
		
		return showMoneyHistoryResult;
	}
	
	/**
	 * 提款记录
	 * @param accountCode
	 * @param withdrawRecordInput
	 * @return
	 */
	public WithdrawRecordResult shoWithdrawRecord(String accountCode,WithdrawRecordInput withdrawRecordInput){
		WithdrawRecordResult withdrawRecordResult=new WithdrawRecordResult();
		List<WithdrawRecordInfo> withdrawList=new ArrayList<WithdrawRecordInfo>();
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
		String sWhereString = " account_code = '"+accountCode+"'";
		if(StringUtils.isNotBlank(withdrawRecordInput.getStartDate())){
			sWhereString += " and  create_time >= '" + withdrawRecordInput.getStartDate()+"' " ;
		}
		
		if(StringUtils.isNotBlank(withdrawRecordInput.getEndDate())){
			sWhereString += " and  create_time <= '" + withdrawRecordInput.getEndDate()+"' " ;
		}
		
		//封装查询统计
		Object searchArrivalMoney = DbUp.upTable("gc_pay_order_info").dataGet("sum(withdraw_money)", sWhereString+" and pay_status = '4497465200070004'",null);//提现到账金额
		Object searchArrivalNum= DbUp.upTable("gc_pay_order_info").dataGet("count(1)", sWhereString+" and pay_status = '4497465200070004'",null);//提现到账笔数
		withdrawRecordResult.setSearchArrivalMoney(searchArrivalMoney!=null ? searchArrivalMoney.toString() : "0.00") ;
		withdrawRecordResult.setSearchArrivalNum(searchArrivalNum!=null ? searchArrivalNum.toString() : "0");
		
		Object searchErrorMoney = DbUp.upTable("gc_pay_order_info").dataGet("sum(withdraw_money)", sWhereString+" and (pay_status = '4497465200070003' or order_status ='4497153900120003')  ",null);//提现失败金额
		Object searchErrorNum= DbUp.upTable("gc_pay_order_info").dataGet("count(1)", sWhereString+" and (pay_status = '4497465200070003' or order_status ='4497153900120003' ) ",null);//提现失败笔数
		withdrawRecordResult.setSearchErrorMoney(searchErrorMoney!=null ? searchErrorMoney.toString() : "0.00") ;
		withdrawRecordResult.setSearchErrorNum(searchErrorNum!=null ? searchErrorNum.toString() : "0");
		
		Object searchReadyMoney = DbUp.upTable("gc_pay_order_info").dataGet("sum(withdraw_money)", sWhereString+" and pay_status = '4497465200070001' and order_status !='4497153900120003' ",null);//正在提现金额
		Object searchReadyNum= DbUp.upTable("gc_pay_order_info").dataGet("count(1)", sWhereString+" and pay_status = '4497465200070001' and order_status !='4497153900120003' ",null);//正在提现笔数
		withdrawRecordResult.setSearchReadyMoney(searchReadyMoney!=null ? searchReadyMoney.toString() : "0.00") ;
		withdrawRecordResult.setSearchReadyNum(searchReadyNum!=null ? searchReadyNum.toString() : "0");
		
		
		if(StringUtils.isNotBlank(withdrawRecordInput.getStatus())){
			//正在提现，订单状态: 审核不通过
			if(withdrawRecordInput.getStatus().equals("4497465200070001"))
				sWhereString += " and  pay_status  =  '" + withdrawRecordInput.getStatus()+"' and order_status !='4497153900120003' " ;
			else
			//提现失败，订单状态 失败: 
			if(withdrawRecordInput.getStatus().equals("4497465200070003"))
				sWhereString += " and  (pay_status  =  '" + withdrawRecordInput.getStatus()+"' or order_status ='4497153900120003' ) " ;
			else
				sWhereString += " and  pay_status  =  '" + withdrawRecordInput.getStatus()+"' " ;
		}
		//组织参数 end 
		
		//取提现列表
		MPageData mPageData= DataPaging.upPageData("gc_pay_order_info", "", "-create_time",sWhereString,null,withdrawRecordInput.getPageOption());
		List<MDataMap> payOrderList=mPageData.getListData();
		if(payOrderList!=null&&payOrderList.size()>0){
			for(MDataMap payOrderMap:payOrderList){
				WithdrawRecordInfo withdrawRecordInfo=new WithdrawRecordInfo();
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
				withdrawRecordInfo.setTime(getTimeDescription(payOrderMap.get("create_time")));
				withdrawList.add(withdrawRecordInfo);
			}
		}
		withdrawRecordResult.setWithdrawRecordList(withdrawList);
		withdrawRecordResult.setPageResults(mPageData.getPageResults());
		
		
		
		return withdrawRecordResult;
	}
	
	/**
	 * 退款明细
	 * @param accountCode
	 * @param withdrawRecordInput
	 * @return
	 */
	public WithdrawRecordResult showRefundRecord(String accountCode,WithdrawRecordInput withdrawRecordInput){
		WithdrawRecordResult withdrawRecordResult=new WithdrawRecordResult();
		List<WithdrawRecordInfo> refundList=new ArrayList<WithdrawRecordInfo>();
		MDataMap accountMap=DbUp.upTable("gc_group_account").one("account_code",accountCode);
		if(accountMap!=null){
			//账户余额 
			withdrawRecordResult.setAccountWithdrawMoney(accountMap.get("account_withdraw_money"));
			//扣款金额 
			MDataMap map=DbUp.upTable("gc_withdraw_log").oneWhere(" ifnull(sum(withdraw_money),0.00) as totalMoney", "", " account_code=:account_code and withdraw_change_type=:withdraw_change_type", "account_code",accountCode,"withdraw_change_type","4497465200040003");
			if(map!=null){
				withdrawRecordResult.setWithdrawedMoney(map.get("totalMoney"));
			}
			
		}
		//取退款列表
		MPageData mPageData= DataPaging.upPageData("gc_withdraw_log", "", "-create_time", new MDataMap("account_code",accountCode,"withdraw_change_type","4497465200040003"),withdrawRecordInput.getPageOption());
		List<MDataMap> withdrawLogList=mPageData.getListData();
		if(withdrawLogList!=null&&withdrawLogList.size()>0){
			for(MDataMap withdrawLogMap:withdrawLogList){
		        WithdrawRecordInfo withdrawRecordInfo=new WithdrawRecordInfo();	
		        //状态
		        withdrawRecordInfo.setStatus("退货扣减");
		        //金额
		        withdrawRecordInfo.setMoney(withdrawLogMap.get("withdraw_money"));
		        //时间
		        withdrawRecordInfo.setTime(getTimeDescription(withdrawLogMap.get("create_time")));
		        //说明
		        withdrawRecordInfo.setDescription(withdrawLogMap.get("create_time").substring(5, 7)+"月"+withdrawLogMap.get("create_time").substring(8, 10)+"日"+"扣减");
		        refundList.add(withdrawRecordInfo);
			}
			withdrawRecordResult.setWithdrawRecordList(refundList);
		}
		withdrawRecordResult.setPageResults(mPageData.getPageResults());
		return withdrawRecordResult;
	}
	
	    /**
     * 返利记录
     * @param accountCode
     * @param withdrawRecordInput
     * @return
     */
    public RebateRecordResult showRebateRecord(String accountCode,WithdrawRecordInput withdrawRecordInput){
        return   showRebateRecord(accountCode,withdrawRecordInput,null);
    }



	/**
	 * 通过订单编号和用户编号获取返利记录
	 * @param accountCode
	 * @param withdrawRecordInput
	 * @return
	 */
	public RebateRecordResult showRebateRecord(String accountCode,WithdrawRecordInput withdrawRecordInput,String orderCode){
        RebateRecordResult rebateRecordResult=new RebateRecordResult();
        List<WithdrawRecordInfo> rebateList=new ArrayList<WithdrawRecordInfo>();
        MDataMap accountMap=DbUp.upTable("gc_group_account").one("account_code",accountCode);
        if(accountMap!=null){
            //预计返利
            rebateRecordResult.setAccountRebateMoney(accountMap.get("account_rebate_money"));
            //累计返利
            rebateRecordResult.setTotalReckonMoney(accountMap.get("total_reckon_money"));
        }
        MPageData mPageData = null;

        //		如果ordercode没有值， 则查询该用户下所有的返利记录，否则查询该用户改订单下所有的返利记录
        if (StringUtils.isBlank(orderCode)){
//            查询所有记录列表
            //取返利记录列表
             mPageData= DataPaging.upPageData("gc_rebate_order", "", "rebate_status,order_create_time desc", new MDataMap("account_code",accountCode),withdrawRecordInput.getPageOption());
        }else {

//查询该用户改订单的记录
            MDataMap whereMdataMap = new MDataMap();
            whereMdataMap.put("account_code",accountCode);
            whereMdataMap.put("order_code",orderCode);
            mPageData= DataPaging.upPageData("gc_rebate_order", "", "rebate_status,order_create_time desc",whereMdataMap,withdrawRecordInput.getPageOption());
        }


        List<MDataMap> rebateOrderList=mPageData.getListData();
        if(rebateOrderList!=null&&rebateOrderList.size()>0){
            for(MDataMap orderMap:rebateOrderList){
                WithdrawRecordInfo rebateRecordInfo=new WithdrawRecordInfo();
                //状态
                rebateRecordInfo.setStatus(WebTemp.upTempDataOne("sc_define", "define_name", "define_code",orderMap.get("rebate_status")));
                //金额
                //已取消
                if(orderMap.get("rebate_status").equals("4497465200170003")){
                    rebateRecordInfo.setMoney("-"+orderMap.get("rebate_money"));
                }
                else {
                    rebateRecordInfo.setMoney(orderMap.get("rebate_money"));
                }

//                orderCode、manageCode
                rebateRecordInfo.setOrderCode(orderMap.get("order_code"));
                rebateRecordInfo.setManageCode(orderMap.get("manage_code"));

                //时间
                rebateRecordInfo.setTime(getTimeDescription(orderMap.get("order_create_time")));

                //说明
                String description="";
                String preDay="";
                //已付款
                if(orderMap.get("rebate_status").equals("4497465200170002")){
                    if(StringUtils.isNotBlank(orderMap.get("order_send_time"))){
                        preDay=DateHelper.upDateTimeAdd(DateHelper.parseDate(orderMap.get("order_send_time")),Calendar.DATE,14);
                        description="预计"+preDay.substring(5, 7)+"月"+preDay.substring(8, 10)+"日"+"返利";
                    }
                }
                //已取消
                else if(orderMap.get("rebate_status").equals("4497465200170003")){
                    if(StringUtils.isNotBlank(orderMap.get("order_cancel_time"))){
                        description=orderMap.get("order_cancel_time").substring(5, 7)+"月"+orderMap.get("order_cancel_time").substring(8, 10)+"日"+"取消";
                    }
                }
                //已返利
                else if(orderMap.get("rebate_status").equals("4497465200170004")){
                    if(StringUtils.isNotBlank(orderMap.get("order_finish_time"))){
                        preDay=DateHelper.upDateTimeAdd(DateHelper.parseDate(orderMap.get("order_finish_time")),Calendar.DATE,9);
                        description=preDay.substring(5, 7)+"月"+preDay.substring(8, 10)+"日"+"返利";
                    }
                }
                rebateRecordInfo.setDescription(description);
                rebateList.add( rebateRecordInfo);
            }
            rebateRecordResult.setRebateRecordList(rebateList);
        }
        rebateRecordResult.setPageResults(mPageData.getPageResults());
        return rebateRecordResult;

    }
	
	/**
	 * 通过账户编号获取手机号
	 * @param accountCode
	 * @return
	 */
	public String getMobileByAccountCode(String accountCode){
		String mobile="";
		List<MDataMap> memberList=DbUp.upTable("mc_member_info").queryByWhere("account_code",accountCode);
		if(memberList!=null&&memberList.size()>0){
			MDataMap memberMap=memberList.get(0);
			if(StringUtils.isNotEmpty(memberMap.get("member_code"))){
				List<MDataMap> mobileList=DbUp.upTable("mc_login_info").queryByWhere("member_code",memberMap.get("member_code"));
				if(mobileList!=null&&mobileList.size()>0){
					MDataMap mobileMap=mobileList.get(0);
					if(StringUtils.isNotEmpty(mobileMap.get("login_name"))){
						mobile=mobileMap.get("login_name");
					}
				}
			}
			
		}
		return mobile;
	}
	
	/**
	 * 获取今天、昨天特定展现形式
	 * @param dateTime
	 * @return
	 */
	public String getTimeDescription(String dateTime){
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
	
	public static void main(String args[]){
		GroupService groupService=new GroupService();
		//System.out.println(groupService.getTimeDescription("2015-01-29 09:24:54"));
	}

	/**
	 * 新版本的返利记录(2.1.4版)
	 * @param accountCode
	 * @param inputParam
	 * @return
	 */
	public RebateRecordNewVersionResult showRebateRecordForNewVersion(
			String accountCode, WithdrawRecordNewVersionInput newInput) {
		
		RebateRecordNewVersionResult rebateRecordNewResult=new RebateRecordNewVersionResult();
		List<WithdrawRecordNewVersionInfo> rebateList=new ArrayList<WithdrawRecordNewVersionInfo>();
		
		//返利明细类型
		String rebateType = newInput.getRebateType();
		
		MDataMap accountMap=DbUp.upTable("gc_group_account").one("account_code",accountCode);
		if(accountMap!=null){
			//预计返利
			rebateRecordNewResult.setAccountRebateMoney(accountMap.get("account_rebate_money"));
			//已返利 
			rebateRecordNewResult.setTotalReckonMoney(accountMap.get("total_withdraw_money"));
		}
		
		//查询条件
		MDataMap whereMap = new MDataMap();
		String sWhere = " account_code ='"+accountCode+"' ";
		String sWhereInField = "rebate_status";
		String sWhereInFieldVal = "";
		if("2".equals(rebateType)){
			//预计返利
			sWhereInFieldVal = "4497465200170001,4497465200170002";
		}else if("3".equals(rebateType)){
			//已返利
			sWhereInFieldVal = "4497465200170004";
		}
		
		//返利记录
		MPageData mPageData= DataPaging.upPageDataQueryIn("gc_rebate_order", "", "order_create_time desc",sWhere, whereMap, newInput.getPageOption(),sWhereInField,sWhereInFieldVal);
		
		List<MDataMap> rebateOrderList=mPageData.getListData();
		if(rebateOrderList!=null&&rebateOrderList.size()>0){
			for(MDataMap orderMap:rebateOrderList){
				
				WithdrawRecordNewVersionInfo rebateRecordInfo=new WithdrawRecordNewVersionInfo();
				
				//订单所属账户信息
				String taMemberCode = "";
				String orderAccountCode  = orderMap.get("order_account_code");
				String sql=" SELECT e.member_code,e.head_icon_url,e.nickname "
						+ " FROM membercenter.mc_extend_info_groupcenter e "
						+ " INNER JOIN membercenter.mc_member_info m ON e.member_code = m.member_code AND m.manage_code = 'SI2011' AND m.account_code ='"+ orderAccountCode +"'";
				
				List<Map<String, Object>> aListMap=DbUp.upTable("mc_member_info").dataSqlList(sql, new MDataMap());
				int aCount = aListMap.size();
				if(aListMap != null && aCount > 0){
					taMemberCode = String.valueOf(aListMap.get(0).get("member_code"));
					String headIconUrl = String.valueOf(aListMap.get(0).get("head_icon_url"));
					//头像
					rebateRecordInfo.setHeadIconUrl(headIconUrl);
				}
				
				//昵称
				if(accountCode.equals(orderAccountCode)){
					rebateRecordInfo.setNickName("自己");
				}else{
					
					if(StringUtils.isBlank(taMemberCode)){
						MDataMap membCodeMap=DbUp.upTable("mc_member_info").one("account_code",orderAccountCode);
						if(membCodeMap != null){
							taMemberCode = membCodeMap.get("member_code");
						}
					}
					Map<String,String> nickMap = new HashMap<String,String>();
					nickMap.put("account_code_wo", accountCode);
					nickMap.put("account_code_ta", orderAccountCode);
					nickMap.put("member_code", taMemberCode);
					rebateRecordInfo.setNickName(NickNameHelper.getNickName(nickMap));
				}
				
				//好友等级 0:自己 1：一度好友 2:2度好友
				String rLevel = orderMap.get("relation_level");
				rebateRecordInfo.setRelationLevel(rLevel);
				
		        //下单时间
				//当年：xx-xx hh:mm（月-日 时-分）24小时制显示 当交易时间超过当年时显示’xxxx-xx-xx’(年-月-日)
				rebateRecordInfo.setOrderCreateTime(orderMap.get("order_create_time"));
		        
		        //订单状态
				rebateRecordInfo.setOrderStatus(orderMap.get("order_status"));
		        
		        //返利金额
//		        if(orderMap.get("rebate_status").equals("4497465200170003")){
//		        	//已取消时
//		        	 rebateRecordInfo.setRebateMoney("-"+orderMap.get("rebate_money"));
//		        }else{
		        	 rebateRecordInfo.setRebateMoney("+"+orderMap.get("rebate_money"));
//				}
		        
		        //返利状态
		        String rebateStatus = orderMap.get("rebate_status");
		        rebateRecordInfo.setRebateStatus(rebateStatus);
		        //返利标签
		        String description="";
		        String preDay="";
		        //返利状态是未付款or已付款时，为"预计xx-xx返利"
		        if("4497465200170001".equals(rebateStatus) || "4497465200170002".equals(rebateStatus)){
		        	if(StringUtils.isNotBlank(orderMap.get("order_finish_time"))){
			        	preDay=DateHelper.upDateTimeAdd(DateHelper.parseDate(orderMap.get("order_finish_time")),Calendar.DATE,7);
			        	description="预计"+preDay.substring(5, 7)+"月"+preDay.substring(8, 10)+"日"+"返利";
		        	}
		        }else if("4497465200170004".equals(rebateStatus)){
		        	//返利状态是 已返利时，为"已返利"
		        	description = "已返利";
		        }else if("4497465200170003".equals(rebateStatus)){
		        	//交易关闭时，为"已取消"
		        	description = "已取消";
		        }
		        rebateRecordInfo.setRebateLabel(description);
		        
		        //记录UID,返利详情用
		        rebateRecordInfo.setRebateUid(String.valueOf(orderMap.get("uid")));
		        
		        rebateList.add(rebateRecordInfo);
			}
			rebateRecordNewResult.setRebateRecordList(rebateList);
		}
		rebateRecordNewResult.setPageResults(mPageData.getPageResults());
		return rebateRecordNewResult;
	}

	/**
	 * 新版本下单时间展示规则
	 * @param orderTime
	 * @return
	 */
	private String GetNewTimeDescription(String orderTime) {
		
		int sysYear = DateUtil.getYear(null);
		String timeDescription=orderTime;
		if(StringUtils.isNotEmpty(orderTime)){
			int orderYear = Integer.parseInt(orderTime.substring(0, 4));
			//当交易时间超过当年,xxxx-xx-xx格式展示
			if(orderYear < sysYear){
				timeDescription = orderTime.substring(0, 10);
			}else{
				//xx-xx hh:mm’（月-日 时-分）
				timeDescription = orderTime.substring(5,16);
			}
		}
		return timeDescription;
	}

	/**
	 * 
	 * 获取账户推送设定信息
	 * @param accountCode
	 * @param inputParam
	 * @return
	 */
	public AccountPushSetInfoResult ShowAccountPushSetInfo(String accountCode,
			RootInput inputParam) {
		AccountPushSetInfoResult accountPushSetInfo = new AccountPushSetInfoResult();
		//保存推送类型名称
		HashMap typeMap = new HashMap();
		//保存推送类型标题
		HashMap titleMap = new HashMap();
		//总开关默认为关闭
		String pushTypeMasterOnoff = "2";
		
		//获取最新的推送配置
		String sFields = "uid,push_title,push_type,push_range,push_onoff,push_range_type";
		List<MDataMap> pushSetList=DbUp.upTable("gc_push_set").queryAll(sFields, "", "", new MDataMap());
		if(pushSetList != null && pushSetList.size() >0){
			for(int i = 0;i < pushSetList.size() ;i++){
				String typeUid = pushSetList.get(i).get("uid");
				String typeName = pushSetList.get(i).get("push_type");
				String titleName = pushSetList.get(i).get("push_title");
				String pushRange = pushSetList.get(i).get("push_range");
				String pushTypeOnoff = pushSetList.get(i).get("push_onoff");
				String pushRangeType = pushSetList.get(i).get("push_range_type");// "0":独自推送 "1":范围推送
				typeMap.put(typeUid, typeName);
				titleMap.put(typeUid, titleName);
				//查询用户推送表中是否存在此配置
				MDataMap accountPushMap=DbUp.upTable("gc_account_push_set").one("account_code",accountCode,"push_type_id",typeUid);
				//没有此配置就插入
				if(accountPushMap == null || accountPushMap.isEmpty()){
					MDataMap dMap = new MDataMap();
					dMap.put("account_code", accountCode);
					dMap.put("push_type_id", typeUid);
					dMap.put("account_push_range", pushRange);
					dMap.put("push_range_type", pushRangeType);
					dMap.put("push_type_onoff", pushTypeOnoff);
					dMap.put("push_type_usable", "449746250001");//449746250001:可用  449746250002:不可用
					DbUp.upTable("gc_account_push_set").dataInsert(dMap);
				}
			}
		}
		
		//推送类型列表
		List<AccountPushTypeInfo> pushTypeInfoList = new ArrayList<AccountPushTypeInfo>();
		//获取账户的推送配置
		List<MDataMap> acPushSetList=DbUp.upTable("gc_account_push_set").queryByWhere("account_code",accountCode);
		if(acPushSetList != null && acPushSetList.size() >0){
			for(int i = 0;i < acPushSetList.size() ;i++){
				AccountPushTypeInfo acPushInfo = new AccountPushTypeInfo();
				String pushTypeId = acPushSetList.get(i).get("push_type_id");
				String accountPushRange = acPushSetList.get(i).get("account_push_range");
				String pushTypeOnoff = acPushSetList.get(i).get("push_type_onoff");
				String pushRangeType = acPushSetList.get(i).get("push_range_type");
				//如果有任何一项推送类型的开关为开启，则总开关设置为开启
				if("449747100001".equals(pushTypeOnoff)){
					pushTypeMasterOnoff = "1";
				}
				
				acPushInfo.setPushTitle(String.valueOf(titleMap.get(pushTypeId)));
				acPushInfo.setPushTypeId(pushTypeId);
				acPushInfo.setPushTypeName(String.valueOf(typeMap.get(pushTypeId)));
				acPushInfo.setAccountPushRange(accountPushRange);
				
				//开关类型转换为前台需要的短类型
				if("449747100001".equals(pushTypeOnoff)){
					acPushInfo.setPushTypeOnoff("1");
				}else if("449747100002".equals(pushTypeOnoff)){
					acPushInfo.setPushTypeOnoff("2");
				}
				acPushInfo.setPushRangeType(pushRangeType);
				pushTypeInfoList.add(acPushInfo);
			}
		
		}
		accountPushSetInfo.setPushTypeInfoList(pushTypeInfoList);
		accountPushSetInfo.setPushTypeMasterOnoff(pushTypeMasterOnoff);
		
		return accountPushSetInfo;
	}

	/**
	 * 修改账户推送信息的开关和范围
	 * @param accountCode
	 * @param inputParam
	 * @return
	 */
	public AccountPushSetInfoResult ModifyAccountPushTypeOnoff(
			String accountCode, AccountPushSetInput inputParam) {
		AccountPushSetInfoResult accountPushSetInfo = new AccountPushSetInfoResult();
		
		String pushTypeMasterOnoff = inputParam.getPushTypeMasterOnoff();
		String pushTypeID = inputParam.getPushTypeID();
		String pushRange = inputParam.getPushRange();
		String pushOnoff = inputParam.getPushOnoff();
		String pushRangeType = inputParam.getPushRangeType();
		
		//总开关设置 449747100001开 449747100002 关闭
		if(StringUtils.isNotBlank(pushTypeMasterOnoff)){
			if(!"1".equals(pushTypeMasterOnoff) && !"2".equals(pushTypeMasterOnoff) ){
				accountPushSetInfo.setResultCode(918519021);
				accountPushSetInfo.setResultMessage(bInfo(918519021));
				return accountPushSetInfo;
			}
			
			MDataMap dMap = new MDataMap();
			dMap.put("account_code", accountCode);
			if("1".equals(pushTypeMasterOnoff)){
				dMap.put("push_type_onoff", "449747100001");//开启
			}else{
				dMap.put("push_type_onoff", "449747100002");//关闭
			}
			
			DbUp.upTable("gc_account_push_set").dataUpdate(dMap, "push_type_onoff", "account_code");
			
		}else{
			//推送类型的UID
			if(StringUtils.isBlank(pushTypeID)){
				accountPushSetInfo.setResultCode(918519020);
				accountPushSetInfo.setResultMessage(bInfo(918519020,"【推送类型】"));
				return accountPushSetInfo;
			}
			
			//范围类型  0:独立开关 1：范围推送
			if(StringUtils.isBlank(pushRangeType)){
				accountPushSetInfo.setResultCode(918519020);
				accountPushSetInfo.setResultMessage(bInfo(918519020,"【推送范围类型】"));
				return accountPushSetInfo;
			}else if(!"0".equals(pushRangeType) && !"1".equals(pushRangeType)){
				accountPushSetInfo.setResultCode(918519022);
				accountPushSetInfo.setResultMessage(bInfo(918519022));
				return accountPushSetInfo;
			}
			
			//范围推送
			if("1".equals(pushRangeType)){
				if(StringUtils.isBlank(pushRange)){
					accountPushSetInfo.setResultCode(918519020);
					accountPushSetInfo.setResultMessage(bInfo(918519020,"【推送范围】"));
					return accountPushSetInfo;
				}
			}
			
			//独立开关
			if("0".equals(pushRangeType)){
				if(StringUtils.isBlank(pushOnoff)){
					accountPushSetInfo.setResultCode(918519020);
					accountPushSetInfo.setResultMessage(bInfo(918519020,"【推送开关】"));
					return accountPushSetInfo;
				}else if(!"1".equals(pushOnoff) && !"2".equals(pushOnoff)){
					accountPushSetInfo.setResultCode(918519023);
					accountPushSetInfo.setResultMessage(bInfo(918519023));
					return accountPushSetInfo;
				}
			}
			
			MDataMap dMap = new MDataMap();
			dMap.put("account_code", accountCode);
			dMap.put("push_type_id", pushTypeID);
			
			//范围推送
			if("1".equals(pushRangeType)){
				dMap.put("account_push_range", pushRange);
				//推动范围为关闭时,推送状态为 关闭
				if("449747220004".equals(pushRange)){
					dMap.put("push_type_onoff", "449747100002");//关闭
				}else{
					dMap.put("push_type_onoff", "449747100001");//开启
				}
				//更新推动范围和开关状态
				DbUp.upTable("gc_account_push_set").dataUpdate(dMap, "account_push_range,push_type_onoff", "account_code,push_type_id");
			}
			
			//独立开关
			if("0".equals(pushRangeType)){
				dMap.put("account_push_range", "");
				if("1".equals(pushOnoff)){
					dMap.put("push_type_onoff", "449747100001");//开启
				}else{
					dMap.put("push_type_onoff", "449747100002");//关闭
				}
				//更新推动开关状态
				DbUp.upTable("gc_account_push_set").dataUpdate(dMap, "account_push_range,push_type_onoff", "account_code,push_type_id");
			}
		}
		
		//返回更新后的新数据
		//总开关默认为关闭
		String retPushTypeMasterOnoff = "2";
		//保存推送类型名称
		HashMap retTypeMap = new HashMap();
		//保存推送类型标题
		HashMap retTitleMap = new HashMap();
		String retFields = "uid,push_title,push_type";
		List<MDataMap> pushSetList=DbUp.upTable("gc_push_set").queryAll(retFields, "", "", new MDataMap());
		if(pushSetList != null && pushSetList.size() >0){
			for(int i = 0;i < pushSetList.size() ;i++){
				String retTypeUid = pushSetList.get(i).get("uid");
				String retTypeName = pushSetList.get(i).get("push_type");
				String retTitleName = pushSetList.get(i).get("push_title");
				retTypeMap.put(retTypeUid, retTypeName);
				retTitleMap.put(retTypeUid, retTitleName);
			}
		}
		//推送类型列表
		List<AccountPushTypeInfo> retPushTypeInfoList = new ArrayList<AccountPushTypeInfo>();
		//获取账户的推送配置
		List<MDataMap> acPushSetList=DbUp.upTable("gc_account_push_set").queryByWhere("account_code",accountCode);
		if(acPushSetList != null && acPushSetList.size() >0){
			for(int i = 0;i < acPushSetList.size() ;i++){
				AccountPushTypeInfo retAcPushInfo = new AccountPushTypeInfo();
				String retPushTypeId = acPushSetList.get(i).get("push_type_id");
				String retAccountPushRange = acPushSetList.get(i).get("account_push_range");
				String retPushTypeOnoff = acPushSetList.get(i).get("push_type_onoff");
				String retPushRangeType = acPushSetList.get(i).get("push_range_type");
				//如果有任何一项推送类型的开关为开启，则总开关设置为开启
				if("449747100001".equals(retPushTypeOnoff)){
					retPushTypeMasterOnoff = "1";
				}
				
				retAcPushInfo.setPushTitle(String.valueOf(retTitleMap.get(retPushTypeId)));
				retAcPushInfo.setPushTypeId(retPushTypeId);
				retAcPushInfo.setPushTypeName(String.valueOf(retTypeMap.get(retPushTypeId)));
				retAcPushInfo.setAccountPushRange(retAccountPushRange);
				
				//开关类型转换为前台需要的短类型
				if("449747100001".equals(retPushTypeOnoff)){
					retAcPushInfo.setPushTypeOnoff("1");
				}else if("449747100002".equals(retPushTypeOnoff)){
					retAcPushInfo.setPushTypeOnoff("2");
				}
				retAcPushInfo.setPushRangeType(retPushRangeType);
				retPushTypeInfoList.add(retAcPushInfo);
			}
		
		}
		accountPushSetInfo.setPushTypeInfoList(retPushTypeInfoList);
		accountPushSetInfo.setPushTypeMasterOnoff(retPushTypeMasterOnoff);
		
		return accountPushSetInfo;
	}

	/**
	 * 返利明细的详情信息
	 * @param accountCode
	 * @param inputParam
	 * @return
	 */
	public RebateRecordDetailResult ShowRebateRecordDetail(String accountCode,
			RebateRecordDetailInput inputParam) {
		RebateRecordDetailResult detailResult = new RebateRecordDetailResult();
		
		String rebateUid = inputParam.getRebateUid();
		
		MDataMap rebateMap=DbUp.upTable("gc_rebate_order").one("uid",rebateUid);
		if(rebateMap != null){
			String orderAccountCode = rebateMap.get("order_account_code");//订单所属账号
			String rLevel = rebateMap.get("relation_level");//好友等级
			String orderCode = rebateMap.get("order_code");//订单编号
			String rebateMoney = rebateMap.get("rebate_money");//返利金额
			String rebateStatus = rebateMap.get("rebate_status");//返利状态
			String taMemberCode = "";
			//获取头像昵称
			String sql=" SELECT e.member_code,e.head_icon_url,e.nickname "
					+ " FROM membercenter.mc_extend_info_groupcenter e "
					+ " INNER JOIN membercenter.mc_member_info m ON e.member_code = m.member_code AND m.manage_code = 'SI2011' AND m.account_code ='"+ orderAccountCode +"'";
			
			List<Map<String, Object>> aListMap=DbUp.upTable("mc_member_info").dataSqlList(sql, new MDataMap());
			int aCount = aListMap.size();
			if(aListMap != null && aCount > 0){
				taMemberCode = String.valueOf(aListMap.get(0).get("member_code"));
				String headIconUrl = String.valueOf(aListMap.get(0).get("head_icon_url"));
				//头像
				detailResult.setHeadIconUrl(headIconUrl);
			}
			
			//昵称
			if(accountCode.equals(orderAccountCode)){
				detailResult.setNickName("自己");
			}else{
				
				if(StringUtils.isBlank(taMemberCode)){
					MDataMap membCodeMap=DbUp.upTable("mc_member_info").one("account_code",orderAccountCode);
					if(membCodeMap != null){
						taMemberCode = membCodeMap.get("member_code");
					}
				}
				Map<String,String> nickMap = new HashMap<String,String>();
				nickMap.put("account_code_wo", accountCode);
				nickMap.put("account_code_ta", orderAccountCode);
				nickMap.put("member_code", taMemberCode);
				detailResult.setNickName(NickNameHelper.getNickName(nickMap));
			}

			//好友等级 0:自己 1：一度好友 2:2度好友
			detailResult.setRelationLevel(rLevel);
			
			//获取清分等级
			String accountLevel = "";
			MDataMap acLevelMap=DbUp.upTable("gc_group_account").one("account_code",orderAccountCode);
			if(acLevelMap != null){
				accountLevel = acLevelMap.get("account_level");
				MDataMap acLevelNameMap=DbUp.upTable("gc_group_level").one("level_code",accountLevel);
				if(acLevelNameMap != null){
					detailResult.setAccountLevel(acLevelNameMap.get("level_name"));
				}
			}
			
			//返利金额
			detailResult.setRebateMoney(rebateMoney);
			
			//SKU返利列表
			List<OrderSkuRebateMoneyInfo> rebateSkuMoneyList=new ArrayList<OrderSkuRebateMoneyInfo>();
			List<MDataMap> skuReckonList=DbUp.upTable("gc_reckon_log").queryByWhere("account_code",accountCode,"order_code",orderCode,"reckon_change_type","4497465200030001");
			if(skuReckonList != null && skuReckonList.size() > 0){
				for(int i = 0;i <skuReckonList.size();i++){
					OrderSkuRebateMoneyInfo skuMoneyInfo = new OrderSkuRebateMoneyInfo();
					String reckonMoney = skuReckonList.get(i).get("reckon_money");
					String scaleReckon = skuReckonList.get(i).get("scale_reckon");
//					String skuCode = skuReckonList.get(i).get("sku_code");
//					String skuName = "";
					//获取sku名称
//					if(StringUtils.isNotBlank(skuCode)){
//						String skuSql = "SELECT product_name FROM gc_reckon_order_detail WHERE order_code ='"+orderCode+"' AND (sku_code = '"+skuCode+"' OR " + " product_code='" + skuCode + "') ";
//						List<Map<String, Object>> skuListMap=DbUp.upTable("gc_reckon_order_detail").dataSqlList(skuSql, new MDataMap());
//						if(skuListMap != null && skuListMap.size() > 0){
//							skuName = String.valueOf(skuListMap.get(0).get("product_name"));
//							skuMoneyInfo.setSkuName(skuName);
//							skuMoneyInfo.setScaleReckon(scaleReckon);
//							skuMoneyInfo.setReckonMoney(reckonMoney);
//							rebateSkuMoneyList.add(skuMoneyInfo);
//						}
//					}
					skuMoneyInfo.setSkuName("商品"+ (i+1));
					skuMoneyInfo.setScaleReckon(new BigDecimal(scaleReckon).multiply(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
					skuMoneyInfo.setReckonMoney(reckonMoney);
					rebateSkuMoneyList.add(skuMoneyInfo);
				}
			}
			detailResult.setRebateSkuMoneyList(rebateSkuMoneyList);
			
			//订单金额
			MDataMap acReckonMap=DbUp.upTable("gc_reckon_order_info").one("order_code",orderCode);
			if(acReckonMap != null){
				detailResult.setOrderMoney(acReckonMap.get("order_money"));
			}
			
			//当前返利状态
			detailResult.setRebateStatus(rebateStatus);
			
			//下单APP
			String manageCode = "";
			if(acReckonMap != null){
				manageCode = acReckonMap.get("manage_code");
				if(StringUtils.isNotBlank(manageCode)){
					if(manageCode.startsWith("SI")){
						MDataMap appMap=DbUp.upTable("uc_appinfo").one("app_code",manageCode);
						if(appMap != null){
							detailResult.setOrderApp(appMap.get("app_name"));
						}
					}else if(manageCode.startsWith("APPM")){
						MDataMap appMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",manageCode);
						if(appMap != null){
							String traderCode = appMap.get("trade_code");
							MDataMap traderMap=DbUp.upTable("gc_trader_info").one("trader_code",traderCode);
							if(traderMap != null){
								detailResult.setOrderApp(traderMap.get("trader_name"));
							}
						}
					}
				}
			}
			
			//交易节点 订单状态1.未付款2.未发货3.已发货4.交易成功5.交易失败
			List<OrderTransactionHistoryInfo> orderHistroyList=new ArrayList<OrderTransactionHistoryInfo>();
			if(StringUtils.isNotBlank(orderCode) && StringUtils.isNotBlank(manageCode)){
				List<MDataMap> syncOrderList=DbUp.upTable("gc_sync_order_status").queryByWhere("order_code",orderCode,"manage_code",manageCode);
				if(syncOrderList != null && syncOrderList.size() >0){
					for(int i = 0;i < syncOrderList.size() ;i++){
						OrderTransactionHistoryInfo orderTransInfo = new OrderTransactionHistoryInfo();
						
						String orderStatus = syncOrderList.get(i).get("order_status");
						String updateTime = syncOrderList.get(i).get("update_time");
						
						orderTransInfo.setOrderCode(orderCode);
						orderTransInfo.setTransactionStatus(orderStatus);
						//状态时间展示规则：‘xx-xx hh:mm’（月-日 时-分）用24小时时间制，当交易时间超过当年时显示’xxxx-xx-xx hh:mm’(年-月-日 时-分)
						orderTransInfo.setTransactionTime(GetNewTransTime(updateTime));
						orderHistroyList.add(orderTransInfo);
					}
				}
			}
			detailResult.setOrderHistroyList(orderHistroyList);
		}

		
		return detailResult;
	}

	/**
	 * 依照规则获取状态时间
	 * @param updateTime
	 * @return
	 */
	private String GetNewTransTime(String time) {
		int sysYear = DateUtil.getYear(null);
		String updateTime=time;
		if(StringUtils.isNotEmpty(updateTime)){
			int orderYear = Integer.parseInt(time.substring(0, 4));
			//当交易时间超过当年,xxxx-xx-xx hh:mm(年-月-日 时-分)格式展示  2014-06-05 14:48:13
			if(orderYear < sysYear){
				updateTime = time.substring(0, 16);
			}else{
				//xx-xx hh:mm’（月-日 时-分）
				updateTime = time.substring(2, 16);
			}
		}
		return updateTime;
	}

	/**
	 * 获取账户明细
	 * @param accountCode
	 * @param inputParam
	 * @return
	 */
	public AccountRecordResult ShowAccountRecord(String accountCode,
			AccountRecordInput inputParam) {
		AccountRecordResult recordResult=new AccountRecordResult();
		List<AccountRecordInfo> acRecordList=new ArrayList<AccountRecordInfo>();
		
		//账户明细类型 1:全部明细 2:入账明细 3:提现明细 4:扣款明细
		String recordType = inputParam.getRecordType();
		
		MDataMap accountMap=DbUp.upTable("gc_group_account").one("account_code",accountCode);
		if(accountMap!=null){
			//可提现返利
			recordResult.setAccountWithdrawRebateMoney(accountMap.get("account_withdraw_money"));
			//累计返利 
			recordResult.setTotalRebateMoney(accountMap.get("total_withdraw_money"));
		}
		
		MDataMap whereMap = new MDataMap();
		String ssWhere = " account_code ='"+accountCode+"' ";
		String sWhereInField = "withdraw_change_type";
		String sWhereInFieldVal = "";
		if("2".equals(recordType)){
			//入账
			sWhereInFieldVal = "4497465200040001,4497465200040009,4497465200040010,4497465200040012";//返利,支付退款,人工加钱,任务奖励
		}else if("3".equals(recordType)){
			//提现
			sWhereInFieldVal = "4497465200040002,4497465200040005,4497465200040006";//用户提现,提现单审核失败,提现单支付失败
		}else if("4".equals(recordType)){
			//扣款
			sWhereInFieldVal = "4497465200040003,4497465200040008,4497465200040011";//订单退换货,支付,人工减钱
		}
		
		//账户明细记录分页
		MPageData mPageData= DataPaging.upPageDataQueryIn("gc_withdraw_log", "", "create_time desc", ssWhere, whereMap, inputParam.getPageOption(),sWhereInField,sWhereInFieldVal);
		List<MDataMap> withdrawList=mPageData.getListData();
		if(withdrawList!=null&&withdrawList.size()>0){
			for(MDataMap recordMap:withdrawList){
				AccountRecordInfo acRecordInfo=new AccountRecordInfo();
				String acCode = recordMap.get("account_code");
				String changeCodes = recordMap.get("change_codes");
				String withdrawChangeType = recordMap.get("withdraw_change_type");
				String withdrawMoney = recordMap.get("withdraw_money");
				String createTime = recordMap.get("create_time");
				
				String chgCode = "";
				String[] chgCodeArg = changeCodes.split(",");
				if(chgCodeArg.length >0){
					
					int maxIndex = chgCodeArg.length;
					//人工加减钱,任务奖励除外
					if(!"4497465200040010".equals(withdrawChangeType) && !"4497465200040011".equals(withdrawChangeType) && !"4497465200040012".equals(withdrawChangeType)){
						//扣款时,取最后的change_code
						if("4497465200040003".equals(withdrawChangeType)){
							chgCode = chgCodeArg[maxIndex-1];
						}else{
							chgCode = chgCodeArg[0];
						}
					}
				}
				//订单所属账户信息
				if(StringUtils.isNotBlank(chgCode) && chgCode.startsWith("GCRL")){
					MDataMap logReckonMap=DbUp.upTable("gc_reckon_log").one("log_code",chgCode);
					if(logReckonMap != null){
						String rLevel = logReckonMap.get("relation_level");
						//好友等级 0:自己 1：一度好友 2:2度好友
						if("1".equals(rLevel)){
							acRecordInfo.setRelationLevel("一度好友");
						}else if("2".equals(rLevel)){
							acRecordInfo.setRelationLevel("二度好友");
						}else{
							acRecordInfo.setRelationLevel("本人");
						}
						
						String taMemberCode = "";
						String orderAccountCode  = logReckonMap.get("order_account_code");
						String sql=" SELECT e.member_code,e.head_icon_url,e.nickname "
								+ " FROM membercenter.mc_extend_info_groupcenter e "
								+ " INNER JOIN membercenter.mc_member_info m ON e.member_code = m.member_code AND m.manage_code = 'SI2011' AND m.account_code ='"+ orderAccountCode +"'";
						
						List<Map<String, Object>> aListMap=DbUp.upTable("mc_member_info").dataSqlList(sql, new MDataMap());
						int aCount = aListMap.size();
						if(aListMap != null && aCount > 0){
							taMemberCode = String.valueOf(aListMap.get(0).get("member_code"));
							String headIconUrl = String.valueOf(aListMap.get(0).get("head_icon_url"));
							//头像
							acRecordInfo.setHeadIconUrl(headIconUrl);
						}
						
						//昵称
						if(acCode.equals(orderAccountCode)){
							acRecordInfo.setNickName("自己");
						}else{
							if(StringUtils.isBlank(taMemberCode)){
								MDataMap membCodeMap=DbUp.upTable("mc_member_info").one("account_code",orderAccountCode);
								if(membCodeMap != null){
									taMemberCode = membCodeMap.get("member_code");
								}
							}
							Map<String,String> nickMap = new HashMap<String,String>();
							nickMap.put("account_code_wo", accountCode);
							nickMap.put("account_code_ta", orderAccountCode);
							nickMap.put("member_code", taMemberCode);
							acRecordInfo.setNickName(NickNameHelper.getNickName(nickMap));
						}

					}
				}else if("4497465200040002".equals(withdrawChangeType) || "4497465200040005".equals(withdrawChangeType) || "4497465200040006".equals(withdrawChangeType)
						|| "4497465200040008".equals(withdrawChangeType) || "4497465200040009".equals(withdrawChangeType) || "4497465200040010".equals(withdrawChangeType)
						|| "4497465200040011".endsWith(withdrawChangeType) || "4497465200040012".endsWith(withdrawChangeType) || "4497465200040004".endsWith(withdrawChangeType)){
					//本人提现在gc_reckon_log表中是没有记录的，需要直接获取本人头像，昵称等信息
					if(StringUtils.isNotBlank(acCode)){
						String sql1=" SELECT e.member_code,e.head_icon_url,e.nickname "
								+ " FROM membercenter.mc_extend_info_groupcenter e "
								+ " INNER JOIN membercenter.mc_member_info m ON e.member_code = m.member_code AND m.manage_code = 'SI2011' AND m.account_code ='"+ acCode +"'";
						
						List<Map<String, Object>> aListMap1=DbUp.upTable("mc_member_info").dataSqlList(sql1, new MDataMap());
						int aCount1 = aListMap1.size();
						if(aListMap1 != null && aCount1 > 0){
							//头像
							acRecordInfo.setHeadIconUrl(String.valueOf(aListMap1.get(0).get("head_icon_url")));
						}
						//好友等级
						acRecordInfo.setRelationLevel("本人");
						//昵称
						acRecordInfo.setNickName("自己");
					}
				}
				
				//交易类型
				String transactionType = "";
				if("4497465200040001".equals(withdrawChangeType)){
					transactionType = "下单返利";
				}else if("4497465200040002".equals(withdrawChangeType) || "4497465200040005".equals(withdrawChangeType) ||"4497465200040006".equals(withdrawChangeType)){
					transactionType = "提现";
					//提现失败时 添加标签
					if("4497465200040005".equals(withdrawChangeType) ||"4497465200040006".equals(withdrawChangeType)){
						acRecordInfo.setTransactionlabel("提现失败");
					}
				}else if("4497465200040003".equals(withdrawChangeType) ){
					transactionType = "退货扣款";
				}else if("4497465200040004".equals(withdrawChangeType) ){
					transactionType = "平台调整";
				}else if("4497465200040008".equals(withdrawChangeType) ){
					transactionType = "支付";
				}else if("4497465200040009".equals(withdrawChangeType) ){
					transactionType = "支付退款";
				}else if("4497465200040010".equals(withdrawChangeType) ){
					transactionType = "平台赠送";
				}else if("4497465200040011".equals(withdrawChangeType) ){
					transactionType = "平台扣减";
				}else if("4497465200040012".equals(withdrawChangeType) ){
					transactionType = "任务奖励";
				}
				acRecordInfo.setTransactionType(transactionType);
				
				//交易金额
				acRecordInfo.setTransactionMoney(withdrawMoney);
				
				//交易时间
				acRecordInfo.setTransactionTime(createTime);
				
				acRecordList.add(acRecordInfo);
				
			}
		}
		
		recordResult.setRebateRecordList(acRecordList);
		recordResult.setPageResults(mPageData.getPageResults());
		return recordResult;
	}

	/**
	 * 获取手机号为默认昵称
	 * @return
	 */
	private String GetDefaultNickName(String memberCode) {
		
		MDataMap mUserMap = DbUp.upTable("mc_login_info").one("member_code", memberCode);
		if(mUserMap != null){
			String loginName = mUserMap.get("login_name");
			if(StringUtils.isNotBlank(loginName)){
				return loginName.substring(0, 3) + "****" + loginName.substring(7);
			}
		}
		return "";
	}
	
	/**
	 * 消费详情
	 * @param memberCode
	 * @param getConsumeDetailInput
	 * @return
	 */
	public GetConsumeDetailResult  getConsumeDetail(String memberCode,GetConsumeDetailInput getConsumeDetailInput){
		GetConsumeDetailResult getConsumeDetailResult=new GetConsumeDetailResult();
		getConsumeDetailResult.setMemberCode(getConsumeDetailInput.getMemberCode());
		getConsumeDetailResult.setRelationLevel(getConsumeDetailInput.getRelationLevel());
		List<ConsumeDetail> consumeList=new ArrayList<ConsumeDetail>();
		MDataMap queryMap=new MDataMap();
		String accountCode="";
		String relationAccountCode="";
		MDataMap accountMap=DbUp.upTable("mc_member_info").one("member_code",memberCode);
		if(accountMap==null){
			getConsumeDetailResult.inErrorMessage(918523002);//membercode未找到账户编号
		}
		else{
			accountCode=accountMap.get("account_code");
			queryMap.put("accountCode", accountCode);
		}
		if(getConsumeDetailResult.upFlagTrue()){
			relationAccountCode=accountCode;
			queryMap.put("relationAccountCode", relationAccountCode);
			if(!getConsumeDetailInput.getRelationLevel().equals("0")){
				MDataMap relationMap=DbUp.upTable("mc_member_info").one("member_code",getConsumeDetailInput.getMemberCode());
				if(relationMap==null){
					getConsumeDetailResult.inErrorMessage(918523002);//membercode未找到账户编号
				}
				else{
					relationAccountCode=relationMap.get("account_code");
					queryMap.put("relationAccountCode", relationAccountCode);
				}
			}
		}
		
		String littleSql="";
		if(getConsumeDetailInput.getRelationLevel().equals("1")){
			littleSql=" and order_account_code in (select account_code "
				+ " from gc_member_relation where (parent_code=:relationAccountCode) or (parent_code=:accountCode and account_code=:relationAccountCode) ) ";
		}
		else if(getConsumeDetailInput.getRelationLevel().equals("2")){
			littleSql=" and order_account_code=:relationAccountCode  ";
		}
		
		
		
		
		
		//总消费
		if(getConsumeDetailResult.upFlagTrue()){
			String consumeSql=" select ifnull(sum(consume_money),0) as totalConsume from gc_active_log where account_code=:accountCode ";
			Map<String, Object> totalConsumeMap= DbUp.upTable("gc_active_log").dataSqlOne(consumeSql+littleSql, queryMap);
			if(totalConsumeMap!=null){
				getConsumeDetailResult.setTotalConsume(totalConsumeMap.get("totalConsume").toString());
			}
		}
		
		//总返利
		if(getConsumeDetailResult.upFlagTrue()){
			String rebateSql=" select ifnull(sum(abs(reckon_money)),0) as totalRebate from gc_reckon_log where account_code=:accountCode and reckon_change_type=\"4497465200030004\" ";
			Map<String, Object> totalRebateMap= DbUp.upTable("gc_reckon_log").dataSqlOne(rebateSql+littleSql, queryMap);
			if(totalRebateMap!=null){
				getConsumeDetailResult.setTotalRebate(totalRebateMap.get("totalRebate").toString());
			}
			
		}
		
		//按月份取明细
		if(getConsumeDetailResult.upFlagTrue()){
			//当前月
			String beginMonth="";
			String endMonth=DateHelper
					.upMonth(FormatHelper.upDateTime());
			//取注册日期
			String memberSql="select create_time from mc_login_info where member_code in(select member_code from mc_member_info where account_code=:account_code) order by create_time limit 1";
			Map<String, Object> memberMap=DbUp.upTable("mc_login_info").dataSqlOne(memberSql, new MDataMap("account_code",relationAccountCode));
			if(memberMap!=null&&memberMap.get("create_time")!=null){
				beginMonth=memberMap.get("create_time").toString().substring(0, 7);
			}
			if(beginMonth!=""){
				List<String> monthList=DateHelper.getMonthList(beginMonth, endMonth);
				Collections.reverse(monthList);
				
				//取总消费
				Map<String, String> monthConsumeMap=new HashMap<String, String>();
				String monthConsumeSql=" select ifnull(sum(consume_money),0) as totalConsume,left(active_time,7) as consumeTime from gc_active_log where account_code=:accountCode ";
				String consumeLittleSql=littleSql+" group by left(active_time,7) ";
				List<Map<String, Object>> monthConsumeList=DbUp.upTable("gc_active_log").dataSqlList(monthConsumeSql+consumeLittleSql, queryMap);
				if(monthConsumeList!=null&&monthConsumeList.size()>0){
					for(Map<String, Object> map:monthConsumeList){
						monthConsumeMap.put(map.get("consumeTime").toString(), map.get("totalConsume").toString());
					}
				}
				
				//取返利
				Map<String, String> monthRebateMap=new HashMap<String, String>();
				String monthRebateSql=" select ifnull(sum(abs(reckon_money)),0) as totalRebate,left(order_reckon_time,7) as rebateTime from gc_reckon_log where account_code=:accountCode and reckon_change_type=\"4497465200030004\" ";
				String rebateLittleSql=littleSql+" group by left(order_reckon_time,7) ";
				List<Map<String, Object>> monthRebateList=DbUp.upTable("gc_reckon_log").dataSqlList(monthRebateSql+rebateLittleSql, queryMap);
				if(monthRebateList!=null&&monthRebateList.size()>0){
					for(Map<String, Object> map:monthRebateList){
						monthRebateMap.put(map.get("rebateTime").toString(), map.get("totalRebate").toString());
					}
				}
				
				Map<String, String> roleMonthConsumeMap=new HashMap<String, String>();
				Map<String, String> roleMonthRebateMap=new HashMap<String, String>();
				//二度好友时不用单独计算
				if(!getConsumeDetailInput.getRelationLevel().equals("2")){
					//取当前人的消费
					String oneConsumeSql=" and order_account_code=:relationAccountCode group by left(active_time,7) ";
					List<Map<String, Object>> roleMonthConsumeList=DbUp.upTable("gc_active_log").dataSqlList(monthConsumeSql+oneConsumeSql, queryMap);
					if(roleMonthConsumeList!=null&&roleMonthConsumeList.size()>0){
						for(Map<String, Object> map:roleMonthConsumeList){
							roleMonthConsumeMap.put(map.get("consumeTime").toString(), map.get("totalConsume").toString());
						}
						
					}
					
				   //取当前人的返利
					String oneRebateSql=" and order_account_code=:relationAccountCode group by left(order_reckon_time,7) ";
					List<Map<String, Object>> roleMonthRebateList=DbUp.upTable("gc_reckon_log").dataSqlList(monthRebateSql+oneRebateSql, queryMap);
					if(roleMonthRebateList!=null&&roleMonthRebateList.size()>0){
						for(Map<String, Object> map:roleMonthRebateList){
							roleMonthRebateMap.put(map.get("rebateTime").toString(), map.get("totalRebate").toString());
						}
					}
				}
				
				
				
				for(String oneMonth:monthList){
				    ConsumeDetail consumeDetail=new ConsumeDetail();
				    consumeDetail.setDate(oneMonth);
				    consumeDetail.setConsumeOne(monthConsumeMap.get(oneMonth)==null?"0":monthConsumeMap.get(oneMonth));
				    consumeDetail.setRebateOne(monthRebateMap.get(oneMonth)==null?"0":monthRebateMap.get(oneMonth));
				    if(!getConsumeDetailInput.getRelationLevel().equals("2")){
				    	consumeDetail.setConsumeTwo(roleMonthConsumeMap.get(oneMonth)==null?"0":roleMonthConsumeMap.get(oneMonth));
					    consumeDetail.setConsumeThree(new BigDecimal(consumeDetail.getConsumeOne()).subtract(new BigDecimal(consumeDetail.getConsumeTwo())).toString());
					    consumeDetail.setRebateTwo(roleMonthRebateMap.get(oneMonth)==null?"0":roleMonthRebateMap.get(oneMonth));
					    consumeDetail.setRebateThree(new BigDecimal(consumeDetail.getRebateOne()).subtract(new BigDecimal(consumeDetail.getRebateTwo())).toString());
				    }
				    consumeList.add(consumeDetail);
				}
				getConsumeDetailResult.setConsumeList(consumeList);
			}
			
		}
		
		return getConsumeDetailResult;
	}
	
	/**
	 * 第三方返利对账
	 * @param manageCode
	 * @param groupRebateRecordInput
	 * @return
	 */
	public GroupRebateRecordResult thirdRebateRecord(String manageCode,GroupRebateRecordInput groupRebateRecordInput){
		GroupRebateRecordResult groupRebateRecordResult=new GroupRebateRecordResult();
		List<GroupRebateRecordList> rebateRecordList=new ArrayList<GroupRebateRecordList>();
		MDataMap queryMap=new MDataMap();
		queryMap.inAllValues("startTime",groupRebateRecordInput.getStartTime(),"endTime",groupRebateRecordInput.getEndTime(),"manageCode",manageCode);
		String sql="select reb.* from gc_rebate_order reb "
				+ "where reb.order_create_time>=:startTime and reb.order_create_time<=:endTime and reb.manage_code=:manageCode";
		List<Map<String, Object>> rebateList=DbUp.upTable("gc_rebate_order").dataSqlList(sql, queryMap);
		if(rebateList!=null&&rebateList.size()>0){
			for(Map<String, Object> rebateMap:rebateList){
				GroupRebateRecordList groupRebateRecordList=new GroupRebateRecordList();
				List<RebateProductDetail> rebateProductList=new ArrayList<RebateProductDetail>();
				MDataMap oneMap=new MDataMap();
				oneMap.inAllValues("account_code",rebateMap.get("account_code").toString(),"orderCode",rebateMap.get("order_code").toString());
				String oneSql="select rec.sku_code,rec.reckon_money,det.product_code,det.price_reckon,det.product_number from gc_reckon_log rec left join gc_reckon_order_detail det on rec.sku_code=det.sku_code and rec.order_code=det.order_code "
						+ "where rec.account_code=:account_code and rec.order_code=:orderCode and rec.reckon_change_type=\"4497465200030001\" ";
				List<Map<String, Object>> detailList=DbUp.upTable("gc_reckon_log").dataSqlList(oneSql, oneMap);
				if(detailList!=null&&detailList.size()>0){
					for(Map<String, Object> detailMap:detailList){
						RebateProductDetail rebateProductDetail=new RebateProductDetail();
						rebateProductDetail.setProductCode(detailMap.get("product_code").toString());
						rebateProductDetail.setSkuCode(detailMap.get("sku_code").toString());
						rebateProductDetail.setProductNum(detailMap.get("product_number").toString());
						rebateProductDetail.setRebateMoney(detailMap.get("reckon_money").toString());
						rebateProductDetail.setReckonMoney(detailMap.get("price_reckon").toString());
						rebateProductList.add(rebateProductDetail);
					}
					groupRebateRecordList.setRebateProductList(rebateProductList);
				}
				
				groupRebateRecordList.setBusinessCode(WebTemp.upTempDataOne("uc_appinfo", "app_name", "app_code",rebateMap.get("manage_code").toString()));
				groupRebateRecordList.setChannel("" );
				groupRebateRecordList.setMoney(rebateMap.get("rebate_money").toString());
				groupRebateRecordList.setOrderCode(rebateMap.get("order_code").toString());
				groupRebateRecordList.setTime(rebateMap.get("order_create_time").toString());
				groupRebateRecordList.setStatus(WebTemp.upTempDataOne("sc_define", "define_name", "define_code",rebateMap.get("rebate_status").toString()));
			    String description="";
		        String preDay="";
		        //已付款
		        if(rebateMap.get("rebate_status").toString().equals("4497465200170002")){
		        	if(StringUtils.isNotBlank(rebateMap.get("order_send_time").toString())){
		        		preDay=DateHelper.upDateTimeAdd(DateHelper.parseDate(rebateMap.get("order_send_time").toString()),Calendar.DATE,14);
		        		description="预计"+preDay.substring(5, 7)+"月"+preDay.substring(8, 10)+"日"+"返利";
		        	}
		        }
		        //已取消
		        else if(rebateMap.get("rebate_status").toString().equals("4497465200170003")){
		        	if(StringUtils.isNotBlank(rebateMap.get("order_cancel_time").toString())){
		        		description=rebateMap.get("order_cancel_time").toString().substring(5, 7)+"月"+rebateMap.get("order_cancel_time").toString().substring(8, 10)+"日"+"取消";
		        	}
		        }
		        //已返利
		        else if(rebateMap.get("rebate_status").toString().equals("4497465200170004")){
		        	if(StringUtils.isNotBlank(rebateMap.get("order_finish_time").toString())){
		        		preDay=DateHelper.upDateTimeAdd(DateHelper.parseDate(rebateMap.get("order_finish_time").toString()),Calendar.DATE,9);
		        		description=preDay.substring(5, 7)+"月"+preDay.substring(8, 10)+"日"+"返利";
		        	}
		        }
				groupRebateRecordList.setDescription(description);
				MDataMap memberMap= DbUp.upTable("mc_member_info").one("account_code",rebateMap.get("account_code").toString(),"manage_code",rebateMap.get("manage_code").toString(),"flag_enable","1");
				if(memberMap!=null){
					groupRebateRecordList.setMemberCode(memberMap.get("member_code"));
				}
				
				rebateRecordList.add(groupRebateRecordList);
			}
			groupRebateRecordResult.setRebateRecordList(rebateRecordList);
		}
		
		return groupRebateRecordResult;
	}
}
