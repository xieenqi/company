package com.loyo.oa.v2.tool;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EthanGong on 2016/10/19.
 */
public class LocationManager implements com.amap.api.location.AMapLocationListener {
    private static LocationManager ourInstance = new LocationManager();

    private  AMapLocationClient locationClient = null;
    private Context context;
    private List<LocatingCallback> observers;
    private List<LocatingCallback> onceObservers;

    public static LocationManager getInstance() {
        return ourInstance;
    }

    private LocationManager() {
        observers = new ArrayList<>();
        onceObservers = new ArrayList<>();
    }

    public LocationManager initWithContext(Context context) {
        this.context = context;
        locationClient = new AMapLocationClient(context.getApplicationContext());
        AMapLocationClientOption locationOption = new AMapLocationClientOption();
        locationOption.setGpsFirst(true);//设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
        locationOption.setInterval(1000 * 60 * 20);// 设置发送定位请求的时间间隔,最小值为1000，如果小于1000，按照1000算
        locationOption.setOnceLocation(false);//false持续定位 true单次定位
        locationOption.setHttpTimeOut(10000);//设置联网超时时间
        locationOption.setNeedAddress(true);
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        locationClient.setLocationListener(this);
        locationClient.setLocationOption(locationOption);
        return this;
    }

    public void startLocating() {
        if (locationClient == null) {
            return;
        }
        if (locationClient.isStarted() ) {
            return;
        }
        locationClient.startLocation();
    }

    public void stopLocating() {
        if (locationClient == null) {
            return;
        }
        locationClient.stopLocation();
    }

    public LocationManager register(LocatingCallback observer) {
        if (observer == null) {
            return this;
        }
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
        return this;
    }

    public LocationManager observerOnce(LocatingCallback observer) {
        if (observer == null) {
            return this;
        }
        if (!onceObservers.contains(observer)) {
            onceObservers.add(observer);
        }
        return this;
    }

    public void unregister(LocatingCallback observer) {
        if (observer == null) {
            return;
        }
        observers.remove(observer);
    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        stopLocating();
        if (location == null) {
            notifyObservers(false, null);
            return;
        }

        if (location.getErrorCode() != 0) {
            //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
            Log.e("AmapError","location Error, ErrCode:"
                    + location.getErrorCode() + ", errInfo:"
                    + location.getErrorInfo());
            // TODO: 加MTA日志
            notifyObservers(false, null);
            return;
        }

        // TODO: 排除不符合要求的点, 加MTA日志
        if ((location.getLatitude() == 0 && location.getLongitude() == 0)
                || location.getAccuracy() <= 0) {
            notifyObservers(false, null);
            return;
        }

        Location loc = new Location();
        loc.address = location.getAddress();
        loc.latitude = location.getLatitude();
        loc.longitude = location.getLongitude();
        loc.radius = location.getRoad();
        loc.region = location.getDistrict();
        loc.message = location.getStreet() + location.getStreetNum();
        loc.cityCode = location.getCityCode();

        notifyObservers(true, loc);

    }

    private void notifyObservers(boolean suc, Location loc) {
        for (LocatingCallback observer:observers) {
            observer.onLocatingFinished(suc, loc);
        }
        for (LocatingCallback observer:onceObservers) {
            observer.onLocatingFinished(suc, loc);
        }
        onceObservers.clear();
    }

    public interface LocatingCallback {
        void onLocatingFinished(boolean succeed, Location loc);
    }

    public class Location {
        public String address;
        public double longitude;
        public double latitude;
        public String radius;
        public String region;
        public String message;
        public String cityCode;
    }
}
