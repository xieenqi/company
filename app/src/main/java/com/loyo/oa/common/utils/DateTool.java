package com.loyo.oa.common.utils;

import android.util.Log;

import com.loyo.oa.v2.activityui.attendance.model.DataSelect;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.Global;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

/**
 * 时间格式化,截取等工具类都在这里面..原来的工具类在package com.loyo.oa.v2.tool.DateTool;
 * Created by jie on 16/12/12.
 */
public class DateTool {
    private static String TAG = "DateTool";
       private static String weeks = new String("日一二三四五六");//用来处理星期几


    public static ArrayList<DataSelect> getYearMonth(int startYear, int endYear) {
        ArrayList<DataSelect> arrayList = new ArrayList<>();
        DataSelect dataSelect = null;
        Calendar calendar = Calendar.getInstance();
        Calendar calendarEnd = Calendar.getInstance();
        calendar.set(Calendar.YEAR, startYear);
        calendar.set(Calendar.MONTH, 0);
        calendarEnd.set(Calendar.YEAR, endYear);
        calendarEnd.set(Calendar.MONTH, Integer.parseInt(getMonth()) - 1);//!!!注意,设置月份的时候,是0序,要-1才是正确的
        String currentYear = getYear();//当前的年份
        while (calendar.getTime().getTime() <= calendarEnd.getTime().getTime()) {
            dataSelect = new DataSelect();
            dataSelect.top = getMonth(calendar.getTime());
            if (getYear(calendar.getTime()).contains(currentYear)) {
                dataSelect.bottom = "今年";
            } else {
                dataSelect.bottom = getYear(calendar.getTime());
            }
            dataSelect.yearMonDay = getYear(calendar.getTime()) + "年" + getMonth(calendar.getTime()) + "月";
            dataSelect.mapOftime = String.valueOf(calendar.getTime().getTime()).substring(0, 10);
            arrayList.add(dataSelect);
            calendar.add(Calendar.MONTH, 1);
        }

        return arrayList;
    }

    /**
     * 【团队考勤 我的拜访 团队拜访】获取年月日
     * 月份0~11，计算时要减1位
     */
    public static ArrayList<DataSelect> getYearMonthDay(int startYear, int endYear) {

        ArrayList<DataSelect> arrayList = new ArrayList();
        DataSelect dataSelect = null;

        Calendar calendar = Calendar.getInstance();
        Calendar calendarEnd = Calendar.getInstance();
        calendar.set(Calendar.YEAR, startYear);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 0);

        calendarEnd.set(Calendar.YEAR, endYear);
        calendarEnd.set(Calendar.MONTH, Integer.parseInt(getMonth()) - 1);
        calendarEnd.set(Calendar.DAY_OF_MONTH, Integer.parseInt(getDay()));

        while (calendar.getTime().getTime() <= calendarEnd.getTime().getTime()) {
            dataSelect = new DataSelect();
            dataSelect.top = getDay(calendar.getTime());
            dataSelect.bottom = getMonth(calendar.getTime()) + "月";
            dataSelect.yearMonDay = getYear(calendar.getTime()) + "年" + getMonth(calendar.getTime())
                    + "月" + getDay(calendar.getTime())
                    + "日 " + getWeekStr(calendar.getTime());
            dataSelect.mapOftime = String.valueOf(calendar.getTime().getTime()).substring(0, 10);
            arrayList.add(dataSelect);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return arrayList;
    }



    /**
     * 通用的把字符串的时间,转换成时间戳
     *
     * @param timeStr 时间字符串
     * @param sdfOut  时间格式化工具
     * @return 时间戳
     */
    public static Long getTimeStamp(String timeStr, DateFormat sdfOut) {
        Long timestamp = 0L;
        try {
            timestamp = sdfOut.parse(timeStr).getTime();
        } catch (Exception e) {
            Global.ProcException(e);
        }
        return timestamp;
    }

