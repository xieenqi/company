package com.loyo.oa.v2.tool;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.DialogHelp;

import java.util.Date;

/**
 * Created by pj on 16/1/29.
 */
public class LocationUtilGD {
    private MAMapLocationListener maMapLocationListener;
    private AfterLocation afterLocation;
    private MainApp app;
    private Context context;
    private static AMapLocationClient locationClient = null;
    private static AMapLocationClientOption locationOption = null;

    public LocationUtilGD(Context context, AfterLocation afterLocation) {
        app = (MainApp) context.getApplicationContext();
        startLocate(context);
        this.afterLocation = afterLocation;
        this.context = context;
    }

    /**
     * 开启定位
     */
    private void startLocate(final Context context) {
        if (PackageManager.PERMISSION_GRANTED ==
                app.getPackageManager().checkPermission("android.permission.ACCESS_FINE_LOCATION", "com.loyo.oa.v2")) {
            maMapLocationListener = new MAMapLocationListener();
            locationClient = new AMapLocationClient(app);
            locationOption = new AMapLocationClientOption();
            locationOption.setGpsFirst(true);//设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
            //* 注意：只有在高精度模式下的单次定位有效，其他方式无效
            locationOption.setInterval(1000 * 60 * 2);// 设置发送定位请求的时间间隔,最小值为1000，如果小于1000，按照1000算
            locationOption.setOnceLocation(false);//false持续定位 true单次定位
            locationOption.setHttpTimeOut(10000);//设置联网超时时间
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
        } else {
//          Global.Toast("你没有配置定位权限");
            DialogHelp.cancelLoading();
        }
    }


    /**
     * 位置变化回调接口
     */
    private class MAMapLocationListener implements com.amap.api.location.AMapLocationListener {

        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            LogUtil.d(aMapLocation.getRoad() + " <高德所定的位置:> " + aMapLocation.getAddress());
            notifyLocation(aMapLocation);
        }
    }

    /**
     * 通知定位结果
     *
     * @param location
     */
    void notifyLocation(AMapLocation location) {
        //友盟统计定位失败的信息
        UMengTools.sendCustomErroInfo(context, location);
        String time = MainApp.getMainApp().df10.format(new Date(location.getTime()));
        LogUtil.d("定位回调数据：" + "时间 : " + time +
                " 模式 : " + location.getProvider()
                + " 地址是否有效 : " + (!TextUtils.isEmpty(location.getAddress()))
                + " 纬度 : " + location.getLatitude()
                + " 经度 : " + location.getLongitude()
                + " 精度 : " + location.getAccuracy()
                + " 定位结果信息：" + location.getErrorInfo() + "--" + location.getLocationDetail());
        if (null == location) {
            afterLocation.OnLocationGDFailed();
            return;
        }

        app.address = location.getAddress();
        app.message = location.getStreet()+location.getStreetNum();
        app.longitude = location.getLongitude();
        app.latitude = location.getLatitude();
        app.cityCode = location.getCityCode();

        if (!TextUtils.isEmpty(location.getAddress())) {
            LogUtil.d("定位notify高德Location,address : " + location.getAddress());
            afterLocation.OnLocationGDSucessed(location.getAddress(), location.getLongitude(), location.getLatitude(),
                    location.getRoad());
        } else {
            afterLocation.OnLocationGDFailed();
        }
    }

    public interface AfterLocation {
        void OnLocationGDSucessed(String address, double longitude, double latitude, String radius);
        void OnLocationGDFailed();
    }

    /**
     * 停止定位
     */
    public static void sotpLocation() {
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

    /**
     * 用户是否配置定位权限     * @return
     */
    public static boolean permissionLocation() {
        if (PackageManager.PERMISSION_GRANTED ==
                MainApp.getMainApp().getPackageManager().checkPermission("android.permission.ACCESS_FINE_LOCATION", "com.loyo.oa.v2")) {
            return true;
        }
        return false;
    }
}

