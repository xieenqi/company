package com.loyo.oa.v2.beans;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.text.TextUtils;

import com.loyo.oa.v2.activityui.other.bean.Company;
import com.loyo.oa.v2.activityui.other.bean.User;
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

import org.joda.time.DateTime;

import java.io.Serializable;
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

    /**
     * 初始化轨迹规则
     */
    public static void InitTrackRule() {
        if (TextUtils.isEmpty(MainApp.getToken())) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    LogUtil.d("定位 轨迹 空空");
                    InitTrackRule();
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
        SharedUtil.put(app.getApplicationContext(), FinalVariables.LAST_CHECK_TRACKLOG, DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
    }

}
