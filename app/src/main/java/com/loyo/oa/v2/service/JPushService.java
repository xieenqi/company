package com.loyo.oa.v2.service;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.loyo.oa.v2.activityui.home.MainHomeActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.TrackRule;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.jpush.HttpJpushNotification;
import com.loyo.oa.v2.tool.ExitActivity;
import com.loyo.oa.v2.tool.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * <p/>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class JPushService extends BroadcastReceiver {
    private static final String TAG = "JPush";
    NotificationManager manger = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        manger = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Bundle bundle = intent.getExtras();
        LogUtil.d("[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

        Common.getToken();//检查刷新token
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            LogUtil.d(" 激光推送RegistrationId： " + regId);
            //send the Registration Id to your server...

        }
        /*【自定义消息】 处理*/
        else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            TrackRule.InitTrackRule();//收到推送启动一次定位服务 避免服务kill掉
            Log.d(TAG, "[MyReceiver] 接收到推送下来的【自定义消息】: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            String msg = bundle.getString(JPushInterface.EXTRA_EXTRA);
            LogUtil.d("【自定义msg】键值数据： " + msg);
            HttpJpushNotification pushMsgData = MainApp.gson.fromJson(msg, HttpJpushNotification.class);

            if (8 == pushMsgData.silentType || 9 == pushMsgData.silentType) {//更新8组织架构与9个人信息
                if (!getUserInfo(pushMsgData))
                    pushMsgData.silentType = 8;//更改别人的信息制动转成 更新8组织架构
                LogUtil.d("更新数据激光推送：更新8组织架构与9个人信息 ");
                TrackRule.initUserData(MainApp.getMainApp());
            }
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
        }
        /*【用户点击打开了通知】 处理*/
        else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            /*收到推送启动一次定位服务 避免服务kill掉*/
            TrackRule.InitTrackRule();
            Log.d(TAG, "[MyReceiver] 用户点击打开了【通知】");
            String msg = bundle.getString(JPushInterface.EXTRA_EXTRA);
            LogUtil.d(" 激光推送传递过来的数据： " + msg);
            HttpJpushNotification pushData = MainApp.gson.fromJson(msg, HttpJpushNotification.class);
            LogUtil.d(" 键值数据： " + pushData);
            /* 打开自定义的Activity*/
            MainApp.jpushData = pushData;
            ExitActivity.getInstance().finishAllActivity();
            Intent in = new Intent();
            in.setClass(context, MainHomeActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(in);
            //清除所有通知
            if (manger != null)
                manger.cancelAll();
//            manger.cancel(bundle.getInt("cn.jpush.android.NOTIFICATION_ID"));

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else {
            Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    /**
     * 更改个人信息 区分是个人 还是别人【9】
     *
     * @param pushMsgData
     * @return
     */
    private boolean getUserInfo(HttpJpushNotification pushMsgData) {
        if (null == pushMsgData.buzzIds || !(pushMsgData.buzzIds.size() > 0)) {
            return false;
        }
        try {
            for (String ele : pushMsgData.buzzIds) {
                if (MainApp.user.id.equals(ele)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }


}