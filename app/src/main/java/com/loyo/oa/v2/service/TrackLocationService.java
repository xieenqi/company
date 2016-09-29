package com.loyo.oa.v2.service;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.APSService;
import com.instacart.library.truetime.TrueTime;
import com.loyo.oa.v2.beans.TrackLog;
import com.loyo.oa.v2.db.LocationDBManager;
import com.loyo.oa.v2.point.ITrackLog;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.TrackLocationManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by EthanGong on 16/9/26.
 */

public class TrackLocationService extends APSService {

    public final static int TRACK_PERIOD_SECONDS = 30;
    public final static int UPLOAD_LOCATIONS_COUNT = 20;
    private boolean isUploading;

    private TrackLocationService.LocalBinder mBinder = new TrackLocationService.LocalBinder();
    private LocationUpdateListener locationUpdateListener;
    private LocationDBManager mLocationDBManager;

    private static AMapLocationClient locationClient = null;

    public static void stopTrackLocation() {
        if (locationClient.isStarted()) {
            locationClient.stopLocation();
        }
        locationClient.onDestroy();
        locationClient = null;
    }

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
        isUploading = false;
        mLocationDBManager.clearAllUploadingFlag();
        Log.v("debug", "onCreate");
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

        if (locationClient != null) {
            stopLocate();
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
        Log.v("debug", "onDestroy");
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

            if (! TrackLocationManager.getInstance().needTracking(date)) {
                return;
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

            uploadNext();
        }
    }

    private synchronized void uploadNext() {
        Log.v("debug", "uploadNext start");

        //  TODO: 检查网络状况, 网络条件允许时开始上传数据
        //

        //  TODO: 检查当前用户和token有效性, 选择对应用户数据上传
        //

        if (isUploading) {
            // Log.v("debug", "uploadNext end2");
            return;
        }
        List<LocationDBManager.LocationEntity> list = mLocationDBManager.getOldestLocationsByLimit(UPLOAD_LOCATIONS_COUNT);

        if (list.size() <= 0) {
            isUploading = false;
            return;
        }
        else if (list.size() < UPLOAD_LOCATIONS_COUNT) {
            Date date = new Date();
            try {
                date = TrueTime.now();
            }
            catch (Exception e) {}
            if (date.getTime()/1000 - list.get(list.size()-1).timestamp
                    < TrackLocationService.TRACK_PERIOD_SECONDS + 1) {
                isUploading = false;
                return;
            }
        }

        isUploading = true;
        uploadLocations(list);

        Log.v("debug", "uploadNext end");
    }

    private synchronized void uploadLocations(final List<LocationDBManager.LocationEntity> list) {
        Log.v("debug", "uploadLocations start");
        final String UUID = StringUtil.getUUID();
        mLocationDBManager.markAsUploadingWithID(list, UUID);
        ArrayList<LocationDBManager.LocationUploadModel> models = new ArrayList<LocationDBManager.LocationUploadModel>();

        for (int i = 0; i < list.size(); i++) {
            LocationDBManager.LocationEntity entity = list.get(i);
            LocationDBManager.LocationUploadModel model = LocationDBManager.LocationUploadModel.instanceFrom(entity);
            if (model != null) {
                models.add(model);
            }
        }

        /* 参数 */
        final HashMap<String, Object> jsonObject = new HashMap<>();
        jsonObject.put("trackLogs", models);

        RestAdapterFactory.getInstance().build(Config_project.NEW_UPLOCATION()).create(ITrackLog.class)
                .newUploadTrack(jsonObject, new Callback<TrackLog>() {
                    @Override
                    public void success(TrackLog trackLog, Response response) {
                        mLocationDBManager.deleteUploadingLocationsWithID(UUID);
                        isUploading = false;
                        uploadNext();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.v("debug", error.toString()+"------");
                        mLocationDBManager.clearAllUploadingFlag();
                        Log.v("debug", "uploadLocations end");
                        isUploading = false;
                        uploadNext();
                    }
                });

    }
}
