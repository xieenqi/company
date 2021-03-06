package com.loyo.oa.v2.service;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.APSService;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.loyo.oa.common.utils.DateFormatSet;
import com.loyo.oa.tracklog.api.TrackLogService;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.LocateData;
import com.loyo.oa.v2.beans.TrackLog;
import com.loyo.oa.v2.beans.TrackRule;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.db.LDBManager;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.SharedUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * com.loyo.oa.v2.service
 * 描述 :高德定位服务
 * 作者 : ykb
 * 时间 : 15/8/19.
 */
public class AMapService extends APSService {
    private final String TAG = getClass().getSimpleName();
    /**
     * 请求定位的最小时间间隔
     */
    private static final long MIN_SCAN_SPAN_MILLS = 15 * 1000;
    /**
     * 【定位精度】
     */
    private static final float MIN_SCAN_SPAN_DISTANCE = 255f;
    /**
     * 请求定位的最小距离间隔
     */
    private static final float MAX_SPAN_DISTANCE = 2400f;

    private PowerManager.WakeLock wakeLock;
    private PowerManager manager;
    //    private LocationManagerProxy mLocationManagerProxy;
    private MAMapLocationListener maMapLocationListener;
    private MainApp app;
    private boolean stopped;
    private TrackRule trackRule;
    private LocalBinder mBinder = new LocalBinder();
    private LDBManager ldbManager;
    private boolean isCache;//是否有缓存
    private String oldAddress = "";

    private static AMapLocationClient locationClient = null;
    private static AMapLocationClientOption locationOption = null;
    Timer timer;

    public class LocalBinder extends Binder {
        public AMapService getService() {
            return AMapService.this;
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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        acquireWakeLock();
        app = (MainApp) getApplicationContext();
        ldbManager = new LDBManager();
        userOnlineTime();
        if (intent != null && intent.hasExtra("track")) {
            trackRule = (TrackRule) intent.getSerializableExtra("track");
            if (locationClient == null || !locationClient.isStarted()) {
                startLocate();//定位是否在运行 如果在运行就不重复启动定位
            }
            //服务运行 通知栏显示 调用startForegound，让你的Service所在的线程成为前台进程
            Notification notification = new Notification();
            notification.flags = Notification.FLAG_ONGOING_EVENT;
            notification.flags |= Notification.FLAG_NO_CLEAR;
            notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
            startForeground(1, notification);
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        releaseWakeLock();
        stopLocate();
        TrackRule.StartTrackRule(10 * 1000);
        recycleTimer();
        super.onDestroy();
    }

    /**
     * 开启定位
     */
    private void startLocate() {
//        if (null != maMapLocationListener)
//            maMapLocationListener = null;
//        if (null != locationClient)
//            locationClient = null;
//        if (null != locationOption)
//            locationOption = null;
        if (locationClient != null) {
            locationClient.stopLocation();
            locationClient.stopAssistantLocation();
        }

        maMapLocationListener = new MAMapLocationListener();
        locationClient = new AMapLocationClient(app);
        locationOption = new AMapLocationClientOption();
        locationOption.setGpsFirst(true);//设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
        //* 注意：只有在高精度模式下的单次定位有效，其他方式无效
        locationOption.setInterval(1000 * 60 * 2);// 设置发送定位请求的时间间隔,最小值为1000，如果小于1000，按照1000算
        locationOption.setOnceLocation(false);//false持续定位 true单次定位
        locationOption.setHttpTimeOut(15000);//设置联网超时时间
        locationOption.setNeedAddress(true);
        // 设置定位模式为低功耗模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        // 设置定位监听
        locationClient.setLocationListener(maMapLocationListener);
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
        locationClient.startAssistantLocation();
    }

    /**
     * 位置变化回调接口
     */
    private class MAMapLocationListener implements AMapLocationListener {

        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            LogUtil.d("=====onLocation轨迹Changed=====aMapLocation");

            releaseWakeLock();//释放cpu
            if (!TrackRule.checkRule(trackRule)) {
                SharedUtil.remove(app, "lat");
                SharedUtil.remove(app, "lng");
                return;
            }

            if (aMapLocation != null) {
                /*不获取服务器时间*/
                //getCurrentTime(aMapLocation);
                dealLocation(aMapLocation);
            } else {
                LogUtil.d(TAG + "位置回调失败！");
            }

        }
    }

