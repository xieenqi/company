package com.loyo.oa.v2.customview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.loyo.oa.common.utils.DateFormatSet;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.tool.DateTool;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * com.loyo.oa.v2.customview
 * 描述 : 日期&时间选择对话框
 * 作者 : ykb
 * 时间 : 15/7/17.
 */
public class DateTimePickDialog implements DatePicker.OnDateChangedListener, TimePicker.OnTimeChangedListener {
    /**
     * 日期&时间改变的回调接口
     */
    public interface OnDateTimeChangedListener {
        /**
         * 日期&时间变化的回调方法
         *
         * @param year  年
         * @param month 月
         * @param day   日
         * @param hour  小时
         * @param min   分钟
         */
        void onDateTimeChanged(int year, int month, int day, int hour, int min);

        void onCancel();
    }

    private int years, month, day, hour, minutes;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private AlertDialog ad;
    private String dateTime;
    private String initDateTime;
    private Context mContext;
    private String defaultFromat = "yyyy年MM月dd日 HH:mm";
    boolean isVisibilityHour;

    /**
     * 设置默认日期格式
     *
     * @param fromat 格式字符串
     */
    public void setmFromat(String fromat) {
        this.defaultFromat = fromat;
    }

    /**
     * 日期时间弹出选择框构造函数
     *
     * @param context      ：上下文
     * @param initDateTime 初始日期时间值，作为弹出窗口的标题和日期时间初始值
     */
    public DateTimePickDialog(Context context, String initDateTime) {
        mContext = context;
        this.initDateTime = initDateTime;
    }

    /**
     * @param context
     * @param initDateTime
     * @param isVisibilityHour 【是否隐藏 小时选择】 默认显示
     */
    public DateTimePickDialog(Context context, String initDateTime, boolean isVisibilityHour) {
        mContext = context;
        this.initDateTime = initDateTime;
        this.isVisibilityHour = isVisibilityHour;
    }

