package com.cmall.groupcenter.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


/**
 * 各种处理日期等工具的方法
 *
 * @author lipengfei
 * @date 2015-6-2
 * email:lipf@163.com
 *
 */
public class CalendarHelper {

	/**
	 * 计算两个日期之间相差的月份。
	 *  @author lipengfei
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int calculateMonthIn(Date startDate, Date endDate) {
		Calendar cal1 = new GregorianCalendar();
		cal1.setTime(endDate);
		Calendar cal2 = new GregorianCalendar();
		cal2.setTime(startDate);
		int c = (cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR)) * 12
				+ cal1.get(Calendar.MONTH) - cal2.get(Calendar.MONTH);
		return c;
	}
	
	
	/**
	 * 超过一天，一周内显示周几；超过一周，显示日期；当天会按照24小时制显示。
	 * <p>默认的传入的时间格式为yyyy-MM-dd HH:mm:ss
	 * <p>如果需要自定义传入的格式，可以使用{@link #convertDateToSimpleString(String, String)};
	 * @author lipengfei
	 * @date 2015-6-2
	 * @param dateString
	 * @return
	 */
	public static String convertDateToSimpleString(String dateString){
		return convertDateToSimpleString(dateString, "yyyy-MM-dd HH:mm:ss");
	}


	/**
	 * 超过一天，一周内显示周几；超过一周，显示日期；当天会按照24小时制显示。
	 * @author lipengfei
	 * @date 2015-6-2
	 * @param dateString
	 * @param dateFormate
	 * @return
	 */
	public static String convertDateToSimpleString(String dateString,String dateFormate){
		String value="";
		
		Date valueDate = String2Date(dateString, dateFormate);
		Date now = new Date();
		
		float diff = calculateDayIn(valueDate, now);
		
		//如果大于一天，则继续判断，否则显示小时数
		if(diff>1f){
			
			if(diff>7f){//如果大于一周，若大于，则显示日期，否则显示周几
				
				value = Date2String(valueDate, "MM-dd");
			
			}else {
				value = getWeek(valueDate);
			}
			
		}else{
			value = Date2String(valueDate, "HH:mm");
		}
		
		return value;
	}

	/**
	 * 计算两个日期之间相差的天数。
	 *  @author lipengfei
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static float calculateDayIn(Date startDate, Date endDate) {
		float quot = 0;
		Calendar cal1 = new GregorianCalendar();
		cal1.setTime(endDate);
		Calendar cal2 = new GregorianCalendar();
		cal2.setTime(startDate);
		quot = endDate.getTime() - startDate.getTime();
		quot = quot / 1000 / 60 / 60 / 24;
		return quot;
	}
	
	
	
	/**
	 * 
	 * @author 原文出处：http://blog.csdn.net/cselmu9/article/details/8625530
	 * @date 2015-6-2
	 * @param date
	 * @return
	 */
    public static String getWeek(Date date){
    	String[] weeks = {"周日","周一","周二","周三","周四","周五","周六"};
    	return getWeek(date,weeks);
    }
    
    /**
     * @author lipengfei  由原文重载
     * @date 2015-6-2
     * @param date
     * @param weeks 每一天所对应的字典，如 {"周日","周一","周二","周三","周四","周五","周六"}
     * @return
     */
    public static String getWeek(Date date,String[] weeks){ 
        
        Calendar cal = Calendar.getInstance();  
        cal.setTime(date);  
        
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1; 
        
        if(week_index<0){
            week_index = 0;  
        }
        return weeks[week_index];  
    
    }  

	/**
	 * 得到N天之后的日期
	 *  @author lipengfei
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static Date getDateAfter(Date date, int day) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, day); // 表示加了day天
		return c.getTime();
	}
	
	
	/**
	 * 字符串转Date类型
	 * <p> 默认格式为yyyy-MM-dd,  如果需要自定义，请参考{@link CalendarHelper#String2Date(String, String)}
	 * @author lipengfei
	 * @date May 10, 2015
	 * @param str
	 * @return
	 */
	public static Date String2Date(String str) {
		if (str != null) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date date = null;
			try {
				date = format.parse(str);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return date;
		}
		return null;
	}

