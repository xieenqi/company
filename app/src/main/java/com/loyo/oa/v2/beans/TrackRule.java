package com.loyo.oa.v2.beans;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.text.TextUtils;

import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.point.ITrackLog;
import com.loyo.oa.v2.service.TrackLogRecevier;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.SharedUtil;

import org.joda.time.DateTime;

import java.io.Serializable;

import retrofit.client.Response;

public class TrackRule implements Serializable {
    public static final int RequestCode = 234;
    public static final String RequestAction = "repeating";

    private Company company;
    private String createdAt;
    private User creator;
    private String deptIds;
    private int duration;
    private boolean enable;
    private String endtime;
    private String groupIds;
    private String id;
    private String starttime;
    private String updatedAt;
    private User user;
    private String userIds;
    private String weekdays;

    public TrackRule() {
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public String getDeptIds() {
        return deptIds;
    }

    public void setDeptIds(String deptIds) {
        this.deptIds = deptIds;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(String groupIds) {
        this.groupIds = groupIds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUserIds() {
        return userIds;
    }

    public void setUserIds(String userIds) {
        this.userIds = userIds;
    }

    public String getWeekdays() {
        return TextUtils.isEmpty(weekdays) ? "" : weekdays;
    }

    public void setWeekdays(String weekdays) {
        this.weekdays = weekdays;
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
            return;
        }

        final MainApp app = MainApp.getMainApp();
        if (Global.isConnected()) {
            app.getRestAdapter().create(ITrackLog.class).getTrackRule(new RCallback<TrackRule>() {
                @Override
                public void success(TrackRule trackRule, Response response) {
                    if (null != trackRule) {
                        DBManager.Instance().putTrackRule(MainApp.gson.toJson(trackRule));
                        SendTrackRuleBroadcast(app, trackRule);
                    }
                }
            });
        } else {
            TrackRule trackRule = DBManager.Instance().getTrackRule();
            if (trackRule != null) {
                SendTrackRuleBroadcast(app, trackRule);
            }
        }
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
     * 开启定位闹钟
     *
     * @param mContext
     * @param trackRule
     * @param delayMills
     */
    private static synchronized void StartTrackRuleAlarm(Context mContext, TrackRule trackRule, long delayMills) {
        if (trackRule.getDuration() <= 0) {
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
