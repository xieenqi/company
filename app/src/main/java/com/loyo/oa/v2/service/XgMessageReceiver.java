package com.loyo.oa.v2.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.TasksInfoActivity_;
import com.loyo.oa.v2.activity.WfinstanceInfoActivity_;
import com.loyo.oa.v2.activity.WorkReportsInfoActivity_;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.StringUtil;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;


public class XgMessageReceiver extends XGPushBaseReceiver {

    static int NOTIFI_ID = 110;
    static int numMessages = 0;

    @Override
    public void onRegisterResult(Context context, int i, XGPushRegisterResult xgPushRegisterResult) {
        LogUtil.lLog().e("onRegisterResult");
    }

    @Override
    public void onUnregisterResult(Context context, int i) {
        LogUtil.lLog().e("onUnregisterResult");
    }

    @Override
    public void onSetTagResult(Context context, int i, String s) {
        LogUtil.lLog().e("onSetTagResult");
    }

    @Override
    public void onDeleteTagResult(Context context, int i, String s) {
        LogUtil.lLog().e("onDeleteTagResult");
    }

    @Override
    public void onTextMessage(Context context, XGPushTextMessage xgPushTextMessage) {
        //消息透传
        LogUtil.lLog().e("onTextMessage");

        if (StringUtil.isEmpty(xgPushTextMessage.getCustomContent())) return;

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(xgPushTextMessage.getTitle())
                .setContentText(xgPushTextMessage.getContent())
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true)
                .setNumber(numMessages++)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL);

        Intent intent = new Intent();

        MessageContent mc = MainApp.gson.fromJson(xgPushTextMessage.getCustomContent(), MessageContent.class);
        switch (mc.bizType) {
            case 1:     // 工作汇报
                intent.setClass(context, WorkReportsInfoActivity_.class);
                break;
            case 2:     // 任务
                intent.setClass(context, TasksInfoActivity_.class);
                break;
            case 12:    // 快捷审批
                intent.setClass(context, WfinstanceInfoActivity_.class);
                break;
            default:
                return;
        }

        intent.putExtra("id", mc.bizId);

        PendingIntent pIntent = PendingIntent.getActivity(context, (int) SystemClock.uptimeMillis(), intent, PendingIntent.FLAG_ONE_SHOT);
        notifyBuilder.setContentIntent(pIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFI_ID++, notifyBuilder.build());
    }

    @Override
    public void onNotifactionClickedResult(Context context, XGPushClickedResult xgPushClickedResult) {
        LogUtil.lLog().e("onNotifactionClickedResult");
    }

    @Override
    public void onNotifactionShowedResult(Context context, XGPushShowedResult xgPushShowedResult) {
        LogUtil.lLog().e("onNotifactionShowedResult");
        if (context == null || xgPushShowedResult == null) {
            return;
        }

        String t = "通知+" + xgPushShowedResult.getTitle();
        LogUtil.lLog().e(t);
    }

    class MessageContent {
        int bizType;
        int bizId;
    }

}
