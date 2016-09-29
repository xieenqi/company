package com.loyo.oa.v2.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.loyo.oa.v2.tool.TrackLocationManager;

/**
 * Created by EthanGong on 16/9/29.
 */

public class TrackLocationScheduleReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        TrackLocationManager.getInstance().startLocationTrackingIfNeeded();
    }
}
