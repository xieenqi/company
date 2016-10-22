package com.loyo.oa.v2.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import com.loyo.oa.v2.tool.LogUtil;


/**
 * 【电话呼入】监听
 * Created by yyy on 16/9/20.
 */
public class BroadcastReceiverMgr extends BroadcastReceiver {

    public final static String B_PHONE_STATE = TelephonyManager.ACTION_PHONE_STATE_CHANGED;
    public onCallInBack callInBack;

    public interface onCallInBack{
        void waitingCall(String num);
        void onlineCall(String num);
        void cancelCall(String num);
    }

    public BroadcastReceiverMgr(onCallInBack callInBack){
         this.callInBack = callInBack;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        //呼入电话
        if (action.equals(B_PHONE_STATE)) {
            doReceivePhone(context, intent);
        }
    }

    /**
     * 处理电话广播.
     *
     * @param context
     * @param intent
     */
    public void doReceivePhone(Context context, Intent intent) {
        String phoneNumber = intent.getStringExtra(
                TelephonyManager.EXTRA_INCOMING_NUMBER);
        TelephonyManager telephony =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int state = telephony.getCallState();
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                callInBack.waitingCall(phoneNumber);
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                callInBack.cancelCall(phoneNumber);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                callInBack.onlineCall(phoneNumber);

                break;
        }
    }
}
