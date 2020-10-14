package com.cmall.groupcenter.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {
	/**
	 * 减当前时间
	 * @param hour
	 * @return
	 */
	public static String minusDate(int hour) {
		Date d = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateMinusOneHours = df.format(new Date(d.getTime() +  min * 60 * 1000));
		//System.out.println("三天后的日期：" + df.format(new Date(d.getTime() + 3 * 24 * 60 * 60 * 1000)));
		//System.out.println(dateMinusOneHours);
		return dateMinusOneHours;
	}
	
	public static void main(String[] args) {
		addMinute(2);
	}
	/**
	 * 返回几种时间格式:刚刚发表|5分钟前|3个小时前|1天前|1周前|1个月前
	 * @param dateTimeStamp : yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String getDateDiff(String dateTimeStamp) {
		String result = "";
		long minute = 1000 * 60;
		long hour = minute * 60;
		long day = hour * 24;
		long halfamonth = day * 15;
		long month = day * 30;
		long now = new Date().getTime();
		long diffValue = 0;
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			diffValue = now - format.parse(dateTimeStamp).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(diffValue < 0){
		 //若日期不符则弹出窗口告之
			return "";
		} else {
			long monthC =diffValue/month;
			long weekC =diffValue/(7*day);
			long dayC =diffValue/day;
			long hourC =diffValue/hour;
			long minC =diffValue/minute;
			if(monthC>=1){
				 result= monthC + "个月前";
			} else if(weekC>=1){
				 result= weekC + "周前";
			} else if(dayC>=1){
				 result= dayC +"天前";
			} else if(hourC>=1){
				 result= hourC +"个小时前";
			} else if(minC>=1){
				 result= minC +"分钟前";
			} else {
				 result="刚刚发表";
			}
			return result;
		}
	}
	
	/**
	 * 格式化时间
	 * @require 一个小时之内的显示x分钟前，超过一个小时已时间的格式（yyyy-MM-dd HH:mm:ss）
	 * @param date
	 * @return
	 */
	public static String formatDate4PostComment(Date date) {
		String res = null;
		long oneMinute = 1000 * 60;
		long oneHour = oneMinute * 60;
		long betweenTimes = 0;
		long now = new Date().getTime();
		long dateTime = date.getTime();
		//时间大于1小时
		if((betweenTimes = now - dateTime) > oneHour) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			res = sdf.format(date);
		} else {
//			res = betweenTimes / oneMinute > 0 ? (betweenTimes / oneMinute) + "分钟前" : "刚刚";
			res = (betweenTimes / oneMinute) + "分钟前";
		}
		return res;
	}
}
