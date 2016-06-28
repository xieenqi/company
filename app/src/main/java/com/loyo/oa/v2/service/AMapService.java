package com.loyo.oa.v2.service;

import android.app.Service;
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
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.LocateData;
import com.loyo.oa.v2.beans.TrackLog;
import com.loyo.oa.v2.beans.TrackRule;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.GeneralPopView;
import com.loyo.oa.v2.db.LDBManager;
import com.loyo.oa.v2.point.ITrackLog;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.SharedUtil;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.UMengTools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * com.loyo.oa.v2.service
 * 描述 :高德定位服务
 * 作者 : ykb
 * 时间 : 15/8/19.
 */
public class AMapService extends Service {
    private final String TAG = getClass().getSimpleName();
    /**
     * 请求定位的最小时间间隔
     */
    private static final long MIN_SCAN_SPAN_MILLS = 15 * 1000;
    /**
     * 请求定位的最小距离间隔【定位精度】
     */
    private static final float MIN_SCAN_SPAN_DISTANCE = 280f;

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
    private RestAdapter mRestAdapter;
    private String oldAddress = "";

    private static AMapLocationClient locationClient = null;
    private static AMapLocationClientOption locationOption = null;

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
        app = (MainApp) getApplicationContext();
        ldbManager = new LDBManager();
        mRestAdapter = new RestAdapter.Builder()
                .setEndpoint(Config_project.SERVER_URL())
                .setLogLevel(RestAdapter.LogLevel.NONE)
                .build();

        startLocate();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        acquireWakeLock();
        if (intent != null && intent.hasExtra("track")) {
            trackRule = (TrackRule) intent.getSerializableExtra("track");
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        releaseWakeLock();
        stopLocate();
        TrackRule.StartTrackRule(10 * 1000);
        super.onDestroy();
    }

