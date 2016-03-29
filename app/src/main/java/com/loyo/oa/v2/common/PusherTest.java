package com.loyo.oa.v2.common;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import com.loyo.oa.v2.tool.LogUtil;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

/**
 * Created by xeq on 16/3/25.
 */
public class PusherTest {
    private PusherTest() {}

    public static void appTest() {
        PusherOptions options = new PusherOptions();
        options.setHost("192.168.31.131");
//        options.setCluster("xx-xx");
        options.setWsPort(8888);
        options.setWssPort(8888);
        options.setEncrypted(false);
        Pusher pusher = new Pusher("loyocloud_web", options);
        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                LogUtil.d(change.getPreviousState() + "pusher链接【改变】" + change.getCurrentState());
            }

            @Override
            public void onError(String message, String code, Exception e) {
                LogUtil.d("pusher链接【错误】" + message.toString() + "<-->" + code + "<--->" + e.toString());
            }
        }, ConnectionState.ALL);


        Channel channel = pusher.subscribe("myPusher");
        channel.bind("myEvent", new SubscriptionEventListener() {
            @Override
            public void onEvent(String channelName, String eventName, String data) {
                LogUtil.d("pusher链接【接受信息】" + data);
            }
        });
    }


    @SuppressLint("NewApi")
    public static boolean checkPermission(Context context, String permission) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        } else {
            PackageManager pm = context.getPackageManager();
            if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        }
        return result;
    }

    /**
     * 获取友盟测试集成设备信息
     *
     * @param context
     * @return
     */
    public static String getDeviceInfo(Context context) {
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String device_id = null;
            if (checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                device_id = tm.getDeviceId();
            }
            android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            String mac = wifi.getConnectionInfo().getMacAddress();
            json.put("mac", mac);
            if (TextUtils.isEmpty(device_id)) {
                device_id = mac;
            }
            if (TextUtils.isEmpty(device_id)) {
                device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID);
            }
            json.put("device_id", device_id);
            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