    /**
     * 处理轨迹
     *
     * @param aMapLocation
     */
    private void dealLocation(AMapLocation aMapLocation) {
        String address = aMapLocation.getAddress();
        float accuracy = aMapLocation.getAccuracy();//定位精度
        String provider = aMapLocation.getProvider();//获取定位提供者
        String time = DateFormatSet.secondCommonSdf.format(new Date(aMapLocation.getTime()));
        LogUtil.d("【轨迹定位】：" + "时间 : " + time + " 模式 : " + provider + " 地址是否有效 : " +
                (!TextUtils.isEmpty(address)) + " 纬度 : " + aMapLocation.getLatitude() +
                " 经度 : " + aMapLocation.getLongitude() + " 精度 : " + accuracy + " 缓存 : " + isCache +
                " 定位信息：" + aMapLocation.getErrorInfo() + "--" + aMapLocation.getLocationDetail());
        isCache = SharedUtil.getBoolean(app, "isCache");
        if (isCache) {//上传缓存的地址数据
            uploadCacheLocation();
        }
        if (isEmptyStr(address)) {//正常定位 没有获取到地址 就拼接一个地址
            StringBuilder addressBuilder = new StringBuilder();
            combineAddress(aMapLocation.getProvince(), addressBuilder);
            combineAddress(aMapLocation.getCity(), addressBuilder);
            combineAddress(aMapLocation.getDistrict(), addressBuilder);
            combineAddress(aMapLocation.getRoad(), addressBuilder);
            combineAddress(aMapLocation.getPoiName(), addressBuilder);

            LogUtil.d("源地址无效,组合的地址 : " + (TextUtils.isEmpty(addressBuilder.toString()) ? "NULL" : addressBuilder.toString()));
            address = addressBuilder.toString();
        }
        oldAddress = SharedUtil.get(app, "address");
        SharedUtil.put(app, "latOld", String.valueOf(aMapLocation.getLatitude()));
        SharedUtil.put(app, "lngOld", String.valueOf(aMapLocation.getLongitude()));
        //排除偏移巨大的点:非gps时地址为空、经纬度为0、精度小于等于0或大于150、是缓存的位置 (!TextUtils.equals("gps", provider) && !  || isCache
        //TextUtils.isEmpty(address) ||
        if ((aMapLocation.getLatitude() == 0 && aMapLocation.getLongitude() == 0)
                || accuracy <= 0 || accuracy > MIN_SCAN_SPAN_DISTANCE || oldAddress.equals(address)) {
            LogUtil.d("当前位置偏移量很大，直接return");
            //缓存有效定位
            return;
        }
//        if (Global.isConnected()) {//检查是否有网络
//            if (isEmptyStr(address)) {
//                aMapLocation.setAddress("未知地址");
//            }
//        } else {
//            aMapLocation.setAddress("未知地址(离线)");
//        }

        processLocation(aMapLocation);
    }

    /**
     * 处理最后的位置信息
     *
     * @param aMapLocation
     */
    private void processLocation(AMapLocation aMapLocation) {
        LogUtil.d(TAG + " processLocation,最终地址 :  " + aMapLocation.getAddress());
        double latitude = aMapLocation.getLatitude();
        double longitude = aMapLocation.getLongitude();
        String lat = SharedUtil.get(app, "lat");
        String lng = SharedUtil.get(app, "lng");
        if (!TextUtils.isEmpty(lat) && !TextUtils.isEmpty(lng)) {
            double tempLat = Double.parseDouble(lat);
            double tempLng = Double.parseDouble(lng);

            LatLng lastLatLng = new LatLng(tempLat, tempLng);
            LatLng newLatLng = new LatLng(latitude, longitude);
            double distance = AMapUtils.calculateLineDistance(lastLatLng, newLatLng);//根据用户的起点和终点经纬度计算两点间距离，此距离为相对较短的距离，单位米。
            LogUtil.d("获取到的distance : " + distance);
            LogUtil.d("当前位置的distance:" + (MIN_SCAN_SPAN_DISTANCE));

//            if ((distance != 0.0 && distance < MIN_SCAN_SPAN_DISTANCE)) {
//                LogUtil.d("小于请求定位的最小间隔！");
//                return;
//            }
        }
        uploadLocation(aMapLocation);
        if (Global.isConnected()) {
        } else {
            LocateData data = buildLocateData(aMapLocation);
            ldbManager.addLocateData(data);
            SharedUtil.putBoolean(app, "isCache", true);
//            isCache = true;
        }
    }

