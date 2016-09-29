package com.loyo.oa.v2.service;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.APSService;
import com.instacart.library.truetime.TrueTime;
import com.loyo.oa.v2.db.LocationDBManager;

import java.util.Date;

/**
 * Created by EthanGong on 16/9/26.
 */

public class TrackLocationService extends APSService {

    public final static int TRACK_PERIOD_SECONDS = 2;

    private TrackLocationService.LocalBinder mBinder = new TrackLocationService.LocalBinder();
    private LocationUpdateListener locationUpdateListener;
    private LocationDBManager mLocationDBManager;

    private static AMapLocationClient locationClient = null;

    public class LocalBinder extends Binder {
        public TrackLocationService getService() {
            return TrackLocationService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mLocationDBManager = LocationDBManager.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        acquireWakeLock();
//        app = (MainApp) getApplicationContext();
//        ldbManager = new LDBManager();
//        userOnlineTime();
        startLocate();

        return START_REDELIVER_INTENT;
    }

    /**
     * 开启定位
     */
    private void startLocate() {
        if (locationClient != null && locationClient.isStarted()) {
            return;
        }

        locationUpdateListener = new TrackLocationService.LocationUpdateListener();
        locationClient = new AMapLocationClient(getApplicationContext());
        AMapLocationClientOption locationOption = new AMapLocationClientOption();
        locationOption.setGpsFirst(true);
        locationOption.setInterval(1000 * TRACK_PERIOD_SECONDS);
        locationOption.setOnceLocation(false);
        locationOption.setHttpTimeOut(15000);
        locationOption.setNeedAddress(false);
        // 设置定位模式为低功耗模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 设置定位监听
        locationClient.setLocationListener(locationUpdateListener);
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
        locationClient.startAssistantLocation();
    }

    /**
     * 停止定位请求
     */
    private void stopLocate() {
        if (null != locationClient) {
            if (locationClient.isStarted()) {
                locationClient.stopLocation();
            }
            locationClient.onDestroy();
            locationClient = null;
        }
    }

    @Override
    public void onDestroy() {
//        releaseWakeLock();
//        stopLocate();
//        TrackRule.StartTrackRule(10 * 1000);
//        recycleTimer();
        stopLocate();
        super.onDestroy();
    }

    /**
     * 位置变化回调接口
     */
    private class LocationUpdateListener implements AMapLocationListener {

        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {

            /* 获取校准时间 */
            Date date = new Date();
            try {
                date = TrueTime.now();
            }
            catch (Exception e) {
            }

            LocationDBManager.LocationEntity entity =
                    new LocationDBManager.LocationEntity(date.getTime()/1000,
                            aMapLocation.getLatitude(),
                            aMapLocation.getLongitude(),
                            aMapLocation.getAccuracy(),
                            aMapLocation.getProvider(),
                            aMapLocation.getAddress());

            if (mLocationDBManager != null) {
                mLocationDBManager.addLocation(entity);
            }

            getApplicationContext().startService(new Intent(getApplicationContext(), UploadTrackLocationService.class));
        }
    }
}