    public void init(DatePicker datePicker, TimePicker timePicker) {

        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        if (!(null == initDateTime || "".equals(initDateTime))) {
            calendar = this.getCalendarByInintData(initDateTime);
        } else {
            initDateTime = calendar.get(Calendar.YEAR) + "年"
                    + calendar.get(Calendar.MONTH) + "月"
                    + calendar.get(Calendar.DAY_OF_MONTH) + "日 "
                    + calendar.get(Calendar.HOUR_OF_DAY) + ":"
                    + calendar.get(Calendar.MINUTE);
        }

        datePicker.init(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), this);
        timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
    }

    /**
     * 弹出日期时间选择框方法
     *
     * @param :为需要设置的日期时间文本编辑框
     * @param isOver:          是否判断 过去时间
     * @return
     */
    public AlertDialog dateTimePicKDialog(final OnDateTimeChangedListener listener, final boolean isOver, String nebName) {

        Locale.setDefault(Locale.CHINA);//设置地区，让显示时间为中文
        LinearLayout dateTimeLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.date_pick_layout, null);
        datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);
        timePicker = (TimePicker) dateTimeLayout.findViewById(R.id.timepicker);
        init(datePicker, timePicker);
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(this);
        if (isVisibilityHour)
            timePicker.setVisibility(View.GONE);
        ad = new AlertDialog.Builder(mContext)
                .setTitle(initDateTime)
                .setView(dateTimeLayout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
//                        String timeFu = years + "." + String.format("%02d", (month + 1)) + "." + day + " " + hour + ":" + minutes;

                        long time= com.loyo.oa.common.utils.DateTool.getStamp(years,month,day,hour,minutes,0);
                        try {
//                            if (!isOver && Integer.parseInt(DateTool.getDataOne(timeFu, "yyyy.MM.dd HH:mm"))
//                                    < Integer.parseInt(DateTool.getDataOne(DateTool.getNowTime(DateTool.DATE_FORMATE_SPLITE_BY_POINT), "yyyy.MM.dd HH:mm"))) {
                            //TODO 这里点不可以直接提交,选择的日期没有秒,获取的当前日志包括秒,可以-60,就表示1秒内可以提交
                            if (!isOver && time< com.loyo.oa.common.utils.DateTool.getStamp(true)) {
                                Toast.makeText(mContext, "不能选择过去时间!", Toast.LENGTH_SHORT).show();
                            } else {
                                listener.onDateTimeChanged(years, month, day, hour, minutes);
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton(nebName, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        listener.onCancel();
                        ad.dismiss();
                    }
                }).show();

        onDateChanged(null, 0, 0, 0);
        return ad;
    }

    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        onDateChanged(null, 0, 0, 0);
    }

    public void onDateChanged(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
        // 获得日历实例
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        years = datePicker.getYear();
        month = datePicker.getMonth();
        day = datePicker.getDayOfMonth();
        hour = timePicker.getCurrentHour();
        minutes = timePicker.getCurrentMinute();

        calendar.set(datePicker.getYear(), datePicker.getMonth(),
                datePicker.getDayOfMonth(), timePicker.getCurrentHour(),
                timePicker.getCurrentMinute());
        SimpleDateFormat sdf = new SimpleDateFormat(defaultFromat);

//        dateTime = sdf.format(calendar.getTime());
        dateTime = DateFormatSet.minuteSdf.format(calendar.getTime());
        ad.setTitle(dateTime);

    }

    /**
     * 实现将初始日期时间2012年07月02日 16:45 拆分成年 月 日 时 分 秒,并赋值给calendar
     *
     * @param initDateTime 初始日期时间值 字符串型
     * @return Calendar
     */
    private Calendar getCalendarByInintData(String initDateTime) {
        Calendar calendar = Calendar.getInstance();

        // 将初始日期时间2012年07月02日 16:45 拆分成年 月 日 时 分 秒
        String date = spliteString(initDateTime, "日", "index", "front"); // 日期
        String time = spliteString(initDateTime, "日", "index", "back"); // 时间

        String yearStr = spliteString(date, "年", "index", "front"); // 年份
        String monthAndDay = spliteString(date, "年", "index", "back"); // 月日

        String monthStr = spliteString(monthAndDay, "月", "index", "front"); // 月
        String dayStr = spliteString(monthAndDay, "月", "index", "back"); // 日

        String hourStr = spliteString(time, ":", "index", "front"); // 时
        String minuteStr = spliteString(time, ":", "index", "back"); // 分

        int currentYear = Integer.valueOf(yearStr.trim());
        int currentMonth = Integer.valueOf(monthStr.trim()) - 1;
        int currentDay = Integer.valueOf(dayStr.trim());
        int currentHour = Integer.valueOf(hourStr.trim());
        int currentMinute = Integer.valueOf(minuteStr.trim());

        calendar.set(currentYear, currentMonth, currentDay, currentHour,
                currentMinute);
        return calendar;
    }

    /**
     * 截取子串
     *
     * @param srcStr      源串
     * @param pattern     匹配模式
     * @param indexOrLast
     * @param frontOrBack
     * @return
     */
    public static String spliteString(String srcStr, String pattern,
                                      String indexOrLast, String frontOrBack) {
        String result = "";
        int loc = -1;
        if ("index".equalsIgnoreCase(indexOrLast)) {
            loc = srcStr.indexOf(pattern); // 取得字符串第一次出现的位置
        } else {
            loc = srcStr.lastIndexOf(pattern); // 最后一个匹配串的位置
        }
        if ("front".equalsIgnoreCase(frontOrBack)) {
            if (loc != -1)
                result = srcStr.substring(0, loc); // 截取子串
        } else {
            if (loc != -1)
                result = srcStr.substring(loc + 1, srcStr.length()); // 截取子串
        }
        return result;
    }

}