	/**
	 * 字符串转Date类型
	 * @author lipengfei
	 * @date May 10, 2015
	 * @param str
	 * @param formatStr 格式
	 * @return
	 */
	public static Date String2Date(String str, String formatStr) {
		Date date = null;
		if (str != null) {
			SimpleDateFormat format = new SimpleDateFormat(formatStr);
			try {
				date = format.parse(str);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return date;
	}

	/**
	 * 
	 * @author lipengfei 2013-6-7
	 * @param date
	 * @return
	 */
	public static String Date2String(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String str = "";

		str = format.format(date);

		return str;
	}

	/**
	 * 字符串转date
	 * 
	 * @author lipengfei 2013-6-7
	 * @param date
	 * @param formatStr
	 * @return
	 */
	public static String Date2String(Date date, String formatStr) {
		SimpleDateFormat format = new SimpleDateFormat(formatStr);
		String str = "";
		str = format.format(date);
		return str;
	}

	/**
	 * 按照每个月来取 反之，返回每月的list，如2012-05-13 ~ 2012-07-13 则list的值为2012-05-13
	 * ,2012-06-13,2012-07-13
	 * 
	 * @author lipengfei 2013-6-7
	 * @param star
	 * @param end
	 * @return
	 * 
	 */
	public static List<Date> dateListByMonth(Date startDate, Date endDate) {
		Calendar cal1 = new GregorianCalendar();
		Calendar cal2 = new GregorianCalendar();
		List<Date> listDate = new ArrayList<Date>();

		cal1.setTime(startDate);
		cal2.setTime(endDate);

		// if(cal1.get(Calendar.YEAR)!=cal2.get(Calendar.YEAR)){//判断是不是同一年
		// int c = cal2.get(Calendar.YEAR)- cal1.get(Calendar.YEAR);//相差的年份
		//			  
		// cal1.set(Calendar.DAY_OF_MONTH,1);
		// cal1.set(Calendar.MONTH,0);
		//			  
		// listDate.add(cal1.getTime());//先把起始的月份存入
		// for (int i = 0; i < c; i++) {
		// // cal1.add(Calendar.YEAR, 1);
		// // listDate.add(cal1.getTime());
		//				  
		//				  
		// }
		// }
		if (cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
				&& cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) {
			// 如果月份相等，则返回当月第一天日期;
			cal1.set(Calendar.DAY_OF_MONTH, 1);
			listDate.add(cal1.getTime());
		} else {
			int c = (cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR)) * 12
					+ cal2.get(Calendar.MONTH) - cal1.get(Calendar.MONTH); // 相差的月份
			cal1.set(Calendar.DAY_OF_MONTH, 1);// 初始化为每月的第1天
			listDate.add(cal1.getTime());
			for (int i = 0; i < c; i++) {
				cal1.add(Calendar.MONTH, 1);
				listDate.add(cal1.getTime());
			}
		}
		return listDate;
	}

	/**
	 * 按照每个月来取 反之，返回每月的list，如2012-05-13 ~ 2012-07-13 则list的值为2012-05-13
	 * ,2012-06-13,2012-07-13
	 * 
	 * @author lipengfei 2013-6-7
	 * @param star
	 * @param end
	 * @return
	 * 
	 */
	public static List<Date> dateListByYear(Date startDate, Date endDate) {
		Calendar cal1 = new GregorianCalendar();
		Calendar cal2 = new GregorianCalendar();
		List<Date> listDate = new ArrayList<Date>();

		cal1.setTime(startDate);
		cal2.setTime(endDate);

		if (cal1.get(Calendar.YEAR) != cal2.get(Calendar.YEAR)) {// 判断是不是同一年
			int c = cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR);// 相差的年份

			cal1.set(Calendar.DAY_OF_MONTH, 1);
			cal1.set(Calendar.MONTH, 0);

			listDate.add(cal1.getTime());// 先把起始的年份存入

			for (int i = 0; i < c; i++) {
				cal1.add(Calendar.YEAR, 1);
				listDate.add(cal1.getTime());
			}
		} else {
			cal1.set(Calendar.DAY_OF_MONTH, 1);
			cal1.set(Calendar.MONTH, 0);
			// cal1.add(Calendar.YEAR, 1);
			listDate.add(cal1.getTime()); // 每年的1月1日
		}
		return listDate;
	}

	/**
	 * 得到当月最后一天日期
	 * 
	 * @author lipengfei 2013-6-7
	 * @return
	 */
	public static Date getLastDay(Date date) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(Calendar.MONTH, 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		return cal.getTime();
	}