    /**
     * 排除异常定位点
     */
    private boolean maxLocation(AMapLocation aMapLocation) {
        String lat = SharedUtil.get(app, "latOld");
        String lng = SharedUtil.get(app, "lngOld");
        double tempLat = Double.parseDouble(lat);
        double tempLng = Double.parseDouble(lng);
        LatLng lastLatLng = new LatLng(tempLat, tempLng);
        double latitude = aMapLocation.getLatitude();
        double longitude = aMapLocation.getLongitude();
        LatLng newLatLng = new LatLng(latitude, longitude);
        double distance = AMapUtils.calculateLineDistance(lastLatLng, newLatLng);
        if (distance > MAX_SPAN_DISTANCE) {//速度超过20米每秒
            return true;
        }
        return false;
    }

    /**
     * 上传轨迹
     *
     * @param location
     */
    private void uploadLocation(final AMapLocation location) {
        final double latitude = location.getLatitude();
        final double longitude = location.getLongitude();
        final String address = location.getAddress();
        ArrayList<TrackLog> trackLogs = new ArrayList<>(Arrays.asList(new TrackLog(longitude
                + "," + latitude, System.currentTimeMillis() / 1000)));
        final HashMap<String, Object> jsonObject = new HashMap<>();
        jsonObject.put("trackLogs", trackLogs);//tracklogs
//新版上传轨迹
        TrackLogService.newUploadTrack(jsonObject)
                .subscribe(new DefaultLoyoSubscriber<TrackLog>() {
                    @Override
                    public void onError(Throwable e) {
                        LocateData data = buildLocateData(location);
                        ldbManager.addLocateData(data);
                        SharedUtil.putBoolean(app, "isCache", true);
                    }

                    @Override
                    public void onNext(TrackLog log) {
                        SharedUtil.put(MainApp.getMainApp(), "lat", String.valueOf(latitude));
                        SharedUtil.put(MainApp.getMainApp(), "lng", String.valueOf(longitude));
                        SharedUtil.remove(MainApp.getMainApp(), "address");
                        SharedUtil.put(MainApp.getMainApp(), "address", address);
                    }
                });
    }

    /**
     * 构建轨迹数据
     *
     * @param aMapLocation
     * @return
     */
    private LocateData buildLocateData(AMapLocation aMapLocation) {
        if (null == aMapLocation) {
            return null;
        }
        LocateData data = new LocateData(aMapLocation.getTime(), aMapLocation.getLatitude(), aMapLocation.getLongitude());
        data.setAccuracy(aMapLocation.getAccuracy());
        data.setAddress(TextUtils.isEmpty(aMapLocation.getAddress()) ? "" : aMapLocation.getAddress());
        data.setProvider(TextUtils.isEmpty(aMapLocation.getProvider()) ? "" : aMapLocation.getProvider());

        return data;
    }


    /**
     * 拼接有效字符串
     *
     * @param str
     * @param builder
     */
    private void combineAddress(String str, StringBuilder builder) {
        if (!isEmptyStr(str)) {
            builder.append(str);
        }
    }

    /**
     * 检测字符串
     *
     * @param str
     * @return
     */
    private boolean isEmptyStr(String str) {
        if (TextUtils.isEmpty(str) || TextUtils.equals("null", str) || TextUtils.equals("NULL", str)) {
            return true;
        }
        return false;
    }


    /**
     * 上传缓存轨迹
     */
    private void uploadCacheLocation() {
        synchronized (AMapService.class) {
            List<LocateData> datas = ldbManager.getAllLocateDatas();
            TrackLog[] trackLogs = buildTrackLogs(datas);
            if (null != trackLogs && trackLogs.length > 0) {
                final HashMap<String, Object> tracklogsMap = new HashMap<>();
                tracklogsMap.put("tracklogs", trackLogs);
                TrackLogService.uploadTrackLogs(tracklogsMap)
                        .subscribe(new DefaultLoyoSubscriber<Object>(LoyoErrorChecker.SILENCE) {
                            @Override
                            public void onNext(Object o) {
                                ldbManager.clearAllLocateDatas();
                                SharedUtil.putBoolean(app, "isCache", false);
                            }
                        });
            }
        }
    }

