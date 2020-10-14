package com.cmall.ordercenter.tallyorder.settleperiod;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 订单结算：周期业务实现
 * @author pang_jhui
 *
 */
public class OcAccountPeriodService extends BaseClass {
	
	public void calPeriodDate(MWebResult mWebResult ,MDataMap mDataMap){
		
		Calendar saleBeginCalendar = null;
		
		Calendar saleEndCalendar = null;
		
		Calendar returnStartCalendar = null;
		
		Calendar returnEndCalendar = null;
		
		if(StringUtils.isNotBlank(mDataMap.get("account_day"))){
			
			Calendar accountCalendar = calStatDate("", mDataMap.get("account_day"), false);
			
			mDataMap.put("account_date", DateUtil.toString(accountCalendar.getTime(), "yyyy-MM-dd"));
			
		}
		
		
		if(StringUtils.isNotBlank(mDataMap.get("sale_begin_month"))
				&& StringUtils.isNotBlank(mDataMap.get("sale_begin_day"))){
			
			saleBeginCalendar = calStatDate(mDataMap.get("sale_begin_month"), mDataMap.get("sale_begin_day") ,false);
			
			mDataMap.put("sale_begin_date", DateUtil.toString(saleBeginCalendar.getTime(), "yyyy-MM-dd HH:mm:ss"));
			
		}
		
		if(StringUtils.isNotBlank(mDataMap.get("sale_end_month"))
				&& StringUtils.isNotBlank(mDataMap.get("sale_end_day"))){
			
			saleEndCalendar = calStatDate(mDataMap.get("sale_end_month"), mDataMap.get("sale_end_day") ,true);
			
			mDataMap.put("sale_end_date", DateUtil.toString(saleEndCalendar.getTime(), "yyyy-MM-dd HH:mm:ss"));
			
		}
		
		if(StringUtils.isNotBlank(mDataMap.get("return_begin_month"))
				&& StringUtils.isNotBlank(mDataMap.get("return_begin_day"))){
			
			returnStartCalendar = calStatDate(mDataMap.get("return_begin_month"), mDataMap.get("return_begin_day") ,false);
			
			mDataMap.put("return_begin_date", DateUtil.toString(returnStartCalendar.getTime(), "yyyy-MM-dd HH:mm:ss"));
			
		}
		
		if(StringUtils.isNotBlank(mDataMap.get("return_end_month"))
				&& StringUtils.isNotBlank(mDataMap.get("return_end_day"))){
			
			returnEndCalendar = calStatDate(mDataMap.get("return_end_month"), mDataMap.get("return_end_day") ,true);
			
			mDataMap.put("return_end_date", DateUtil.toString(returnEndCalendar.getTime(), "yyyy-MM-dd HH:mm:ss"));
			
		}
		
		mDataMap.put("state", "1");
		
		/*校验结束日期是否早于开始日期*/
		validteDate(mWebResult, saleBeginCalendar, saleEndCalendar, returnStartCalendar, returnEndCalendar);
		
	}
	
	/**
	 * 计算统计日期
	 * @param month
	 * 		统计月份
	 * @param day
	 * 		统计日
	 * @param flag
	 * 		是否闭区间
	 */
	public Calendar calStatDate(String month ,String day ,boolean flag){
	
		Calendar currCalendar = Calendar.getInstance();
		
		int calday;
				
		if(StringUtils.equals(month, "上月")){
			
			currCalendar.add(Calendar.MONTH, -1);		
			
		}
				
		if(StringUtils.equals(day, "月末")){
			
			calday = currCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			
		}else{
			
			calday = Integer.parseInt(day);
			
		}
		
		currCalendar.set(Calendar.DATE, calday);
		
		currCalendar.set(Calendar.HOUR_OF_DAY, 0);
		currCalendar.set(Calendar.MINUTE, 0);
		currCalendar.set(Calendar.SECOND, 0);
		
		if(flag){

			currCalendar.add(Calendar.DATE, 1);
			
			currCalendar.add(Calendar.SECOND, -1);
			
		}
		
		return currCalendar;
		
	}
	
	
	
	/**
	 * 将数据状态置为0
	 * @param uid
	 * 		唯一标识
	 */
	public void delete(String tableName,String uid){
		
		MDataMap mDataMap = DbUp.upTable(tableName).one("uid",uid);
		
		if(mDataMap != null){
			
			mDataMap.put("state", "0");
			
			DbUp.upTable(tableName).update(mDataMap);
			
		}
		
	}
	
	/**
	 * 根基统计日期查询
	 * @param day
	 * 		统计日期
	 * @return
	 */
	public MDataMap queryByPeriod(String date, String accountType){
		if(StringUtils.isEmpty(accountType)) {
			return DbUp.upTable("oc_bill_account_period").one("account_day",date, "state","1");			
		} else {
			return DbUp.upTable("oc_bill_account_period").one("account_day",date, "account_type",accountType, "state","1");			
		}
	}
	
	/**
	 * 获取结算周期
	 * @return MDataMap
	 * 		结算周期配置信息
	 */
	public MDataMap getSettlePeriod(){
		
		String date = Integer.toString(Calendar.getInstance().get(Calendar.DATE));
		
		if(date.length() == 1){
			
			date = "0"+date; 
			
		}
		
		MDataMap mDataMap = queryByPeriod(date, null);
		
		
		if(mDataMap != null){
			
			MWebResult mWebResult = new MWebResult();
			
			calPeriodDate(mWebResult, mDataMap);
			
			if(!mWebResult.upFlagTrue()){
				
				mDataMap = null;
				
			}
			
		}
		
		
		return mDataMap;
		
	}
	
	/**
	 * 校验日历的大小
	 * @param calendars
	 *   日历集合
	 */
	public void validteDate(MWebResult mWebResult ,Calendar... calendars){
		
		List<String> list = new ArrayList<String>();
		
		String msg = "结束日期不能早于开始日期，请核对";
		
		if(calendars[0].compareTo(calendars[1]) > 0){
			
			list.add("销售统计");
			
		}
		
		if(calendars[2].compareTo(calendars[3]) > 0){
			
			list.add("退货统计");
			
		}
		
		if(list.size() > 0){
			
			
			mWebResult.setResultCode(-1);
			
			if(list.size() == 2){
				
				mWebResult.setResultMessage(list.get(0)+"与"+list.get(1)+msg);
				
			}
			
			if(list.size() == 1){
				
				mWebResult.setResultMessage(list.get(0)+msg);
			}
			
		}
		
	}

}