	/**
	 * 得到当月第一天日期
	 * 
	 * @author lipengfei 2013-6-7
	 * @return
	 */
	public static Date getFirstDay(Date date) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return cal.getTime();
	}

	/**
	 * 计算两个日期之间相差的月数
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int getBetweenMonths(Date date1, Date date2) {
		int iMonth = 0;
		int flag = 0;
		try {
			Calendar objCalendarDate1 = Calendar.getInstance();
			objCalendarDate1.setTime(date1);

			Calendar objCalendarDate2 = Calendar.getInstance();
			objCalendarDate2.setTime(date2);

			if (objCalendarDate2.equals(objCalendarDate1))
				return 0;
			if (objCalendarDate1.after(objCalendarDate2)) {
				Calendar temp = objCalendarDate1;
				objCalendarDate1 = objCalendarDate2;
				objCalendarDate2 = temp;
			}
			if (objCalendarDate2.get(Calendar.DAY_OF_MONTH) < objCalendarDate1
					.get(Calendar.DAY_OF_MONTH))
				flag = 1;

			if (objCalendarDate2.get(Calendar.YEAR) > objCalendarDate1
					.get(Calendar.YEAR))
				iMonth = ((objCalendarDate2.get(Calendar.YEAR) - objCalendarDate1
						.get(Calendar.YEAR))
						* 12 + objCalendarDate2.get(Calendar.MONTH) - flag)
						- objCalendarDate1.get(Calendar.MONTH);
			else
				iMonth = objCalendarDate2.get(Calendar.MONTH)
						- objCalendarDate1.get(Calendar.MONTH) - flag;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return iMonth;
	}

	/**
	 * 计算传入的日期参数再增加years年之后的时间是否比现在的时间大，大则true，否则为false
	 * 
	 * @author lipengfei 2013-6-8
	 * @param starDate
	 * @param years
	 * @return
	 */
	public static boolean ifBiggerThanNow(Date starDate, int years) {
		Calendar cal = Calendar.getInstance();
		Date now = new Date();
		cal.setTime(starDate);
		cal.add(Calendar.YEAR, years);
		if (cal.getTime().getTime() >= now.getTime()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断当前日期是不是今天
	 * 
	 * @author lipengfei2013-7-4
	 * @param date
	 * @return
	 */
	public static boolean ifToday(Date date) {
		boolean ifToday = true;
		try {
			Date now = new Date();
			Calendar calNow = Calendar.getInstance();
			Calendar cal = Calendar.getInstance();

			calNow.setTime(now);
			cal.setTime(date);
			if (calNow.get(Calendar.YEAR) != cal.get(Calendar.YEAR)) {
				ifToday = false;
			} else if (calNow.get(Calendar.MONTH) != cal.get(Calendar.MONTH)) {
				ifToday = false;
			} else if (calNow.get(Calendar.DAY_OF_MONTH) != cal
					.get(Calendar.DAY_OF_MONTH)) {
				ifToday = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ifToday;
	}
	
	/**
	 * 判断当前日期是不是同一天
	 * 
	 * @author lipengfei2013-7-4
	 * @param date
	 * @return
	 */
	public static boolean ifSameDay(Date date1,Date date2) {
		boolean ifToday = true;
		try {
			Date now = date1;
			Calendar calNow = Calendar.getInstance();
			Calendar cal = Calendar.getInstance();
			
			calNow.setTime(now);
			cal.setTime(date2);
			if (calNow.get(Calendar.YEAR) != cal.get(Calendar.YEAR)) {
				ifToday = false;
			} else if (calNow.get(Calendar.MONTH) != cal.get(Calendar.MONTH)) {
				ifToday = false;
			} else if (calNow.get(Calendar.DAY_OF_MONTH) != cal
					.get(Calendar.DAY_OF_MONTH)) {
				ifToday = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ifToday;
	}

	/**
	 * 获得指定日期的那一周的时间段
	 * 
	 * @author lipengfei2013-7-6
	 * @param currDate
	 * @return
	 */
	public static Date[] getWeekDate(Date currDate) {
		Calendar cal = Calendar.getInstance();
		Date[] date = new Date[2];

		cal.setTime(currDate);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		 if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
     		cal.add(Calendar.WEEK_OF_YEAR, -1);//如果是礼拜天，则设置成西方的上一周，也就是中国的本周,因为老外那边把周日当成第一天
		 }
		
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // 获取本周一的日期
		date[0] = cal.getTime();
		

		// 这种输出的是上个星期周日的日期，因为老外那边把周日当成第一天
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

		// 增加一个星期，才是我们中国人理解的本周日的日期
		cal.add(Calendar.WEEK_OF_YEAR, 1);

		date[1] = cal.getTime();

		return date;
	}

	/**
	 * 获取指定时间月份开始时间和结束时间 Jul 10, 2013
	 * 
	 * @description
	 * @param currDate
	 * @return
	 */
	public static Date[] getMonthDate(Date currDate) {

		Calendar cal = Calendar.getInstance();
		Date[] date = new Date[2];
		cal.setTime(currDate);

		cal.set(Calendar.DATE, cal.getActualMinimum(Calendar.DATE));
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		date[0] = cal.getTime();

		cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
		cal.set(Calendar.HOUR, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		date[1] = cal.getTime();

		return date;
	}

	/**
	 * 获取指定时间上月同一天的日期 Jul 10, 2013
	 * 
	 * @description
	 * @param currDate
	 * @return
	 */
	public static Date getLastMonthDate(Date currDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(currDate);// 这一天日期
		cal.add(Calendar.MONTH, -1);
		return cal.getTime();
	}

	/**
	 * 得到去年这一个月的开始时间和结束时间 Jul 10, 2013
	 * 
	 * @description
	 * @param currDate
	 * @return
	 */
	public static Date[] getLastYearMonth(Date[] currDate) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(currDate[0]);// 去年的这一天的起始日期
		Date[] date = new Date[2];

		cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
		cal.set(Calendar.DATE, cal.getActualMinimum(Calendar.DATE));
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		date[0] = cal.getTime();

		cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
		cal.set(Calendar.HOUR, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		date[1] = cal.getTime();

		return date;
	}

	public static void main(String[] args) {

//		Calendar cal = Calendar.getInstance();
//		cal.setTime(ParamUtil.parseDate("2012-02-09", "yyyy-MM-dd"));
//		cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
//		cal.set(Calendar.DATE, cal.getActualMinimum(Calendar.DATE));
//		cal.set(Calendar.HOUR, 0);
//		cal.set(Calendar.MINUTE, 0);
//		cal.set(Calendar.SECOND, 0);
//		System.out.println(ParamUtil.formatDate(cal.getTime(),
//				"yyyy-MM-dd HH:mm:ss"));
//
//		cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
//		cal.set(Calendar.HOUR, 23);
//		cal.set(Calendar.MINUTE, 59);
//		cal.set(Calendar.SECOND, 59);
//
//		System.out.println(ParamUtil.formatDate(cal.getTime(),
//				"yyyy-MM-dd HH:mm:ss"));
//
//		Date now = new Date();
//		Date l = getLastYearThisDate(now);
//
//		System.out.println(ParamUtil.formatDate(l, "yyyy-MM-dd"));
//		
//		Date[] arr = getOverDaysInterval(ParamUtil.parseDate("2013-07-24 21:00:00", "yyyy-MM-dd HH:mm:ss"),"18:30","06:00");
//		
//		for(Date d : arr){
//			System.out.println(ParamUtil.formatDate(d, "yyyy-MM-dd HH:mm"));
//		}
		
		Date[] date  = getWeekDateByWeekNum(2013, 32);
		
//		Date[] date = CaculateUtil.getOverDaysInterval(CaculateUtil.getPreDay(new Date()), "6:00", "5:00");//
		//System.out.println(CalendarHelper.Date2String(date[0],"yyyy-MM-dd HH:mm:ss"));
		//System.out.println(CalendarHelper.Date2String(date[1],"yyyy-MM-dd HH:mm:ss"));
	}
	
	
	/**
	 * 得到昨天的日期
	 * @author lipengfei2013-8-20
	 * @param currDate
	 * @return
	 */
	public static Date getPreDay(Date currDate){
		Date date = null;	
		if(currDate!=null){
			Calendar cal = Calendar.getInstance();
			cal.setTime(currDate);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			date = cal.getTime();
		}
		return date;
	}
	
	/**
	 * 得到明天的日期
	 * @author lipengfei2013-8-20
	 * @param currDate
	 * @return
	 */
	public static Date getTomorrowDay(Date currDate){
		Date date = null;	
		if(currDate!=null){
			Calendar cal = Calendar.getInstance();
			cal.setTime(currDate);
			cal.add(Calendar.DAY_OF_MONTH, 1);
			date = cal.getTime();
		}
		return date;
	}

	/**
	 * 得到上一周的所在的时间段区间
	 * 一个礼拜的第一天为礼拜一
	 * @author lipengfei 2013-7-6
	 * @param currDate
	 * @return
	 */
	public static Date[] getLastWeekDate(Date currDate) {
		Calendar cal = Calendar.getInstance();

		Date[] date = new Date[2];

		cal.setTime(currDate);

		cal.add(Calendar.WEEK_OF_YEAR, -1);
		
//		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // 获取本周一的日期

//		date[0] = cal.getTime();

		date = getWeekDate(cal.getTime());
		return date;
	}


	/**
	 * 得到上一周的所在的时间
	 * 
	 * @author lipengfei 2013-7-6
	 * @param currDate
	 * @return
	 */
	public static Date getLastWeekThisDate(Date currDate) {
		Calendar cal = Calendar.getInstance();

		cal.setTime(currDate);

		cal.add(Calendar.WEEK_OF_YEAR, -1);

		return cal.getTime();
	}


	/**
	 * 得到去年的这个时间段的日期(比如：本周是2013年7月份的第二个月，那么去年同期则是指去年的7月份的第二周所在的日期的区间)
	 * 
	 * @author lipengfei 2013-7-6
	 * @param currDate
	 * @return
	 */
	public static Date[] getLastYearDate(Date[] currDate) {
		Calendar cal = Calendar.getInstance();

		Date[] date = new Date[2];

		cal.setTime(currDate[0]);// 去年的这一天的起始日期

		int weekNum = cal.get(Calendar.WEEK_OF_MONTH);

		int year = cal.get(Calendar.YEAR) - 1; // 去年的年份

		cal.set(Calendar.YEAR, year);

		cal.set(Calendar.WEEK_OF_MONTH, weekNum);// 去年同期的周,如去年第二周

		date = getWeekDate(cal.getTime());

		// date[0]=cal.getTime();
		// //去年的这一天的结束日期
		// cal.setTime(currDate[1]);
		// int year2 = cal.get(Calendar.YEAR) - 1;
		// cal.set(Calendar.YEAR,year2);
		// date[1]=cal.getTime();

		return date;
	}

	/**
	 * 得到去年同几周周几的日期
	 * 
	 * @author lipengfei 2013-7-6
	 * @param currDate
	 * @return
	 */
	public static Date getLastYearThisDate(Date currDate) {
		Calendar cal = Calendar.getInstance();

		cal.setTime(currDate);// 这一天日期

		int weekNum = cal.get(Calendar.WEEK_OF_MONTH);

		int year = cal.get(Calendar.YEAR) - 1; // 去年的年份

		cal.set(Calendar.YEAR, year);

		cal.set(Calendar.WEEK_OF_MONTH, weekNum);// 去年同期的周,如去年第二周

		return cal.getTime();
	}

	/**
	 * 得到去年的这一天的日期
	 * 
	 * @author lipengfei 2013-7-6
	 * @param currDate
	 * @return
	 */
	public static Date getLastYearDate(Date currDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(currDate);// 这一天日期
		cal.add(Calendar.YEAR, -1); // 去年的年份
		return cal.getTime();
	}

	/**
	 * 判断该日期段是否跨月
	 * 
	 * @author lipengfei2013-7-9
	 * @return
	 */
	public static boolean ifOverMonth(Date[] date) {
		boolean condition = true;
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date[0]);
		cal2.setTime(date[1]);

		int monthNum1 = cal1.get(Calendar.MONTH);
		int monthNum2 = cal2.get(Calendar.MONTH);

		int year1 = cal1.get(Calendar.YEAR);
		int year2 = cal2.get(Calendar.YEAR);

		if (monthNum2 == monthNum1) { // 可能大于0 也可能小于0，如2013-05-06~2013-06-09
			// 大于0，而跨年时，2013-05-06~2014-03-09
			if (year1 == year2) {
				condition = false;
			}// 代表同年同月
		}

		return condition;
	}

	/**
	 * 得到跨月的日期的区间
	 * 
	 * @author lipengfei2013-7-9
	 * @return
	 */
	public static List<Date[]> getoverMonthInterval(Date[] date) {
		List<Date[]> list = new ArrayList<Date[]>();

		if (ifOverMonth(date)) {// 如果该日期段跨月
			Date[] dateSt = new Date[2];
			Date[] dateEnd = new Date[2];

			dateSt[0] = date[0];
			dateSt[1] = getLastDay(date[0]);
			list.add(dateSt);

			dateEnd[1] = date[1];
			dateEnd[0] = getFirstDay(date[1]);
			list.add(dateEnd);
		} else {
			list.add(date); // 不跨月的话返回原时间段，跨月的话返回两个日期段
		}
		return list;
	}

	/**
	 * 临时使用
	 * 
	 * @author lipengfei2013-7-5
	 * @param str
	 * @return
	 */
	public static String getString(Object str) {
		if (str == null) {
			return null;
		} else {
			return "'" + str.toString() + "'";
		}
	};

	/**
	 * 处理分母为0的情况，若分母为0，则直接返回0的值
	 * 
	 * @author lipengfei2013-7-11
	 * @return
	 * @param fractions
	 *            分子
	 * @param numerator
	 *            分母
	 * @throws ParseException
	 */
	public static Float getValues(Float fractions, Float numerator) {

		Float value = 0f;

		if (numerator == 0) {
			return 0f;
		} else {
			DecimalFormat df = new DecimalFormat("#.0000");
			value = fractions / numerator;
			value = Float.valueOf(df.format(value));
		}
		return value;
	}

	/**
	 * 得到本周每一天的日期数组
	 * 
	 * @author lipengfei 2013-7-6
	 * @param currDate
	 * @return
	 */
	public static Date[] getEveryDateThisWeekToArray(Date currDate) {

		Calendar cal = Calendar.getInstance();

		Date[] weekInfinit = getWeekDate(currDate); // 该日期所在的周的日期区间

		Date[] date = new Date[7];

		date[0] = weekInfinit[0];

		cal.setTime(date[0]);

		for (int i = 1; i < 7; i++) {
			cal.add(Calendar.DAY_OF_YEAR, 1);// 加1天
			date[i] = cal.getTime();// 从礼拜一到礼拜天
		}

		return date;
	}

	/**
	 * 得到本周每一天的日期map，将当前日期传进去后返回的是每一个礼拜的日期,如礼拜二的日期为：yyyy-MM-dd
	 * 
	 * @author lipengfei 2013-7-6
	 * @param currDate
	 *            当前日期
	 * @return
	 */
	public static Map<String, Date> getEveryDateThisWeekToMap(Date currDate) {

		Calendar cal = Calendar.getInstance();

		Map<String, Date> map = new HashMap<String, Date>();

		Date[] weekInfinit = getWeekDate(currDate); // 该日期所在的周的日期区间

		Date[] date = new Date[7];

		date[0] = weekInfinit[0];

		cal.setTime(date[0]);

		map.put("monday", date[0]);

		for (int i = 3; i < 8; i++) {// 从礼拜二开始到礼拜六
			cal.add(Calendar.DAY_OF_YEAR, 1);// 加1天
			switch (i) {
			case Calendar.TUESDAY:
				map.put("tuesday", cal.getTime());
				break;
			case Calendar.WEDNESDAY:
				map.put("wednesday", cal.getTime());
				break;
			case Calendar.THURSDAY:
				map.put("thursday", cal.getTime());
				break;
			case Calendar.FRIDAY:
				map.put("friday", cal.getTime());
				break;
			case Calendar.SATURDAY:
				map.put("saturday", cal.getTime());
				break;
			default:
				break;
			}
		}
		map.put("sunday", weekInfinit[1]);

		return map;
	}

	/**
	 * 得到小时的区间段，如:传入的是7，则打印7:00~7:59
	 * 
	 * @author lipengfei2013-7-11
	 */
	public static String getHoursInfinit(Object hours) {
		return hours + ":00" + "~" + hours + ":59";
	}

	/**
	 * 小数位数
	 * 
	 * @author lipengfei2013-7-11
	 * @return
	 * @param fractions
	 *            分子
	 * @param numerator
	 *            分母
	 * @throws ParseException
	 */
	public static Float getValuesBite(Float values) {
		Float value = 0f;
		DecimalFormat df = new DecimalFormat("#.00");
		value = Float.valueOf(df.format(values));
		return value;
	}
	
	
	
	/**
	 * 判断是否跨天，
	 * 传入两个时间，判断这两个时间是否跨天
	 * @author lipengfei2013-7-15
	 */
	public static boolean IfOverDays(String hoursStart,String hoursEnd){
		 boolean flag = false;	
		 	Calendar cal1 = Calendar.getInstance();
			Calendar cal2 = Calendar.getInstance();
			
			cal1.setTime(CalendarHelper.String2Date(hoursStart, "HH:mm"));
			cal2.setTime(CalendarHelper.String2Date(hoursEnd, "HH:mm"));
			if(cal1.get(Calendar.HOUR_OF_DAY)>=cal2.get(Calendar.HOUR_OF_DAY)){
					//代表跨天，
						flag=true;
			}
			return flag;
	}

	/**
	 * 判断是否跨年，
	 * 判断该日期段是否跨年
	 * @author lipengfei2013-7-15
	 */
	public static boolean IfOverYears(String hoursStart,String hoursEnd){
		boolean flag = false;	
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		
		cal1.setTime(CalendarHelper.String2Date(hoursStart, "HH:mm"));
		cal2.setTime(CalendarHelper.String2Date(hoursEnd, "HH:mm"));
		if(cal1.get(Calendar.HOUR_OF_DAY)>=cal2.get(Calendar.HOUR_OF_DAY)){
			//代表跨天，
			flag=true;
		}
		return flag;
	}
	
	
	/**
	 * 先判断是否跨天，如果跨天，则返回今明两天的日期以及小时、分钟数， 如2013-07-07 15:00~12:00，是跨天，则返回的是2013-07-07 15:00 ~ 2013-07-08 12:00
	 * @author lipengfei 2013-7-15
	 * @param date
	 * @param hoursStart
	 * @param hoursEnd
	 * @return
	 */
	public static Date[] getOverDaysInterval(Date date,String hoursStart,String hoursEnd){
		Date[] dateArray = new Date[2];
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		
	 	Calendar calTemp1 = Calendar.getInstance();
		Calendar calTemp2 = Calendar.getInstance();
		
		calTemp1.setTime(CalendarHelper.String2Date(hoursStart, "HH:mm"));
		calTemp2.setTime(CalendarHelper.String2Date(hoursEnd, "HH:mm"));
		
		if (IfOverDays(hoursStart, hoursEnd)) {
			cal1.setTime(date);
			cal1.set(Calendar.HOUR_OF_DAY,calTemp1.get(Calendar.HOUR_OF_DAY));
			cal1.set(Calendar.MINUTE, calTemp1.get(Calendar.MINUTE));

			cal2.setTime(date);
			cal2.set(Calendar.HOUR_OF_DAY,calTemp2.get(Calendar.HOUR_OF_DAY));
			cal2.set(Calendar.MINUTE, calTemp2.get(Calendar.MINUTE));
			cal2.add(Calendar.DAY_OF_MONTH, 1);//跨天则加一天
		}else {

			cal1.setTime(date);
			cal1.set(Calendar.HOUR_OF_DAY,calTemp1.get(Calendar.HOUR_OF_DAY));
			cal1.set(Calendar.MINUTE, calTemp1.get(Calendar.MINUTE));

			cal2.setTime(date);
			cal2.set(Calendar.HOUR_OF_DAY,calTemp2.get(Calendar.HOUR_OF_DAY));
			cal2.set(Calendar.MINUTE, calTemp2.get(Calendar.MINUTE));
			
		}
		dateArray[0] = cal1.getTime();
		dateArray[1] = cal2.getTime();
		return dateArray;
	}
	

	/**
	 * 判断是否跨年，
	 * 判断该日期段是否跨年
	 * @author lipengfei2013-7-15
	 */
	public static boolean IfOverYears(Date start,Date end){
		boolean flag = false;	
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(start);
		cal2.setTime(end);
		if(cal1.get(Calendar.YEAR)!=cal2.get(Calendar.YEAR)){
			//代表跨年，
			flag=true;
		}
		return flag;
	}
	/**
	 * 得到跨年的时间段列表，如果不跨年，则list只返回一个
	 * @author lipengfei2013-7-16
	 * @return
	 */
	public static List<Date[]> getOverYearDate(Date start,Date end){
		List<Date[]> list = new ArrayList<Date[]>();
		
		if(IfOverYears(start, end)){
			Date[] date1 = new Date[2];
			Date[] date2 = new Date[2];
			
			date1[0] =start;
			date1[1]=getLastDayOfYear(start);
			
			date2[0]=getFirstDayOfYear(end);
			date2[1]=end;

			list.add(date1);
			list.add(date2);
		
		}else {
			Date[] date = new Date[2];
			date[0]=start;
			date[1]=end;
			list.add(date);
		}
		return list;
	}
	
	public static Date getLastDayOfYear(Date date){
		Date temp = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.YEAR, 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.MONTH, 0);//重置到下一年的1月1号，之后再减1，得到上一年的最后一天的日期
		cal.add(Calendar.DAY_OF_MONTH, -1);
		return cal.getTime();
	}
	
	/**
	 * 得到第一天日期
	 * @author lipengfei 2013-6-7
	 * @return
	 */
	public static Date getFirstDayOfYear(Date date){
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.MONTH, 0);
		return cal.getTime();
	}
	
	
	
	

	/**
	 *判断传入的时间是否在营业时间范围之内。
	 * @author lipengfei 2013-7-15
	 * @param date 当前时间
	 * @param hoursStart 营业开始时间
	 * @param hoursEnd  营业结束时间
	 * @return
	 */
	public static boolean ifInDaysInterval(String hoursStart,String hoursEnd,Date date){
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		Calendar cal3 = Calendar.getInstance();
		
		cal1.setTime(CalendarHelper.String2Date(hoursStart, "HH:mm"));
		cal2.setTime(CalendarHelper.String2Date(hoursEnd, "HH:mm"));
		cal3.setTime(date);
		
		if(IfOverDays(hoursStart, hoursEnd)){//如果跨天
			return ifIninterval(cal1, cal2, cal3,true);
		}else {
			return ifIninterval(cal1, cal2, cal3,false);
		}
	}
	

	/**
	 * 判断该日期段是否跨周
	 * @author lipengfei 2013-7-9
	 * @return
	 */
	public static boolean ifOverWeek(Date[] date){
		boolean condition = true;
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date[0]);
		cal2.setTime(date[1]);
		
		int monthNum1 = cal1.get(Calendar.MONTH);
		int monthNum2 = cal2.get(Calendar.MONTH);
		
		
		Date[] theDate = getWeekDate(date[0]);//得到date[0]所在的周的日期段。,看date[1]是否属于这个日期段之内
		
		if(calculateDayIn(theDate[0], date[1])>=0 && calculateDayIn(theDate[1],date[1])<=0){
			condition=false;//代表在这一周的日期段内，于是同周
		}
		
		return condition;
	}
	
	/**
	 * 判断数据是否在这个区间,如 营业时间是8:30~18:30 ，则19:00不在该区间
	 * @author lipengfei2013-7-17
	 * @return
	 */
	public static boolean ifIninterval(Calendar start,Calendar end , Calendar data,boolean ifoverDay){
		int startHour = start.get(Calendar.HOUR_OF_DAY);
		int endHour = end.get(Calendar.HOUR_OF_DAY);
		int dataHour = data.get(Calendar.HOUR_OF_DAY);
		
		if(ifoverDay){//如果跨天
			if(dataHour<startHour && dataHour>endHour){
				return false;
			}else if(startHour==endHour){//如果营业时间的小时数相等
				if(dataHour==startHour){
					if(data.get(Calendar.MINUTE)<start.get(Calendar.MINUTE) || data.get(Calendar.MINUTE)>end.get(Calendar.MINUTE)){
							return false;
						}else {
							return true;
						}
				}else {
						return true;
				}
			}else if(dataHour==startHour){
				if(data.get(Calendar.MINUTE)<start.get(Calendar.MINUTE)){
					return false;
				}else {
					return true;
				}
			}else if(dataHour==endHour){
				if(data.get(Calendar.MINUTE)>end.get(Calendar.MINUTE)){
					return false;
				}else {
					return true;
				}
			}else{
				return true;
			}
		}else {//如果不跨天
			if(dataHour<startHour || dataHour>endHour){
					return false;
			}else if(dataHour==startHour){
				if(data.get(Calendar.MINUTE)<start.get(Calendar.MINUTE)){
					return false;
				}else {
					return true;
				}
			}else if(dataHour==endHour){
				if(data.get(Calendar.MINUTE)>end.get(Calendar.MINUTE)){
					return false;
				}else {
					return true;
				}
			}
			else{
				return true;
			}
		}
	}
	
	
	public static String fromListTosqlString(List<Long> list){
		String str="";
		if(list!=null && list.size()>0){
			for (Long value : list) {
				str+=value+",";
			}

			if(str.length()>0){
				str = str.substring(0, str.length()-1);
			}
		}else {
			return null;
		}
		return str;
	}
	
	public static String fromSzTosqlString(String sql,String fh){
		String[] sqls = sql.split(fh);
		String str="";
		for(int i =0;i<sqls.length;i++){
			String s = sqls[i];
			if(StringUtils.isNotBlank(s)){
				str+="'"+s+"'";
				str+=",";
			}
			
		}
		
		if(str.length()>0){
			str=str.substring(0,str.length()-1);
		}
		
		return str;
	}
	
	/***
	 * 若传入数组为[1,2,3,4]
	 * 则返回字符串1,2,3,4
	 * @author lipengfei2013-7-25
	 * @param list
	 * @return
	 */
	public static String fromListTosqlStringForMap(List<Map> list,String keyStr){
		String str="";
		if(list!=null && list.size()>0){
			for (Map map : list) {
				str+=map.get(keyStr)+",";
			}
			
			if(str.length()>0){
				str = str.substring(0, str.length()-1);
			}
		}else {
			return null;
		}
		return str;
	}
	
	
	/**
	 * 按照每周来取
	 * 反之，返回日期段之间的每周的日期开始、结束时间
	 * 如：2013-07-21 00:00:00~2013-07-22
	 * 则返回 2013-07-15~2013-07-21 date数组0为第一个日期，date[1]为第二个日期，下同
			 2013-07-22~2013-07-28
	 * 此方法已东方周日期为主，即以礼拜一为第一天，礼拜天为最后一天
	 * @author lipengfei 2013-6-7
	 * @param star
	 * @param end
	 * @return
	 * 
	 */
	public static List<Date[]> dateListByWeek(Date startDate,Date endDate){
		List<Date[]> listDate = new ArrayList<Date[]>();
		
		Date[] date = new Date[2];
		date[0] = startDate;
		date[1] = endDate;
		
		if(ifOverWeek(date)){//如果两个日期段超过了一周，则开始分开周,否则不分开
				Calendar cal = Calendar.getInstance();
				cal.setTime(startDate);
				Date curr = cal.getTime();
				while(calculateDayIn(curr, endDate)>0){//日期不超过后者时。
					curr = cal.getTime();
					listDate.add(getWeekDate(curr));//得到其所在的一周的区间,加入列表
					cal.setTime(curr);
					cal.add(Calendar.DAY_OF_YEAR, 7);//得到下一个周期，的日期
				}
		}else{
			listDate.add(getWeekDate(date[0]));//因为在同一周，所以取其中一个得到本周的日期区间即可
		}
		return listDate;
	}
	

	/**
	 * 通过年份和周数来的到这一周所在的日期段
	 * @author lipengfei2013-7-26
	 * @param year
	 * @param weekNum
	 * @return
	 */
	public static Date[] getWeekDateByWeekNum(int year,int weekNum){
		Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.MONDAY);//设置礼拜一为一星期的第一天
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.WEEK_OF_YEAR, weekNum);
		return getWeekDate(cal.getTime());
	}
	
	/**
	 * 得到两个日期相差的小时数
	 * @author lipengfei2013-8-1
	 * @return
	 */
	public static Float getDifferenceHourOfDate(Date startDate , Date endDate){
		float quot = 0;

		quot = getDifferencemillisecondsOfDate(startDate, endDate);
		
		quot = quot/(60*60*1000);//从毫秒数变成秒数
		
		return quot;
		
	}

	/**
	 * 得到两个日期相差的秒数
	 * @author lipengfei2013-8-1
	 * @return
	 */
	public static long getDifferencemillisecondsOfDate(Date startDate , Date endDate){
		long quot = 0;
		Calendar cal1 = new GregorianCalendar();
		cal1.setTime(endDate);
		Calendar cal2 = new GregorianCalendar();
		cal2.setTime(startDate);
		quot = endDate.getTime() - startDate.getTime();
		return quot;
	}
	
}
