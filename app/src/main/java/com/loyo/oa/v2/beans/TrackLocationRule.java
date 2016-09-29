package com.loyo.oa.v2.beans;

import android.text.TextUtils;

import com.loyo.oa.v2.activityui.other.bean.User;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.tool.DateTool;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by EthanGong on 16/9/29.
 */

public class TrackLocationRule {

    public String createdAt;  /** 轨迹规则创建时间 */
    public String updatedAt;  /** 轨迹规则更新时间 */
    public String id;         /** 轨迹规则id */

    public User user;         /** 轨迹规则关联用户 */


    public boolean enable;    /** 是否启用轨迹定位 */
    public String startTime;  /** 轨迹定位每天开始时间, 格式HH:mm */
    public String endTime;    /** 轨迹定位结束时间, 格式HH:mm */

    /**
     *  周内轨迹定位开启日, 格式 '1111100', 七位长字符串, '1' 标示当天开启, '0'标示当天关闭
     *  周一到周日, 依序为周一, 周二, 周三, 周四, 周五, 周六, 周日取对应的开启标示
     */
    public String weekdays;


    public boolean weekdayEnable(Date date) {

        int day_of_week = DateTool.get_DAY_OF_WEEK(date);

        /* Weekday从周日开始, 与定义的规则不同 */
        day_of_week = day_of_week == 1 ? 7 : day_of_week - 1;

        boolean result = false; /* 当天是否设置轨迹规则， 是否需要打卡 */
        if (!TextUtils.isEmpty(weekdays) && weekdays.length() >= day_of_week) {
            char bit = weekdays.charAt(day_of_week - 1);
            result = ('0' != bit);
        }

        return result;
    }

    public boolean timeEnable(Date date) {

        boolean isInTimeRange= false; /* 是否在开始和结束时间之间，打卡时间范围内 */
        SimpleDateFormat sdf = MainApp.getMainApp().df6;
        String currentDate = sdf.format(date);
        try {
            Date currDate = sdf.parse(currentDate);
            Date startDate = sdf.parse(startTime);
            Date endDate = sdf.parse(endTime);

            if (currDate.after(startDate) && currDate.before(endDate)) {
                isInTimeRange = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return isInTimeRange;
    }

    public boolean needTracking(Date date) {

        return enable && weekdayEnable(date) /* && timeEnable(date)*/;
    }

    public long alarmDelay(Date date) {
        SimpleDateFormat sdf = MainApp.getMainApp().df6;
        String currentDate = sdf.format(date);
        try {
            Date currDate = sdf.parse(currentDate);
            Date startDate = sdf.parse(startTime);
            if (currDate.after(startDate)) {
                return -1;
            }
            else {
                return startDate.getTime() - currDate.getTime();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
