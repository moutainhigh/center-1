package com.cmall.ordercenter.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatUtil {
  
	
	public static DecimalFormat df = new DecimalFormat("0");// 格式化 number String
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 格式化日期字符串
	public static DecimalFormat nf = new DecimalFormat("0.00");// 格式化数字
	
	public String dateToString(Date date){
		return sdf.format(date);
	}
	
	/**
	 * 比较当前时间是否在有效时间内
	 * @param 
	 * @return
	 */
	public static boolean getTimefag(String now_time,String time){
		java.util.Date date1 = null;
		java.util.Date date2 = null;
		try {
			date1 = convertToDate(now_time, "yyyy-MM-dd HH:mm:ss");
			date2 = convertToDate(time, "yyyy-MM-dd HH:mm:ss");

		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(date1.compareTo(date2)==1){
			return true;
		}else{
			return false;
		}

	}
	
	public static java.util.Date convertToDate(String date,String sysFormat) throws ParseException{
		SimpleDateFormat sysDateTime = new SimpleDateFormat(sysFormat);
		return (java.util.Date) sysDateTime.parse(date);
	}
	
	/**
	 * getNowTime:(获取当前系统时间). <br/>
	 * @author hexd
	 * @return
	 * @since JDK 1.6
	 */
	public static String getNowTime()
	{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String nowTime = df.format(new java.util.Date());
		return nowTime;
	}
}