    /**
     * 构建轨迹数据集
     *
     * @param datas
     * @return
     */
    private TrackLog[] buildTrackLogs(List<LocateData> datas) {
        if (null == datas || datas.isEmpty()) {
            return null;
        }
        TrackLog[] trackLogs = new TrackLog[datas.size()];
        for (int i = 0; i < datas.size(); i++) {
            LocateData data = datas.get(i);
            String gps = data.getLng() + "," + data.getLat();
            String address = data.getAddress();
            long createAt = data.getRecordTime();

            TrackLog trackLog = new TrackLog();
            trackLog.setGps(gps);
            trackLog.setAddress(address);
            trackLog.setCreatedAt(createAt);
            trackLogs[i] = trackLog;
        }
        return trackLogs;
    }


    /**
     * 停止定位请求
     */
    private void stopLocate() {
        if (!stopped) {
            stopped = true;
            if (null != locationClient) {
                /**
                 * 如果AMapLocationClient是在当前Activity实例化的，
                 * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
                 */
                locationClient.onDestroy();
                locationClient = null;
                locationOption = null;
            }
        }
    }

    /**
     * 唤醒cpu
     */
    private void acquireWakeLock() {
        if (null == manager) {
            manager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        }
        boolean isInteractive = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            isInteractive = manager.isInteractive();
        } else {
            isInteractive = manager.isScreenOn();
        }
        if (!isInteractive) {
            if (null == wakeLock) {
                wakeLock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
            }
            if (!wakeLock.isHeld()) {
                wakeLock.acquire();
            }
        }
    }

    /**
     * 释放被唤醒的cpu
     */
    private void releaseWakeLock() {
        if (null != wakeLock && wakeLock.isHeld()) {
            wakeLock.release();
        }
        wakeLock = null;
    }

    /**
     * 计时器 记录用户在线
     */
    private void userOnlineTime() {
        long timerOk = 5 * 60 * 1000;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                TrackLogService.getUserOneLine()
                        .subscribe(new DefaultLoyoSubscriber<Object>(LoyoErrorChecker.SILENCE) {
                            @Override
                            public void onNext(Object o) {

                            }
                        });
            }
        }, timerOk, timerOk);
    }

    /**
     * 回收计时器
     */
    private void recycleTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }


    /**
     * 获取最近的一次位置信息
     *
     * @return
     */
//    public AMapLocation getLastLocation() {
//        return mLocationManagerProxy.getLastKnownLocation(LocationProviderProxy.AMapNetwork);
//    }
    //    /**
//     * 百度反地理编码获取地址
//     *
//     * @param aMapLocation
//     */
//    private boolean convertAddressBMap(final AMapLocation aMapLocation) {
//        com.baidu.mapapi.model.LatLng tempLatLng = LocationUtil.convert(1, aMapLocation.getLatitude(), aMapLocation.getLongitude());
//        final GeoCoder coder = GeoCoder.newInstance();
//        OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
//            @Override
//            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
//
//            }
//
//            @Override
//            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
//                coder.destroy();
//                if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
//                    aMapLocation.setAddress("未知地址");
//                } else {
//                    aMapLocation.setAddress(reverseGeoCodeResult.getAddress());
//                }
//                processLocation(aMapLocation);
//            }
//        };
//        coder.setOnGetGeoCodeResultListener(listener);
//        boolean result = coder.reverseGeoCode(new ReverseGeoCodeOption().location(tempLatLng));
//        if (!result) {
//            LogUtil.d(TAG + "  convertAddressBMap,启动百度反地理编码失败");
//            coder.destroy();
//        }
//
//        return result;
//    }
    /**
     * 获取当前时间
     *
     * @return
     */
//
//    private void getCurrentTime(final AMapLocation aMapLocation) {
//        mRestAdapter.create(IMain.class).getServerTime(new RCallback<ServerTime>() {
//            @Override
//            public void success(ServerTime serverTime, Response response) {
//                HttpErrorCheck.checkResponse("轨迹定位－获取当前时间", response);
//                long time = 0;
//                if (null != serverTime) {
//                    time = serverTime.getNow();
//                }
//
//                if (time <= 0) {
//                    time = System.currentTimeMillis();
//                }
//                dealLocation(aMapLocation, time);
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                super.failure(error);
//                HttpErrorCheck.checkError(error);
//                dealLocation(aMapLocation, System.currentTimeMillis());
//            }
//        });
//    }

}