    /**
     * 获取月份和天,eg:12.01
     * @param time 要格式化的时间戳
     * @return 返回月份和天 eg:12.01
     */
    public static String getMonthDay(long time){
        return DateFormatSet.daySdf.format(new Date(time));
    }

    /**
     * APP日期格式
     * 1、今天、昨天不显示年和月： 今天 15:30   昨天 15:30
     * 2、本年不显示年：03-04 15:30
     * 3、非本年显示完整的年月日时分：2016-03-04 15:30
     * @param seconds 时间
     * @param includeTime 是否显示详细的时间 eg  false:今天   true:今天 14:90
     * @return
     */
    public static String getFriendlyTime(long seconds,boolean includeTime) {
        seconds*=1000;//这里要乘1000,把秒转换成毫秒
        Date time = new Date(seconds);
//        Calendar cal = Calendar.getInstance();
        Date curTime=new Date();
        String day=null;
        //判断是不是同一天
        if (DateFormatSet.dateSdf.format((seconds)).equals(DateFormatSet.dateSdf.format((curTime.getTime())))){
            day="今天";
        }else {
            //不是同一天
            int dayMillis=24*3600*1000;
            long timeDiff=(curTime.getTime()- time.getTime());//差距的毫秒
            if(timeDiff<=dayMillis&&timeDiff>0){
                //昨天
                day="昨天";
            }else if(timeDiff>-dayMillis&&timeDiff<0){
                //明天
                day="明天";
            }else{
                //本年不显示年  eg：03-04 15:30
                if(getYear(time).equals(getYear(curTime))){//同一年
                    return DateFormatSet.dateNoYear.format(time);
                }else{
                    return DateFormatSet.minuteSdf.format(time);
                }
            }
        }
        return includeTime?day+" "+DateFormatSet.dateHourMinute.format(time):day;

    }


    /**
     * 判断一个时间,是否已经过期了
     * @param date 时间
     * @param dateNumber 过期的天数
     * @return 没有过期,返回true,否则返回false
     */
    public static boolean isDateInTime(long date,int dateNumber){
        Date time=new Date(date);
        Date curTime=new Date();
        //时间相差的天数
        long dayDiff=(curTime.getTime()- time.getTime())/(24*3600*1000);
        return dayDiff<dateNumber?true:false;
    }


    /**
     * 获取当天的开始的时间戳 也就是当天的0点
     * @return
     */
    public static long getCurrentDayBeginMillis() {
        return getSomeDayBeginMillis(0);
    }

    /**
     *获取当天结束的时间戳 也就是当天的23:59:59.999
     *
     * @return
     */
    public static long getCurrentDayEndMillis() {
        return getSomeDayEndMillis(0);
    }

