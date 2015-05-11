package com.boredream.boreweibo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.text.format.DateFormat;

public class DateUtils {
	
	public static final long ONE_MINUTE_MILLIONS = 60 * 1000;
	public static final long ONE_HOUR_MILLIONS = 60 * ONE_MINUTE_MILLIONS;
	public static final long ONE_DAY_MILLIONS = 24 * ONE_HOUR_MILLIONS;

	/**
	 * MM-dd HH:mm
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getDateTime(String dateStr) {
		@SuppressWarnings("deprecation")
		Date date = new Date(dateStr);
		return DateFormat.format("MM-dd HH:mm", date).toString();
	}
	
	public static String getShortTime(String dateStr) {
		String str = "";
		
		@SuppressWarnings("deprecation")
		Date date = new Date(dateStr);
		Date curDate = new Date();
		
		try {
			long durTime = curDate.getTime() - date.getTime();
			int dayStatus = calculateDayStatus(date, curDate);
			
			if(durTime <= 10 * ONE_MINUTE_MILLIONS) {
				str = "刚刚";
			} else if(durTime < ONE_HOUR_MILLIONS){
				str = durTime / ONE_MINUTE_MILLIONS + "分钟前";
			} else if(dayStatus == 0){
				str = durTime / ONE_HOUR_MILLIONS + "小时前";
			} else if(dayStatus == -1) {
				str = "昨天" + (String) DateFormat.format("HH:mm", date);
			} else if(dayStatus < -1){
				str = (String) DateFormat.format("MM-dd", date);
			} else {
				str = (String) DateFormat.format("yyyy-MM-dd", date);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return str;
	}
	
	public static boolean isSameDay(Date date1, Date date2) {
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(date1);
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(date2);
		
		return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) && 
				calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) &&
				calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH);
	}
	
	/**
	 * @param targetTime
	 *            需要计算的时间
	 * @param compareTime
	 *            被对比的时间 (如果为空 默认当前时间 ,表示和当前时间相比)
	 * @return 	0 :同一天  
	 * 			< 0:targetTime是compareTime的前多少天,比如-1就是前一天
	 * 			> 0:targetTime是compareTime的后多少天,比如-1就是前一天
	 * @throws ParseException
	 *             转换异常
	 */
	public static int calculateDayStatus(Date targetTime, Date compareTime) throws ParseException {
		if (compareTime == null) {
			compareTime = new Date();
		}
		
		Calendar targetCalendar = Calendar.getInstance();
		targetCalendar.setTime(targetTime);
		targetCalendar.set(Calendar.HOUR_OF_DAY, 0);
		targetCalendar.set(Calendar.MINUTE, 0);
		targetCalendar.set(Calendar.SECOND, 0);
		int targetYear = targetCalendar.get(Calendar.YEAR);
		int targetDayOfYear = targetCalendar.get(Calendar.DAY_OF_YEAR);
		
		Calendar compareCalendar = Calendar.getInstance();
		compareCalendar.setTime(compareTime);
		compareCalendar.set(Calendar.HOUR_OF_DAY, 0);
		compareCalendar.set(Calendar.MINUTE, 0);
		compareCalendar.set(Calendar.SECOND, 0);
		int compareYear = compareCalendar.get(Calendar.YEAR);
		int compareDayOfYear = compareCalendar.get(Calendar.DAY_OF_YEAR);
		
		if(targetYear == compareYear) {
			return targetDayOfYear - compareDayOfYear;
		} else {
			return (int) ((targetCalendar.getTimeInMillis() - compareCalendar.getTimeInMillis()) / ONE_DAY_MILLIONS);
		}
	}
	
}
