package com.loyo.oa.v2.tool;

import android.content.Context;
import android.location.LocationManager;

import com.amap.api.location.AMapLocation;
import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.tracklog.api.TrackLogService;
import com.loyo.oa.v2.activityui.other.model.CellInfo;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.TrackLog;
import com.loyo.oa.v2.beans.TrackRule;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import retrofit.RetrofitError;

/**
 * 友盟统计相关方法
 * Created by xeq on 16/3/22.
 */
public class UMengTools {
    protected UMengTools() {
        throw new UnsupportedOperationException(); // 防止子类调用
    }

    /**
     * 自定义统计error信息
     */
    public static void sendCustomErroInfo(Context context, AMapLocation location) {
        if (!"success".equals(location.getErrorInfo())) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//            String time = MainApp.getMainApp().df10.format(new Date(location.getTime()));
            String time = DateTool.getDateTimeReal(location.getTime()/1000);
            CellInfo cellInfo = Utils.getCellInfo();
            StringBuffer erroInfo = new StringBuffer();
            erroInfo.append("品牌：" + cellInfo.getLoyoAgent() + "<->");
            erroInfo.append("版本：" + cellInfo.getLoyoOSVersion() + "<->");
            erroInfo.append("设备硬件版本:" + cellInfo.getLoyoHVersion() + "<->");
            erroInfo.append("GPS开启状态:" + gps + "<->");
            erroInfo.append("网络状态（非数据）:" + network + "<->");
            erroInfo.append("定位结果:" + location.getErrorInfo() + "--" + location.getLocationDetail() + "<->");
            erroInfo.append("网络类型:" + Utils.getNetworkType(context) + "<->");
            erroInfo.append("发生的时间:" + time + "<->");
            if (null != MainApp.user) {
                erroInfo.append("用户信息:" + MainApp.user.name + "-" + MainApp.user.mobile + "-" + MainApp.gson.toJson(MainApp.user.depts));
            }
            LogUtil.d("高德定位设备友盟统计信息：" + erroInfo.toString());
            MobclickAgent.reportError(context, erroInfo.toString());
        }
    }

    /**
     * 上传轨迹失败原因友盟收集
     */
    public static void sendCustomTrajectory(Context context, RetrofitError error, HashMap<String, Object> jsonObject) {
        String errInfo = error.getMessage() +
                " url：" + error.getUrl() + " 定位信息：" + MainApp.gson.toJson(jsonObject)
                + "用户：" + MainApp.gson.toJson(MainApp.user);
        MobclickAgent.reportError(context, errInfo.toString());
    }

    public static void sendLocationInfo(final String address, final double longitude, final double latitude) {
        try {
//            final String date = MainApp.getMainApp().df4.format(new Date(System.currentTimeMillis()));
            final String date = com.loyo.oa.common.utils.DateTool.getDateFriendly(System.currentTimeMillis()/1000);
            LogUtil.d("检查时间: " + date);
            String oldInfo = SharedUtil.get(MainApp.getMainApp(), "sendLocation");
            TrackRule trackrule = DBManager.Instance().getTrackRule();
            if (null == trackrule) {
                LogUtil.d("trackrule为null，就不继续执行");
                return;
            }
            if (!TrackRule.checkRule(trackrule) || (date + address).equals(oldInfo)) {
                LogUtil.d("此时不需要穿轨迹" + address.equals(oldInfo));
                return;
            }
            newUpLocation(address, longitude, latitude, date);
        } catch (Exception e) {
            e.printStackTrace();
            if (!Config_project.isRelease) {
                Global.Toast("数据异常！");
            }
        }
    }

    private static void upLocation(final String address, final double longitude, final double latitude, final String date) {
        ArrayList<TrackLog> trackLogs = new ArrayList<>(Arrays.asList(new TrackLog(address, longitude
                + "," + latitude, System.currentTimeMillis() / 1000)));
        final HashMap<String, Object> jsonObject = new HashMap<>();
        jsonObject.put("tracklogs", trackLogs);
        TrackLogService.uploadTrackLogs(jsonObject)
                .subscribe(new DefaultLoyoSubscriber<Object>(LoyoErrorChecker.SILENCE) {
                    @Override
                    public void onNext(Object o) {
                        SharedUtil.remove(MainApp.getMainApp(), "sendLocation");
                        SharedUtil.put(MainApp.getMainApp(), "sendLocation", date + address);
                    }
                });
    }

    private static void newUpLocation(final String address, final double longitude, final double latitude, final String date) {
        LogUtil.d("newUpLocation");
        ArrayList<TrackLog> trackLogs = new ArrayList<>(Arrays.asList(new TrackLog(longitude
                + "," + latitude, System.currentTimeMillis() / 1000)));
        final HashMap<String, Object> map = new HashMap<>();
        map.put("trackLogs", trackLogs);
        TrackLogService.newUploadTrack(map)
                .subscribe(new DefaultLoyoSubscriber<TrackLog>(LoyoErrorChecker.SILENCE) {
                    @Override
                    public void onNext(TrackLog log) {
                        SharedUtil.remove(MainApp.getMainApp(), "sendLocation");
                        SharedUtil.put(MainApp.getMainApp(), "sendLocation", date + address);
                    }
                });
    }
}
