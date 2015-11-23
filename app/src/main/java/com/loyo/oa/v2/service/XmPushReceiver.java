package com.loyo.oa.v2.service;

import android.content.Context;
import android.util.Log;

import com.loyo.oa.v2.beans.TrackRule;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

/**
 * com.loyo.oa.v2.service
 * 描述 :小米推送广播接收器
 * 作者 : ykb
 * 时间 : 15/8/7.
 */
public class XmPushReceiver extends PushMessageReceiver {
    private final String TAG = getClass().getSimpleName();

    /**
     * 启动轨迹上报
     */
    private synchronized void startTrackRule() {
        TrackRule.StartTrackRule(30 * 1000);
    }


    //    服务器发送的透传消息
    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        Log.e(TAG, "小米 onReceivePassThroughMessage");
        startTrackRule();
    }

    //服务器发来的通知栏消息（用户点击通知栏时触发）
    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        Log.e(TAG, "小米 onNotificationMessageClicked");
    }

    //服务器发来的通知栏消息（消息到达客户端时触发，并且可以接收应用在前台时不弹出通知的通知消息）
    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        Log.e(TAG, "小米 onNotificationMessageArrived");
        startTrackRule();
    }

    //客户端向服务器发送命令消息后返回的响应
    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        Log.e(TAG, "小米 onCommandResult");
    }

    //客户端向服务器发送注册命令消息后返回的响应
    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        Log.e(TAG, "小米 onReceiveRegisterResult");
        startTrackRule();
    }
}
