package com.loyo.oa.v2.beans;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.text.TextUtils;

import com.loyo.oa.v2.activityui.other.model.Company;
import com.loyo.oa.v2.activityui.other.model.User;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.point.ITrackLog;
import com.loyo.oa.v2.service.InitDataService_;
import com.loyo.oa.v2.service.TrackLogRecevier;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.SharedUtil;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.client.Response;

public class TrackRule implements Serializable {
    public static final int RequestCode = 234;
    public static final String RequestAction = "repeating";

    public Company company;
    public String createdAt;
    public User creator;
    public String deptIds;
    public int duration;
    public String groupIds;
    public String id;
    public String updatedAt;
    public User user;
    public String userIds;

    public boolean enable;
    public String endTime;
    public String startTime;//上班时间
    public String weekdays;


    public TrackRule() {
    }


    public String getWeekdays() {
        return TextUtils.isEmpty(weekdays) ? "" : weekdays;
    }

    /**
     * 初始化轨迹规则
     */
    public static void InitTrackRule() {
        if (TextUtils.isEmpty(MainApp.getToken()) && count < 20) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    LogUtil.d("定位 轨迹 空空");
                    InitTrackRule();
                    count++;
                }
            }, 5000);
            return;
        }

        final MainApp app = MainApp.getMainApp();
        if (Global.isConnected()) {
            app.getRestAdapter().create(ITrackLog.class).getTrackRule(new RCallback<TrackRule>() {
                @Override
                public void success(TrackRule trackRule, Response response) {
                    HttpErrorCheck.checkResponse("后台轨迹规则加载成功！", response);
                    if (null != trackRule && trackRule.enable) {
                        DBManager.Instance().putTrackRule(MainApp.gson.toJson(trackRule));
                        SendTrackRuleBroadcast(app, trackRule);
                    }
                }
            });
        } else {
            TrackRule trackRule = DBManager.Instance().getTrackRule();
            if (null != trackRule && trackRule.enable) {
                SendTrackRuleBroadcast(app, trackRule);
            }
        }
    }

    /**
     * 创建轨迹规则,如果网络不通，则使用本地规则
     *
     * @param delayMills
     */
    public static void StartTrackRule(final long delayMills) {
        if (TextUtils.isEmpty(MainApp.getToken())) {
            return;
        }

        final MainApp app = MainApp.getMainApp();

        if (Global.isConnected()) {
            app.getRestAdapter().create(ITrackLog.class).getTrackRule(new RCallback<TrackRule>() {
                @Override
                public void success(TrackRule trackRule, Response response) {
                    if (null != trackRule) {
                        StartTrackRule(trackRule, app);
                        DBManager.Instance().putTrackRule(MainApp.gson.toJson(trackRule));
                    }
                }
            });
        } else {
            TrackRule trackRule = DBManager.Instance().getTrackRule();
            if (null != trackRule) {
                StartTrackRule(trackRule, app);
            }
        }
    }

    private static int count = 0;

    /**
     * 此处更新组织架构  在推送调用
     *
     * @param context
     */
    public static void initUserData(Context context) {
//        Intent intent = new Intent(context, InitDataService.class);
//        context.startService(intent);

        InitDataService_.intent(context).start();
    }

    /**
     * 发送定位服务依赖的广播
     *
     * @param context
     * @param trackRule
     */
    public static void SendTrackRuleBroadcast(Context context, TrackRule trackRule) {
        Intent intent = new Intent(context.getApplicationContext(), TrackLogRecevier.class);
        intent.setAction(TrackRule.RequestAction);
        intent.putExtra("track", trackRule);
        context.sendBroadcast(intent);
    }

    /**
     * 开启定位闹钟  [暂时不用了]
     *
     * @param mContext
     * @param trackRule
     * @param delayMills
     */
    private static synchronized void StartTrackRuleAlarm(Context mContext, TrackRule trackRule, long delayMills) {
        if (trackRule.duration <= 0) {
            return;
        }

        Context context = mContext.getApplicationContext();
        Intent intent = new Intent(context, TrackLogRecevier.class);
        intent.setAction(TrackRule.RequestAction);
        intent.putExtra("track", trackRule);

        PendingIntent sender = PendingIntent.getBroadcast(context, TrackRule.RequestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(sender);
        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + delayMills, sender);
    }

    public static void StartTrackRule(TrackRule trackRule, Context app) {
        StartTrackRuleAlarm(app, trackRule, 30 * 1000);
        //记录最近一次设置轨迹成功的时间
//        SharedUtil.put(app.getApplicationContext(), FinalVariables.LAST_CHECK_TRACKLOG, DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
        SharedUtil.put(app.getApplicationContext(), FinalVariables.LAST_CHECK_TRACKLOG, com.loyo.oa.common.utils.DateTool.getDateTime());
    }


    /**
     * 检测轨迹规则 后台是否产生轨迹
     *
     * @return
     */
    public static boolean checkRule(TrackRule trackRule) {
        boolean unRuleable = trackRule == null || trackRule.getWeekdays() == null || trackRule.getWeekdays().length() != 7;
        if (unRuleable) {
            LogUtil.d("checkRule,轨迹规则【设置】错误，trackRule is null ? : " + (trackRule == null) +
                    " weekdays : " + (trackRule == null ? "NULL" : trackRule.getWeekdays().length()));
            return false;
        }

        int day_of_week = com.loyo.oa.common.utils.DateTool.getWeek();
        day_of_week = day_of_week == 1 ? 7 : day_of_week - 1;

        boolean notNeedCheck = true; /* 当天是否设置轨迹规则， 是否需要打卡 */
        if (!TextUtils.isEmpty(trackRule.getWeekdays()) && trackRule.getWeekdays().length() >= day_of_week) {
            notNeedCheck = '1' != (trackRule.getWeekdays().charAt(day_of_week - 1));
        }
        if (notNeedCheck) {
            LogUtil.d("checkRule,当日未【设置】上报轨迹,weekdays : " + trackRule.getWeekdays() + " dayofweek : " + day_of_week);
        }


        //        boolean isInTime = false; /* 是否在开始和结束时间之间，打卡时间范围内 */
//        SimpleDateFormat sdf = MainApp.getMainApp().df6;
//        String currentDate = sdf.format(new Date());
//        try {
//            Date currDate = sdf.parse(currentDate);
//            Date startDate = sdf.parse(trackRule.startTime);
//            Date endDate = sdf.parse(trackRule.endTime);
//
//            if (currDate.after(startDate) && currDate.before(endDate)) {
//                isInTime = true;
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        long start= com.loyo.oa.common.utils.DateTool.getHourMinStamp(trackRule.startTime);
        long end= com.loyo.oa.common.utils.DateTool.getHourMinStamp(trackRule.endTime);
        String curTime=com.loyo.oa.common.utils.DateTool.getHourMinute(System.currentTimeMillis()/1000); //获取当前时间 eg 12:12
        long cur= com.loyo.oa.common.utils.DateTool.getHourMinStamp(curTime);
        boolean isInTime = false; /* 是否在开始和结束时间之间，打卡时间范围内 */

        if(cur>=start&&cur<=end){
            isInTime= true;
        }else{
            isInTime= false;
        }







        if (!isInTime) {
            LogUtil.d("checkRule,该时间段内未【设置】上报轨迹");
        }

        if (!notNeedCheck && isInTime) {
            return true;
        }
        return false;
    }

}
