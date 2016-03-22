package com.loyo.oa.v2.tool;

import android.content.Context;
import android.location.LocationManager;

import com.amap.api.location.AMapLocation;
import com.loyo.oa.v2.beans.CellInfo;
import com.umeng.analytics.MobclickAgent;

/**
 * 友盟统计相关方法
 * Created by xeq on 16/3/22.
 */
public class UMengTools {
    /**
     * 自定义统计error信息
     */
    public static void sendCustomErroInfo(Context context, AMapLocation location) {
        if (!"success".equals(location.getErrorInfo())) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            CellInfo cellInfo = Utils.getCellInfo();
            StringBuffer erroInfo = new StringBuffer();
            erroInfo.append("品牌：" + cellInfo.getLoyoAgent() + "<->");
            erroInfo.append("版本：" + cellInfo.getLoyoOSVersion() + "<->");
            erroInfo.append("设备硬件版本:" + cellInfo.getLoyoHVersion() + "<->");
            erroInfo.append("GPS开启状态:" + gps + "<->");
            erroInfo.append("网络状态（非数据）:" + network + "<->");
            erroInfo.append("定位结果:" + location.getErrorInfo() + "--" + location.getLocationDetail() + "<->");
            erroInfo.append("网络类型:" + Utils.getNetworkType(context) + "<->");
            MobclickAgent.reportError(context, erroInfo.toString());
            LogUtil.d("高德定位设备友盟统计信息：" + erroInfo.toString());
        }
    }


}