    /**
     * 开启定位
     */
    private void startLocate() {
//        mLocationManagerProxy = LocationManagerProxy.getInstance(this.getApplicationContext());
//        maMapLocationListener = new MAMapLocationListener();
//        mLocationManagerProxy.setGpsEnable(true);
//        mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, MIN_SCAN_SPAN_MILLS, MIN_SCAN_SPAN_DISTANCE, maMapLocationListener);
        maMapLocationListener = new MAMapLocationListener();
        locationClient = new AMapLocationClient(app);
        locationOption = new AMapLocationClientOption();
        locationOption.setGpsFirst(true);//设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
        //* 注意：只有在高精度模式下的单次定位有效，其他方式无效
        locationOption.setInterval(1000 * 60 * 2);// 设置发送定位请求的时间间隔,最小值为1000，如果小于1000，按照1000算
        locationOption.setOnceLocation(false);//false持续定位 true单次定位
        locationOption.setHttpTimeOut(15000);//设置联网超时时间
        locationOption.setNeedAddress(true);
        // 设置定位模式为高精度模式
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
            if (!checkRule()) {
                SharedUtil.remove(app, "lat");
                SharedUtil.remove(app, "lng");
                return;
            }

            if (aMapLocation != null) {
                /*不获取服务器时间*/
                //getCurrentTime(aMapLocation);
                dealLocation(aMapLocation, System.currentTimeMillis());
            } else {
                LogUtil.d(TAG + "位置回调失败！");
            }

        }
    }

    /**
     * 处理轨迹
     *
     * @param aMapLocation
     * @param currentTime
     */
    private void dealLocation(AMapLocation aMapLocation, long currentTime) {
        String address = aMapLocation.getAddress();
        float accuracy = aMapLocation.getAccuracy();//定位精度
        String provider = aMapLocation.getProvider();//获取定位提供者
        String time = MainApp.getMainApp().df1.format(new Date(aMapLocation.getTime()));
        boolean isTimeMin = currentTime - aMapLocation.getTime() >= 2 * 60 * 1000;
        LogUtil.d("轨迹定位：" + "时间 : " + time + " 模式 : " + provider + " 地址是否有效 : " +
                (!TextUtils.isEmpty(address)) + " 纬度 : " + aMapLocation.getLatitude() +
                " 经度 : " + aMapLocation.getLongitude() + " 精度 : " + accuracy + " 缓存 : " + isCache +
                " 定位信息：" + aMapLocation.getErrorInfo() + "--" + aMapLocation.getLocationDetail());
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
        //排除偏移巨大的点:非gps时地址为空、经纬度为0、精度小于等于0或大于150、是缓存的位置 (!TextUtils.equals("gps", provider) && !  || isCache
        if (TextUtils.isEmpty(address) ||
                (aMapLocation.getLatitude() == 0 && aMapLocation.getLongitude() == 0)
                || accuracy <= 0 || accuracy > MIN_SCAN_SPAN_DISTANCE || oldAddress.equals(address)) {
            LogUtil.d("当前位置偏移量很大，直接return");
            return;
        }
        if (Global.isConnected()) {
            if (isEmptyStr(address)) {
                aMapLocation.setAddress("未知地址");
            }
        } else {
            aMapLocation.setAddress("未知地址(离线)");
        }

        processLocation(aMapLocation);
    }

    /**
     * 检测轨迹规则 后台是否产生轨迹
     *
     * @return
     */
    private boolean checkRule() {
        boolean unRuleable = trackRule == null || trackRule.getWeekdays() == null || trackRule.getWeekdays().length() != 7;
        if (unRuleable) {
            LogUtil.d("checkRule,轨迹规则【设置】错误，trackRule is null ? : " + (trackRule == null) +
                    " weekdays : " + (trackRule == null ? "NULL" : trackRule.getWeekdays().length()));
        }

        int day_of_week = DateTool.get_DAY_OF_WEEK(new Date());
        day_of_week = day_of_week == 1 ? 7 : day_of_week - 1;

        boolean unInDay = true;
        if (!TextUtils.isEmpty(trackRule.getWeekdays()) && trackRule.getWeekdays().length() >= day_of_week) {
            unInDay = '1' != (trackRule.getWeekdays().charAt(day_of_week - 1));
        }
        if (unInDay) {
            LogUtil.d("checkRule,当日未【设置】上报轨迹,weekdays : " + trackRule.getWeekdays() + " dayofweek : " + day_of_week);
        }

        boolean isInTime = false;
        SimpleDateFormat sdf = app.df6;
        String currentDate = sdf.format(new Date());
        try {
            Date currDate = sdf.parse(currentDate);
            Date startDate = sdf.parse(trackRule.startTime);
            Date endDate = sdf.parse(trackRule.endTime);

            if (currDate.after(startDate) && currDate.before(endDate)) {
                isInTime = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!isInTime) {
            LogUtil.d("checkRule,该时间段内未【设置】上报轨迹");
        }

        if (!unRuleable && !unInDay && isInTime) {
            return true;
        }
        return false;
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

            if (distance != 0.0 && distance < MIN_SCAN_SPAN_DISTANCE) {
                LogUtil.d("小于请求定位的最小间隔！");
                return;
            }
        }
        if (Global.isConnected()) {
            uploadLocation(aMapLocation);
            if (isCache) {
                uploadCacheLocation();
            }
        } else {
            isCache = true;
            LocateData data = buildLocateData(aMapLocation);
            ldbManager.addLocateData(data);
        }
//        uploadLocation(aMapLocation);
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

        ArrayList<TrackLog> trackLogs = new ArrayList<>(Arrays.asList(new TrackLog(address, longitude + "," + latitude, System.currentTimeMillis() / 1000)));
        HashMap<String, Object> jsonObject = new HashMap<>();
        jsonObject.put("tracklogs", trackLogs);

        app.getRestAdapter().create(ITrackLog.class).uploadTrackLogs(jsonObject, new RCallback<Object>() {
            @Override
            public void success(Object trackLog, Response response) {
                LogUtil.d(TAG + "uploadLocation,轨迹上报成功,address : " + address);
                HttpErrorCheck.checkResponse("上报轨迹", response);
                SharedUtil.put(app.getApplicationContext(), FinalVariables.LAST_TRACKLOG, "1|" + app.df1.format(new Date()));

                SharedUtil.put(app, "lat", String.valueOf(latitude));
                SharedUtil.put(app, "lng", String.valueOf(longitude));
                SharedUtil.put(app, "address", address);
            }

            @Override
            public void failure(RetrofitError error) {
//                HttpErrorCheck.checkError(error);
                LogUtil.d(TAG + " uploadLocation,轨迹上报失败");
                LocateData data = buildLocateData(location);
                ldbManager.addLocateData(data);
                SharedUtil.put(app.getApplicationContext(), FinalVariables.LAST_TRACKLOG, "2|" + app.df1.format(new Date()));
                //fixes bugly1043 空指针异常 v3.1.1 ykb 07-15
                String userName = MainApp.user == null || StringUtil.isEmpty(MainApp.user.getRealname()) ? "" : MainApp.user.getRealname();
                UMengTools.sendCustomErroInfo(getApplicationContext(), location);
                Global.ProcException(new Exception(userName + " 轨迹上报失败:" + error.getMessage()));

                if (Config_project.is_developer_mode) {
                    GeneralPopView generalPopView = new GeneralPopView(getApplicationContext(), true);
                    generalPopView.setMessage(error.getMessage());
                    generalPopView.setCanceledOnTouchOutside(true);
                    generalPopView.show();
                }
                super.failure(error);
            }
        });
    }

    /**
     * 上传缓存轨迹
     */
    private void uploadCacheLocation() {
        synchronized (AMapService.class) {
            List<LocateData> datas = ldbManager.getAllLocateDatas();
            TrackLog[] trackLogs = buildTrackLogs(datas);
            if (null != trackLogs && trackLogs.length > 0) {
                HashMap<String, Object> tracklogsMap = new HashMap<>();
                tracklogsMap.put("tracklogs", trackLogs);
                app.getRestAdapter().create(ITrackLog.class).uploadTrackLogs(tracklogsMap, new RCallback<Object>() {
                    @Override
                    public void success(Object o, Response response) {
                        HttpErrorCheck.checkResponse("轨迹上传成功： ", response);
                        isCache = false;
                        ldbManager.clearAllLocateDatas();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        super.failure(error);

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
