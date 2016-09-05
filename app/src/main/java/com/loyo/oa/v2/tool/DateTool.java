package com.loyo.oa.v2.tool;

import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.widget.TextView;

import com.loyo.oa.v2.activityui.attendance.bean.DataSelect;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.Global;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTool {




    /**
     * 1s==1000ms
     */
    protected static final int TIME_MILLISECONDS = 1000;
    /**
     * 时间中的分、秒最大值均为60
     */
    protected static final int TIME_SECONDS = 60;
    /**
     * 时间中的小时最大值
     */
    protected static final int TIME_HOURSES = 24;

    public static final long MINUTE_MILLIS = TIME_SECONDS * TIME_MILLISECONDS;

    public static final long HOUR_MILLIS = MINUTE_MILLIS * TIME_SECONDS;
    /**
     * milliseconds of a day
     */
    public static final long DAY_MILLIS = HOUR_MILLIS * TIME_HOURSES;


    /**
     * yyyy-MM-dd HH:mm:ss 2010-05-11 17:22:26
     */
    public static final String DATE_FORMATE_ALL = "yyyy-MM-dd HH:mm:ss";
    /**
     * 精确到分钟
     */
    public static final String DATE_FORMATE_AT_MINUTES = "yyyy-MM-dd HH:mm";

    /**
     * dd-MM-yyyy, hh:mm
     */
    public static final String DATE_FORMATE_TRANSACTION = "MM.dd HH:mm";
    /**
     * HH:mm
     */
    public static final String DATE_FORMATE_HOUR_MINUTE = "HH:mm";

    public static final String DATE_FORMATE_SPLITE_BY_POINT = "yyyy.MM.dd HH:mm";
    //MM-dd HH:mm
    public static final String DATE_FORMATE_HOUR_YEAR = "MM-dd HH:mm";
    public static Calendar calendar;


    static SimpleDateFormat FORMATE_HOUR_MINUTE = new SimpleDateFormat(DATE_FORMATE_HOUR_MINUTE, Locale.getDefault());
    static SimpleDateFormat FORMATE_HOUR_YEAR = new SimpleDateFormat(DATE_FORMATE_HOUR_YEAR, Locale.getDefault());
    static SimpleDateFormat FORMATE_AT_MINUTES = new SimpleDateFormat(DATE_FORMATE_AT_MINUTES, Locale.getDefault());

    protected DateTool() {
        throw new UnsupportedOperationException(); // 防止子类调用
    }


    /**
     * 【我的考勤】获取年份 月份
     */
    public static ArrayList<DataSelect> getYearAllofMonth(int startYear, int endYear) {
        ArrayList<DataSelect> arrayList = new ArrayList<>();
        SimpleDateFormat yearSf = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthSf = new SimpleDateFormat("MM");
        DataSelect dataSelect = null;
        long nowTimelongs = Long.parseLong(getDataTwo(getNowTime(DATE_FORMATE_SPLITE_BY_POINT), DATE_FORMATE_SPLITE_BY_POINT));

        Calendar calendar = Calendar.getInstance();
        Calendar calendarEnd = Calendar.getInstance();
        calendar.set(Calendar.YEAR, startYear);
        calendar.set(Calendar.MONTH, 0);
        calendarEnd.set(Calendar.YEAR, endYear);
        calendarEnd.set(Calendar.MONTH, (Integer.parseInt(monthSf.format(nowTimelongs)) - 1));

        while (calendar.getTime().getTime() <= calendarEnd.getTime().getTime()) {
            dataSelect = new DataSelect();
            dataSelect.top = monthSf.format(calendar.getTime());
            if (yearSf.format(calendar.getTime()).contains(DateTool.getNowTime("yyyy"))) {
                dataSelect.bottom = "今年";
            } else {
                dataSelect.bottom = yearSf.format(calendar.getTime());
            }
            dataSelect.yearMonDay = yearSf.format(calendar.getTime()) + "年" + monthSf.format(calendar.getTime()) + "月";
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
    public static ArrayList<DataSelect> getYearAllofDay(int startYear, int endYear) {

        SimpleDateFormat yearSf = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthSf = new SimpleDateFormat("MM");
        SimpleDateFormat daySf = new SimpleDateFormat("dd");

        ArrayList<DataSelect> arrayList = new ArrayList();
        DataSelect dataSelect = null;
        long nowTimelongs = Long.parseLong(getDataTwo(getNowTime(DATE_FORMATE_SPLITE_BY_POINT), DATE_FORMATE_SPLITE_BY_POINT));

        Calendar calendar = Calendar.getInstance();
        Calendar calendarEnd = Calendar.getInstance();
        calendar.set(Calendar.YEAR, startYear);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 0);

        calendarEnd.set(Calendar.YEAR, endYear);
        calendarEnd.set(Calendar.MONTH, (Integer.parseInt(monthSf.format(nowTimelongs)) - 1));
        calendarEnd.set(Calendar.DAY_OF_MONTH, Integer.parseInt(daySf.format(nowTimelongs)));

        while (calendar.getTime().getTime() <= calendarEnd.getTime().getTime()) {
            dataSelect = new DataSelect();
            dataSelect.top = daySf.format(calendar.getTime());
            dataSelect.bottom = monthSf.format(calendar.getTime()) + "月";
            dataSelect.yearMonDay = yearSf.format(calendar.getTime()) + "年" + monthSf.format(calendar.getTime())
                    + "月" + daySf.format(calendar.getTime())
                    + "日 " + getWeekStr(calendar.getTime().getTime());
            dataSelect.mapOftime = String.valueOf(calendar.getTime().getTime()).substring(0, 10);

            arrayList.add(dataSelect);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return arrayList;
    }

    public static Long getDateToTimestamp(String strTime, DateFormat sdfOut) {
        Long timestamp = 0L;
        try {
            timestamp = sdfOut.parse(strTime).getTime();
        } catch (Exception e) {
            Global.ProcException(e);
        }
        return timestamp;
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


    public static String getMMDD(long time) {
        return new SimpleDateFormat("MM.dd", Locale.getDefault()).format(new Date(time));
    }

    public static int get_DAY_OF_WEEK(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);

    }

    /**
     * 获取当前时间距离指定日期时差的大致表达形式
     *
     * @param seconds 日期
     * @return 时差的大致表达形式
     */
    public static String getDiffTime(long seconds) {
        String result = "--";
        // 今天午夜00:00:00的毫秒数-日期毫秒数
        long millis = seconds * 1000;
        long diffTime = Math.abs(getCurrentMoringMillis() + DAY_MILLIS - millis);
        // 一天内
        if (diffTime <= DAY_MILLIS) {
            result = "今天  ".concat(FORMATE_HOUR_MINUTE.format(new Date(millis)));
        } else if (diffTime <= 2 * DAY_MILLIS) {// 昨天
            result = "昨天  ".concat(FORMATE_HOUR_MINUTE.format(new Date(millis)));
        } else if (diffTime <= 365 * DAY_MILLIS) {// 一年内
            result = FORMATE_HOUR_YEAR.format(new Date(millis));
        } else {
            result = FORMATE_AT_MINUTES.format(new Date(millis));
        }

        return result;
    }


    public static String getPrettyTimeStringFromMillis(long millis) {

        String result = "--"; // empty return

        long diffTime = Math.abs(getCurrentMoringMillis() + DAY_MILLIS - millis);

        if (diffTime <= DAY_MILLIS) {              /** 一天内 */
            result = "今天  ".concat(FORMATE_HOUR_MINUTE.format(new Date(millis)));

        } else if (diffTime <= 2 * DAY_MILLIS) {   /** 昨天 */
            result = "昨天  ".concat(FORMATE_HOUR_MINUTE.format(new Date(millis)));

        } else if (diffTime <= 365 * DAY_MILLIS) { /** 一年内 */
            result = FORMATE_HOUR_YEAR.format(new Date(millis));

        } else {                                   /** 大于一年，显示年份 */
            result = FORMATE_AT_MINUTES.format(new Date(millis));
        }

        return result;
    }

    public static String getPrettyTimeStringFromSeconds(long seconds) {

        return getPrettyTimeStringFromMillis(seconds * 1000);

    }

    public static String getPrettyTimeStringFromTimestamp(long timeStamp) {

        String timeStampString = String.valueOf(timeStamp);
        if (timeStampString.length() >= 13) {
            return getPrettyTimeStringFromMillis(timeStamp);
        }
        else  {
            return getPrettyTimeStringFromSeconds(timeStamp);
        }
    }

    /**
     * 是否过来我们设置的天数
     *
     * @param date
     * @param dateNUmber
     * @return
     */
    public static boolean getDate(long date, int dateNUmber) {
        SimpleDateFormat format = null;
        String strTime = "";
        // 今天午夜00:00:00的毫秒数-日期毫秒数
        long time = Math.abs(getCurrentMoringMillis() + DAY_MILLIS - date);
        LogUtil.d("检查过了多少时间>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ：" + getDiffTime(date));
        return time < dateNUmber * DAY_MILLIS ? false : true;
    }

    /**
     * 获取当天 开始时间
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
     * 获取当天 结束时间
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
     * 获取过去某天 开始时间
     */
    public static long getSomeDayBeginAt(int index) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(calendar.DAY_OF_MONTH, -(index + 1));//防止下标为0
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime().getTime();
    }

    /**
     * 获取过去某天 结束时间
     */
    public static long getSomeDayEndAt(int index) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(calendar.DAY_OF_MONTH, -(index + 1));//防止下标为0
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime().getTime();
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
     * 获取过去某月 开始时间
     */
    public static long getSomeMonthBeginAt(int index) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(calendar.MONTH, -(index + 1));//防止下标为0
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime().getTime();
    }


    /**
     * 获取过去某月 结束时间
     */
    public static long getSomeMonthEndAt(int index) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(calendar.MONTH, -(index + 1));//防止下标为0
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime().getTime();
    }

    /**
     * 获取过去某星期 开始时间
     */
    public static long getSomeWeekBeginAt(int year, int month, int day) {
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
    public static long getSomeWeekEndAt(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime().getTime();
    }

    /**
     * @return 当前星期开始时间
     */
    public static long getBeginAt_ofWeek() {
        Calendar calendar = Calendar.getInstance();
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
     */
    public static String getOutDataOne(String time, String timeGs) {
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
     * 特殊格式转时间戳
     */
    public static String getDataTwo(String time, String timeGs) {
        SimpleDateFormat sdr = new SimpleDateFormat(timeGs,
                Locale.CHINA);
        Date date;
        String times = null;
        try {
            date = sdr.parse(time);
            long l = date.getTime();
            String stf = String.valueOf(l);
            times = stf.substring(0, 13);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return times;
    }

    /**
     * 自定义格式转时间戳
     */
    public static String getDataOne(String time, String timeGs) {
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
    public static String timet(String time, String timeGs) {
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
    public static String getNowTime(String dataFormate) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(dataFormate);
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
            boolean onClick_onDateSet();

            boolean onClick_onTimeSet();
        }
    }
}
