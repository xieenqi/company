package com.loyo.oa.v2.tool;

import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.widget.TextView;

import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.Global;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTool {
    /**
     * 1s==1000ms
     */
    private final static int TIME_MILLISECONDS = 1000;
    /**
     * 时间中的分、秒最大值均为60
     */
    private final static int TIME_SECONDS = 60;
    /**
     * 时间中的小时最大值
     */
    private final static int TIME_HOURSES = 24;

    public static final long MINUTE_MILLIS = TIME_SECONDS * TIME_MILLISECONDS;

    public static final long HOUR_MILLIS = MINUTE_MILLIS * TIME_SECONDS;
    /**
     * milliseconds of a day
     */
    public static final long DAY_MILLIS = HOUR_MILLIS * TIME_HOURSES;

    /**
     * yyyyMMdd
     */
    public static final String DATE_DEFAULT_FORMATE = "yyyyMMdd";

    /**
     * yyyyMMdd HH:mm
     */
    public static final String DATE_DEFAULT_FORMATE2 = "yyyyMMdd HH:mm";

    /**
     * yyyy-MM-dd
     */
    public static final String DATE_DEFAULT_FORMATE1 = "yyyy-MM-dd";

    public static final String DATE_FORMATE_DAY = "MM.dd";

    /**
     * 自定义日期格式1，为周报月报服务
     */
    public static final String DATE_FORMATE_CUSTOM_1 = "yyyy年MM月dd日";

    /**
     * yyyy年MM月dd日HH时mm分
     */
    public static final String DATE_FORMATE_CUSTOM_2 = "yyyy年MM月dd日HH时mm分";

    /**
     * yyyy-MM-dd HH:mm:ss 2010-05-11 17:22:26
     */
    public static final String DATE_FORMATE_ALL = "yyyy-MM-dd HH:mm:ss";
    /**
     * 精确到分钟
     */
    public static final String DATE_FORMATE_AT_MINUTES = "yyyy-MM-dd HH:mm";

    public static final String DATE_FORMATE_MINUTES = "yyyy-MM-dd_HH-mm-ss";

    /**
     * dd-MM-yyyy, hh:mm
     */
    public static final String DATE_FORMATE_TRANSACTION = "MM.dd HH:mm";
    /**
     * MM/dd HH:mm
     */
    public static final String DATE_FORMATE_DAY_HOUR_MINUTE = "MM/dd HH:mm";
    /**
     * HH:mm
     */
    public static final String DATE_FORMATE_HOUR_MINUTE = "HH:mm";
    public static final String DATE_FORMATE_HOUR_MINUTE_SECOND = "HH:mm:ss";

    public static final String DATE_FORMATE_CHINE = "yyyy年MM月dd日 hh时mm分";

    public static final String DATE_FORMATE_SPLITE_BY_POINT = "yyyy.MM.dd hh:mm";

    public static Calendar calendar;

    public static String toDateStr(long time, String dateFormat) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.getDefault());
        return format.format(new Date(time));
    }

    public static Long getDateToTimestamp(String strTime, DateFormat sdfOut) {
        Long timestamp = 0l;
        try {
            timestamp = sdfOut.parse(strTime).getTime();
        } catch (Exception e) {
            Global.ProcException(e);
        }
        return timestamp;
    }

    public static String getDate(String dateString, String dateFormat1,
                                 String dateFormat2) {
        SimpleDateFormat sdf1 = new SimpleDateFormat(dateFormat1, Locale.getDefault());
        SimpleDateFormat sdf2 = new SimpleDateFormat(dateFormat2, Locale.getDefault());
        String strDate = "";
        try {
            Date date = sdf1.parse(dateString);
            strDate = sdf2.format(date);
        } catch (ParseException e) {
            Global.ProcException(e);
        }
        return strDate;
    }

    //最好改用formateServerDate
    public static String getDate(String dateString, DateFormat sdf1,
                                 DateFormat sdf2) {
        String strDate = "";
        try {
            Date date = sdf1.parse(dateString);

            if (sdf2 == MainApp.getMainApp().df_api) {
                Calendar cal = Calendar.getInstance(Locale.CHINA);
                sdf2.setTimeZone(cal.getTimeZone());
            }

            strDate = sdf2.format(date);
        } catch (Exception e) {
            Global.ProcException(e);
        }

        return strDate;
    }

    //格式化服务器返回的时间
    public static String formateServerDate(String dateString, DateFormat sdf1) {
//        try {
//            if (dateString.endsWith("+08:00")) {
//                return sdf1.format(MainApp.getMainApp().df_api_get2.parse(dateString));
//            } else if (dateString.toUpperCase().endsWith("Z")) {
//                return sdf1.format(MainApp.getMainApp().df_api.parse(dateString));
//            } else {
//                return sdf1.format(MainApp.getMainApp().df_api_get.parse(dateString));
//            }
//        } catch (Exception ex) {
//            DateTime dt = DateTime.parse(dateString);
//            Global.ProcException(ex);
//        }
        return sdf1.format(DateTime.parse(dateString).toDate());
    }

    public static Date getDate(String dateString, String dateFormat) {
        SimpleDateFormat sdf1 = new SimpleDateFormat(dateFormat, Locale.getDefault());
        Date date = null;
        try {
            date = sdf1.parse(dateString);
        } catch (ParseException e) {
            Global.ProcException(e);
        }
        return date;
    }

    public static String getDate(int day, int hour, int minute, int second, int milliSecond) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.MILLISECOND, milliSecond);

        Date date = new Date(cal.getTimeInMillis());
        return simpleDateFormat.format(date);
    }

    public static String getMMDD(long time) {
        return new SimpleDateFormat("MM.dd", Locale.getDefault()).format(new Date(time));
    }

    public static String getDateOfAll(long time) {
        return new SimpleDateFormat("YYYY-MM-dd HH:mm", Locale.getDefault()).format(new Date(time));
    }

    public static int get_DAY_OF_WEEK(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);

    }

    /**
     * 获取当前时间距离指定日期时差的大致表达形式
     *
     * @param date 日期
     * @return 时差的大致表达形式
     */
    public static String getDiffTime(long date) {
        SimpleDateFormat format = null;
        String strTime = "";
        // 今天午夜00:00:00的毫秒数-日期毫秒数
        long time = Math.abs(getCurrentMoringMillis() + DAY_MILLIS - date);
        // 一天内
        if (time <= DAY_MILLIS) {
            format = new SimpleDateFormat(DATE_FORMATE_HOUR_MINUTE, Locale.getDefault());
            strTime = "今天".concat(format.format(new Date(date)));
        }
        // 昨天
        else if (time <= 2 * DAY_MILLIS) {
            format = new SimpleDateFormat(DATE_FORMATE_HOUR_MINUTE, Locale.getDefault());
            strTime = "昨天  ".concat(format.format(new Date(date)));
        } else {
            format = new SimpleDateFormat(DATE_FORMATE_SPLITE_BY_POINT, Locale.getDefault());
            strTime = format.format(new Date(date));
        }

        return strTime;
    }


    /**
     * 获取当天凌晨（即前一天24:00:00）的毫秒数
     *
     * @return
     */
    public static long getCurrentMoringMillis() {
        Calendar calendar = Calendar.getInstance(Locale.CHINESE);
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        long millis = calendar.getTimeInMillis();
        return millis;
    }

    /**
     * 获取当天24:00:00的毫秒数
     *
     * @return
     */
    public static long getNextMoringMillis() {
        Calendar calendar = Calendar.getInstance(Locale.CHINESE);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        long millis = calendar.getTimeInMillis();
        return millis;
    }

    /**
     * @param sdfOut
     * @return 当前月开始时间
     */
    public static String getBeginAt_ofMonth(DateFormat sdfOut, int hours) {
        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return sdfOut.format(calendar.getTime());
    }

    /**
     * @return 当前月开始时间描述
     */
    public static int getBeginAt_ofMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return (int) (calendar.getTime().getTime() / 1000);
    }

    /**
     * @return 当前月开时间毫秒数
     */
    public static long getBeginAt_ofMonthMills() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime().getTime();
    }

    /**
     * @return 当前月结束时间
     */
    public static long getEndAt_ofMonth() {
        Calendar calendar = Calendar.getInstance();
        //        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTimeInMillis();
    }

    /**
     * @param sdfOut
     * @return 当前月结束时间
     */
    public static String getEndAt_ofMonth(DateFormat sdfOut) {
        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return sdfOut.format(calendar.getTime());
    }

    /**
     * @param sdfOut
     * @return 当前星期开始时间
     */
    public static String getBeginAt_ofWeek(DateFormat sdfOut) {
        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date());
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return sdfOut.format(calendar.getTime());
    }

    /**
     * @return 当前星期开始时间
     */
    public static long getBeginAt_ofWeek() {
        Calendar calendar = Calendar.getInstance();
        //        calendar.setTime(new Date());
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return calendar.getTime().getTime();
    }

    /**
     * @return 当前星期结束时间
     */
    public static long getEndAt_ofWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return calendar.getTimeInMillis();
    }

    /**
     * @param sdfOut
     * @return 当前星期结束时间
     */
    public static String getEndAt_ofWeek(DateFormat sdfOut) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return sdfOut.format(calendar.getTime());
    }

    /**
     * @param sdfOut
     * @return 当天开始时间
     */
    public static String getBeginAt_ofDay(DateFormat sdfOut) {
        //TODO:服务端会蛋疼的减8个小时，所以这里先+8个小时
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return sdfOut.format(calendar.getTime());
    }

    /**
     * @return 当天开始时间
     */
    public static long getBeginAt_ofDay() {
        //TODO:服务端会蛋疼的减8个小时，所以这里先+8个小时
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime().getTime();
    }

    /**
     * @param sdfOut
     * @return 当天结束时间
     */
    public static String getEndAt_ofDay(DateFormat sdfOut) {
        Calendar calendar = Calendar.getInstance();
        Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return sdfOut.format(calendar.getTime());
    }

    /**
     * @return 当天结束时间
     */
    public static long getEndAt_ofDay() {
        Calendar calendar = Calendar.getInstance();
        Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime().getTime();
    }

    /**
     * 获得一个日期所在的周的星期几
     *
     * @param millSecons
     * @return
     */
    public static String getWeek(long millSecons) {
        Date dd = new Date(millSecons);
        Calendar c = Calendar.getInstance();
        c.setTime(dd);
        int dates = c.get(Calendar.DAY_OF_WEEK);
        return dates + "";
    }

    /**
     * 根据一个日期，返回是星期几的字符串
     *
     * @param millSecons
     * @return
     */
    public static String getWeekStr(long millSecons) {
        String str = "";
        str = getWeek(millSecons);
        if ("1".equals(str)) {
            str = "星期天";
        } else if ("2".equals(str)) {
            str = "星期一";
        } else if ("3".equals(str)) {
            str = "星期二";
        } else if ("4".equals(str)) {
            str = "星期三";
        } else if ("5".equals(str)) {
            str = "星期四";
        } else if ("6".equals(str)) {
            str = "星期五";
        } else if ("7".equals(str)) {
            str = "星期六";
        }
        return str;
    }

    /**
     * 下班分时转时间戳
     * */
    public static String getOutDataOne(String time,String timeGs) {
        SimpleDateFormat sdr = new SimpleDateFormat(timeGs,
                Locale.CHINA);
        Date date;
        String times = null;
        try {
            date = sdr.parse(time);
            long l = date.getTime();
            String stf = String.valueOf(l);
            times = stf.substring(0, stf.length());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return times;
    }

    /**
     *自定义格式转时间戳
     */
    public static String getDataOne(String time,String timeGs) {
        SimpleDateFormat sdr = new SimpleDateFormat(timeGs,
                Locale.CHINA);
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
     * 时间戳转时间
     *
     * @param time
     * @return
     */
    public static String timet(String time,String timeGs) {
        SimpleDateFormat sdr = new SimpleDateFormat(timeGs);
        @SuppressWarnings("unused")
        long lcc = Long.valueOf(time);
        int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        return times;

    }

    /**
     * 获取当前时间
     */
    public static String getNowTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(DATE_FORMATE_AT_MINUTES);
        return sDateFormat.format(new java.util.Date());
    }



    public static class DateSetListener_Datetool implements
            DatePickerDialog.OnDateSetListener {
        TextView textView_showTime;
        public String strDate = "";
        public String strTime = "";
        private OnClick_Callback onClick_callback;

        public DateSetListener_Datetool(TextView textView_showTime) {
            this.textView_showTime = textView_showTime;
        }

        public DateSetListener_Datetool(TextView textView_showTime, OnClick_Callback onClick_callback) {
            this.textView_showTime = textView_showTime;
            this.onClick_callback = onClick_callback;
        }

        public void setOnClick_callback(OnClick_Callback onClick_callback) {
            this.onClick_callback = onClick_callback;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if (strDate != "") {
                return;
            }
            // Calendar月份是从0开始,所以month要加1

            strDate = year + "." + String.format("%02d", (monthOfYear + 1)) + "."
                    + String.format("%02d", dayOfMonth) + " ";
            textView_showTime.setText(strDate);

            onClick_callback.onClick_onTimeSet();
        }

        public interface OnClick_Callback {
            public boolean onClick_onDateSet();

            public boolean onClick_onTimeSet();
        }
    }
}
