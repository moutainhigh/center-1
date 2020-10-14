package com.cmall.ordercenter.util;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

public class DateNewUtils {
	public static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static final SimpleDateFormat dfd = new SimpleDateFormat("yyyyMMddHHmmss");
	
	/**
	 * 获取当前时间
	 * @param hour
	 * @return
	 */
	public static String nowDate() {
		Date d = new Date();
		String dateMinusOneHours = df.format(new Date(d.getTime()));
		//System.out.println("三天后的日期：" + df.format(new Date(d.getTime() + 3 * 24 * 60 * 60 * 1000)));
		return dateMinusOneHours;
	}
	/**
	 * 获取当前时间  格式yyyyMMddHHmmss
	 * @return
	 */
	public static String nowDateDouble(){
		Date d = new Date();
		String dateMinusOneHours = dfd.format(new Date(d.getTime()));
		//System.out.println("三天后的日期：" + df.format(new Date(d.getTime() + 3 * 24 * 60 * 60 * 1000)));
		return dateMinusOneHours;
	}
	/**
	 * 减当前时间
	 * @param hour
	 * @return
	 */
	public static String minusDate(int hour) {
		Date d = new Date();
		String dateMinusOneHours = df.format(new Date(d.getTime() -  hour*(60 * 60 * 1000)));
		//System.out.println("三天后的日期：" + df.format(new Date(d.getTime() + 3 * 24 * 60 * 60 * 1000)));
		return dateMinusOneHours;
	}
	
	
	/**
	 * 增加分钟
	 * @param hour
	 * @return
	 */
	public static String addMinute(int min) {
		Date d = new Date();
		String dateMinusOneHours = df.format(new Date(d.getTime() +  min * 60 * 1000));
		//System.out.println("三天后的日期：" + df.format(new Date(d.getTime() + 3 * 24 * 60 * 60 * 1000)));
		//System.out.println(dateMinusOneHours);
		return dateMinusOneHours;
	}
	
	/**
	 * 指定的时间  加上天数
	 * @param time   传入时间
	 * @param day   相加的天数
	 * @return
	 * @throws ParseException
	 */
	public static String addDay(String time,int day) {
		try {
			Date date = df.parse(time);
			String dateMinusOneHours = df.format(new Date(date.getTime() +  day * 24 * 60 * 60 * 1000));
			//System.out.println("三天后的日期：" + df.format(new Date(d.getTime() + 3 * 24 * 60 * 60 * 1000)));
			//System.out.println(dateMinusOneHours);
			return dateMinusOneHours;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 增加天数
	 * @param time
	 * @param day
	 * 
	 * 
	 * @return   返回的时间类型为yyyyMMddHHmmss
	 */
	public static String addDayNew(String time,int day) {
		try {
			Date date = dfd.parse(time);
			String dateMinusOneHours = dfd.format(new Date(date.getTime()+  day * 24 * 60 * 60 * 1000));  // 
			//System.out.println("三天后的日期：" + df.format(new Date(d.getTime() + 3 * 24 * 60 * 60 * 1000)));
			//System.out.println(dateMinusOneHours);
			return dateMinusOneHours;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	
		
	}
	/**
	 * 指定的时间  加上分钟
	 * @param time
	 * @param min
	 * @return    格式为：yyyy-MM-dd HH:mm:ss
	 */
	public static String addMin(String time,int min) {
		try {
			Date date = df.parse(time);
			String dateMinusOneHours = df.format(new Date(date.getTime() +  min * 60 * 1000));
			//System.out.println("三天后的日期：" + df.format(new Date(d.getTime() + 3 * 24 * 60 * 60 * 1000)));
			//System.out.println(dateMinusOneHours);
			return dateMinusOneHours;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 指定的时间  加上分钟
	 * @param time
	 * @param min
	 * @return    格式为：yyyyMMddHHmmss
	 */
	public static String addMinNew(String time,int min) {
		try {
			Date date = dfd.parse(time);
			String dateMinusOneHours = dfd.format(new Date(date.getTime() +  min * 60 * 1000));
			//System.out.println("三天后的日期：" + df.format(new Date(d.getTime() + 3 * 24 * 60 * 60 * 1000)));
			//System.out.println(dateMinusOneHours);
			return dateMinusOneHours;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 比较时间大小
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	public static boolean compareTime(String beginTime,String endTime){
		try {
			Date beginDate = df.parse(beginTime);
			Date endDate = df.parse(endTime);
			if(beginDate.getTime() > endDate.getTime()){
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static void main(String[] args) throws ParseException {
//		System.out.println(addDayNew("2015-08-21 11:09:53",1));  //2015-08-21 11:09:53
//		System.out.println(nowDateDouble());
//		System.out.println(addDayNew(nowDateDouble(),1));
		//System.out.println(new BigDecimal("2014-12-17 15:15:02").compareTo(new BigDecimal("2014-12-17 15:15:03")));
		Map<String,Object> map = DbUp.upTable("oc_orderinfo").dataSqlOne("select * from oc_orderinfo where order_code=:order_code", new MDataMap("order_code","DD140119100002"));
		System.out.println(addMinNew(String.valueOf(map.get("create_time")).replace("-", "").replace(":", "").replace(" ", ""),15));  //2015-08-21 11:09:53
		System.out.println(String.valueOf(map.get("create_time")).replace("-", "").replace(":", "").replace(" ", ""));
		System.out.println("================================");
//		System.out.println(String.valueOf(map.get("create_time")).replaceAll("-", ""));
	}

}
