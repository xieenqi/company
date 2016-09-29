package com.loyo.oa.v2.tool;

import android.content.Context;
import android.content.Intent;

import com.instacart.library.truetime.TrueTime;
import com.loyo.oa.v2.beans.TrackLocationRule;
import com.loyo.oa.v2.beans.TrackRule;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.point.ITrackLog;
import com.loyo.oa.v2.service.TrackLocationService;

import java.util.Date;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by EthanGong on 16/9/29.
 */
public class TrackLocationManager {

    private static TrackLocationManager ourInstance = new TrackLocationManager();
    public static TrackLocationManager getInstance() {
        return ourInstance;
    }

    private TrackLocationRule mRule;
    private Context mContext;

    private TrackLocationManager() {
    }

    public TrackLocationManager initWithContext(Context context){
        this.mContext = context;
        return this;
    }


    /** 开启轨迹定位 */
    public void startLocationTrackingIfNeeded() {

        if (Global.isConnected()) {
            RestAdapterFactory.getInstance()
                    .build(Config_project.API_URL())
                    .create(ITrackLog.class).getTrackLocationRule(new Callback<TrackLocationRule>() {
                @Override
                public void success(TrackLocationRule rule, Response response) {
                    // TODO:
                    mRule = rule;
                    _startTrackingIfNeeded();
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });

        } else {

            // TODO:
            TrackRule trackRule = DBManager.Instance().getTrackRule();
            _startTrackingIfNeeded();
        }
    }

    private void _startTrackingIfNeeded() {
        if (mRule.needTracking(getTrueTime()) && mContext != null) {
            mContext.startService(new Intent(mContext, TrackLocationService.class));
        }
    }

    public void stopLocationTracking() {
        if (mContext == null) {
            return;
        }
        TrackLocationService.stopTrackLocation();
    }

    /** 时间校正,获取准确的时间,而不是使用本地的系统时间 */
    public Date getTrueTime() {
        Date date = new Date();
        try {
            date = TrueTime.now();
        }
        catch (Exception e) {
        }
        return date;
    }

    public boolean needTracking(Date date) {

        if (mRule == null) {
            return false;
        }
        return mRule.enable && mRule.weekdayEnable(date) && mRule.timeEnable(date);
    }
}
