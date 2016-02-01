package com.loyo.oa.v2.tool;

import android.content.Context;
import android.text.TextUtils;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.loyo.oa.v2.application.MainApp;


public class LocationUtil {
    LocationClient mLocationClient;
    BDLocationListener mLocationListener;
    AfterLocation afterLocation;
    MainApp app;

    public LocationUtil(Context context, AfterLocation _afterLocation) {
        afterLocation = _afterLocation;
        app = (MainApp) context.getApplicationContext();

        mLocationClient = new LocationClient(context.getApplicationContext());
        mLocationListener = new MyLocationListener();

        startLocation(context);
    }

    /**
     * 开启定位
     *
     * @param context
     */
    public void startLocation(Context context) {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式
        option.setCoorType("gcj02");
        option.setScanSpan(1000);
        option.setIsNeedAddress(true);
//        option.setOpenGps(true);
        option.setLocationNotify(true);
        option.setIsNeedLocationDescribe(true);
//        option.setEnableSimulateGps(false);
        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(mLocationListener);
        mLocationClient.start();
    }

    /**
     * 实现实位回调监听
     */
    class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation location) {
            filterBDLocation(location);
        }
    }

    void filterBDLocation(BDLocation location) {
        String type = "UNKNOWN";
        boolean available = true;
        switch (location.getLocType()) {
            case BDLocation.TypeGpsLocation:
                type = "GPS";
                break;
            case BDLocation.TypeNetWorkLocation:
                type = "NETWORK";
                break;
            case BDLocation.TypeCacheLocation:
                type = "CACHE";
                available = false;
                break;
            case BDLocation.TypeOffLineLocation:
                type = "OFFLINE";
                available = false;
                break;
            default:
                available = false;
                break;
        }

//        LogUtil.d(getClass().getSimpleName(), "locateType : " + location.getLocType() + " " +
//                type + " " + location.getNetworkLocationType() + " time : " + location.getTime() +
//                " 卫星数 : " + location.getSatelliteNumber() + " radius : " +
//                location.getRadius() + " available : " + available);

        mLocationClient.unRegisterLocationListener(mLocationListener);
        mLocationClient.stop();

        notifyLocation(location);
    }

    /**
     * 通知定位结果
     *
     * @param location
     */
    void notifyLocation(BDLocation location) {
        if (null == location) {
            afterLocation.OnLocationFailed();
            return;
        }

        String address = location.getAddrStr();
        app.address = address;
        app.longitude = location.getLongitude();
        app.latitude = location.getLatitude();

        if (!TextUtils.isEmpty(address)) {
            LogUtil.d("定位notifyLocation,address : " + address);
            LogUtil.d("定位location:" + location.getLatitude() + "," + location.getLatitude());
            afterLocation.OnLocationSucessed(address, location.getLongitude(), location.getLatitude(),
                    location.getRadius());
        } else {
            afterLocation.OnLocationFailed();
        }
    }

    public interface AfterLocation {
        void OnLocationSucessed(String address, double longitude, double latitude, float radius);

        void OnLocationFailed();
    }

    /**
     * 其他坐标系与百度坐标系的转换
     *
     * @param from 0,gps;1,其他
     * @param lat
     * @param lng
     * @return
     */
    public synchronized static LatLng convert(int from, double lat, double lng) {
        CoordinateConverter.CoordType type = null;
        if (from == 0) {
            type = CoordinateConverter.CoordType.GPS;
        } else if (from == 1) {
            type = CoordinateConverter.CoordType.COMMON;
        }
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(type).coord(new LatLng(lat, lng));

        return converter.convert();
    }
}