    /**
     * 获取某一天开始的时间,从今天开始算,前index天的开始时间 eg:如果是0,就表示今天,1就是昨天,-1就是明天
     * @param index
     * @return
     */
    public static long getSomeDayBeginMillis(int index) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(calendar.DAY_OF_MONTH, -index);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime().getTime();
    }

    /**
     * 获取某一天结束的时间,从今天开始算,前index天的开始时间 eg:如果是0,就表示今天,1就是昨天,-1就是明天
     */
    public static long getSomeDayEndMillis(int index) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(calendar.DAY_OF_MONTH, -index);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND,999);
        return calendar.getTime().getTime();
    }




    /**
     * 获取当月的开始时间戳
     * @return
     */
    public static long getCurrentMonthBeginMillis(){
        return getSomeMonthBeginMillis(0);
    }

    /**
     * 获取当月的结束时间戳
     * @return
     */
    public static long getCurrentMonthEndMillis(){
        return getSomeMonthEndMillis(0);
    }

    /**
     * 获取某一个月开始的时间戳,从本月开始算,前index月的开始时间 eg:如果是0,就表示本月,1就是上月,-1就是下月
     */
    public static long getSomeMonthBeginMillis(int index) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(calendar.MONTH, -index);//防止下标为0
        calendar.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime().getTime();
    }


    /**
     * 获取某一个月开始的结束戳,从本月开始算,前index月的开始时间 eg:如果是0,就表示本月,1就是上月,-1就是下月
     */
    public static long getSomeMonthEndMillis(int index) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(calendar.MONTH, -index);//防止下标为0
        calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));//当月最后一天
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime().getTime();
    }




    /**
     * 获取当周的开始时间戳
     * @return
     */
    public static long getCurrentWeekBeginMillis(){
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime().getTime();
    }

    /**
     * 获取当周的结束时间戳
     * @return
     */
    public static long getCurrentWeekEndMillis(){
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime().getTime();
    }

    /**
     * 获取过去某星期 开始时间
     */
    public static long getSomeWeekBeginMillis(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime().getTime();
    }

    /**
     * 获取过去某星期 结束时间
     */
    public static long getSomeWeekEndMillis(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime().getTime();
    }





    /**
     * 获取完整的到秒的时间戳
     *
     * @param timeStr 时间的字符串
     * @return 时间戳
     */
    public static Long getSecondStamp(String timeStr) {
        return getTimeStamp(timeStr, DateFormatSet.secondSdf);
    }

    /**
     * 获取到分钟的时间错
     * @param timeStr
     * @return
     */
    public static Long getMinuteStamp(String timeStr) {
        return getTimeStamp(timeStr, DateFormatSet.minuteSdf);
    }

    /**
     * 获取到day的时间戳
     * @param timeStr
     * @return
     */
    public static Long getDateStamp(String timeStr) {
        return getTimeStamp(timeStr, DateFormatSet.dateSdf);
    }

    /**
     * 获取特殊格式 MM.dd的时间戳
     * @param timeStr
     * @return
     */
    public static Long getDayStamp(String timeStr) {
        return getTimeStamp(timeStr, DateFormatSet.daySdf);
    }

    /**
     * 时间格式转换
     * @param dateString 时间的字符串
     * @param sdf2 目标格式
     * @return 转换以后的时间
     */
    public static String convertDate(String dateString,
                                 DateFormat sdf2) {
        String strDate = "";
        try {
            Date date = DateFormatSet.serverApiSdf.parse(dateString);
            strDate = sdf2.format(date);
        } catch (Exception e) {
            Global.ProcException(e);
        }
        return strDate;
    }



    /**
     * 获取制定时间的年份
     *
     * @param date 指定的date
     * @return 年份, 比如:2016
     */
    public static String getYear(Date date) {
        return DateFormatSet.dateYearFormat.format(date);
    }

    /**
     * 获取当前年份 比如:2016
     */
    public static String getYear() {
        return getYear(new Date());
    }

    /**
     * 获取指定时间的月份,比如:12
     *
     * @param date 指定的date
     * @return 月份 比如:12
     */
    public static String getMonth(Date date) {
        return DateFormatSet.dateMonthFormat.format(date);
    }

    /**
     * 获取当前的月份
     *
     * @return
     */
    public static String getMonth() {
        return getMonth(new Date());
    }

    /**
     * 获取指定时间是一个月中的第几天 比如:2016-11-12 10-16-18 返回12
     *
     * @param date 指定的时间
     * @return 返回day of month
     */
    public static String getDay(Date date) {
        return DateFormatSet.dateDayFormat.format(date);
    }

    /**
     * 获取当前时间是一个月中的第几天,当前时间是2016-11-12 10-16-18 返回12
     *
     * @return 返回day of month
     */
    public static String getDay() {
        return getDay(new Date());
    }

    /**
     * 给定一个时间,返回是星期几 比如:星期一 星期天
     *
     * @param date 给定一个时间
     * @return 返回星期几的字符串 eg:星期天
     */
    public static String getWeekStr(Date date) {
//        这里可以使用格式化的方式,但是返回不完全满足要求,返回是:周日,周一,周二。。。
//        SimpleDateFormat dateWeekFormat = new SimpleDateFormat("E");//格式化 星期几
//        String week=dateWeekFormat.format(date);
        int week = getWeek(date);
        String weekStr = "星期" + weeks.substring(week - 1, week);//用截取字符串的方式,获取到最后一个文字
        return weekStr;
    }


    /**
     * 获取指定的一个日期的星期几,返回的是数字,eg:1 2 3 4 5 6 7
     *
     * @param date 指定的一个日期
     * @return 返回星期的数字, eg:1 2 3 4 5 6 7
     */
    public static int getWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int week = c.get(Calendar.DAY_OF_WEEK);
        return week;
    }

    /**
     * 获取当前的星期,返回的是数字,eg:1 2 3 4 5 6 7
     * @return 返回星期的数字, eg:1 2 3 4 5 6 7
     */
    public static int getWeek(){
        return getWeek(new Date());
    }


    /**
     * 把自定义的时间格式转换成秒为单位的时间戳
     * @param time 要格式化的时间
     * @param sdr 源时间的格式
     * @return 返回10位的时间戳
     */
    public static String date2StampByFormat(String time,SimpleDateFormat sdr) {
        Date date;
        String times = null;
        try {
            date = sdr.parse(time);
            long l = date.getTime();
            String stf = String.valueOf(l);
            times = stf.substring(0, 10);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return times;
    }

    /**
     * 获取当前的日期时间的时间戳,秒级(10位),比如获取:2016-12-12 2:2:3的时间戳
     * @param hasTime 是否包括时分秒的时间戳
     * @return
     */
    public static int getStamp(boolean hasTime){
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND,0);
        if(!hasTime){
            calendar.set(Calendar.HOUR_OF_DAY,0);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
        }
        return (int)(calendar.getTimeInMillis()/1000);
    }

    /**
     * 获取当前的日期
     * @return eg:2012-12-12
     */
    public static int getDateStamp(){
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND,0);
        return (int) (calendar.getTimeInMillis()/1000);
    }

    /**
     * 根据年月日 时分秒来生成对应时间的时间戳
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     * @param second
     * @return 对应时间的时间戳,秒级(10位)
     */
    public static int getStamp(int year,int month,int day,int hour,int minute,int second){
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.DAY_OF_MONTH,day);
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,minute);
        calendar.set(Calendar.SECOND,second);
        calendar.set(Calendar.MILLISECOND,0);
        return (int) (calendar.getTimeInMillis()/1000);
    }
    /**
     * 根据年月日 来生成对应时间的时间戳
     * @param year
     * @param month
     * @param day
     * @return 对应时间的时间戳,秒级(10位)
     */
    public static int getStamp(int year,int month,int day){
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.DAY_OF_MONTH,day);
        return getStamp(year,month,day,0,0,0);
    }

    /**
     * 根据把时间戳,转换成方便阅读的时间  eg:2016-12-12 2:2:3
     * @param time 对应时间的时间戳,秒级(10位)
     * @return
     */
    public static String getDateTime(long time){
        return getFriendlyTime(time,true);//友好的显示时间
//        return DateFormatSet.minuteSdf.format(new Date(time*1000));//完整显示
    }

    /**
     * 根据把时间戳,转换成方便阅读的时间  eg:2016-12-12
     * @param time 对应时间的时间戳,秒级(10位)
     * @return
     */
    public static String getDate(long time){
        return getFriendlyTime(time,false);//友好的显示时间
//        return DateFormatSet.dateSdf.format(new Date(time*1000));//完整显示
    }

    /**
     * 把毫秒转换成：1:20:30这里形式,一般用于声音视频的播放
     * @param timeMs 毫秒
     * @return
     */
    public static String int2time(int timeMs) {
        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter();
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        return mFormatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString();
    }


}


