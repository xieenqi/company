package com.loyo.oa.v2.tool;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.loyo.oa.v2.application.MainApp;

/**
 * Created by pj on 16/1/29.
 */
public class LocationUtilGD {

    /**
     * 请求定位的最小时间间隔
     */
    private static final long MIN_SCAN_SPAN_MILLS = 1000;
    /**
     * 请求定位的最小距离间隔
     */
    private static final float MIN_SCAN_SPAN_DISTANCE = 250f;

    private LocationManagerProxy mLocationManagerProxy;
    private MAMapLocationListener maMapLocationListener;
    AfterLocation afterLocation;
    MainApp app;

    public LocationUtilGD(Context context, AfterLocation afterLocation) {
        app = (MainApp) context.getApplicationContext();
        startLocate(context);
        this.afterLocation = afterLocation;
    }


    /**
     * 开启定位
     */
    private void startLocate(Context context) {
        mLocationManagerProxy = LocationManagerProxy.getInstance(context.getApplicationContext());
        maMapLocationListener = new MAMapLocationListener();
        mLocationManagerProxy.setGpsEnable(true);
        mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork,
                MIN_SCAN_SPAN_MILLS, MIN_SCAN_SPAN_DISTANCE, maMapLocationListener);
    }


    /**
     * 位置变化回调接口
     */
    private class MAMapLocationListener implements AMapLocationListener {
        @Override
        public void onLocationChanged(Location location) {
            LogUtil.d("高德所定的位置", location.toString());
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            LogUtil.d("高德所定Status的位置", s);
        }

        @Override
        public void onProviderDisabled(String s) {
            LogUtil.d("高德所Provider定的位置", s);
        }

        @Override
        public void onProviderEnabled(String s) {
            LogUtil.d("高德所Enabled定的位置", s);
        }

        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (mLocationManagerProxy != null) {
                mLocationManagerProxy.removeUpdates(maMapLocationListener);
                mLocationManagerProxy.destroy();
            }
            mLocationManagerProxy = null;
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
        if (null == location) {
            afterLocation.OnLocationGDFailed();
            return;
        }

        String address = location.getAddress();
        app.address = address;
        app.longitude = location.getLongitude();
        app.latitude = location.getLatitude();

        if (!TextUtils.isEmpty(address)) {
            LogUtil.d("定位notify高德Location,address : " + address);
            LogUtil.d("定位l高德ocation:" + location.getLatitude() + "," + location.getLatitude());
            afterLocation.OnLocationGDSucessed(address, location.getLongitude(), location.getLatitude(),
                    location.getRoad());
        } else {
            afterLocation.OnLocationGDFailed();
        }
    }

    public interface AfterLocation {
        void OnLocationGDSucessed(String address, double longitude, double latitude, String radius);

        void OnLocationGDFailed();
    }
}

