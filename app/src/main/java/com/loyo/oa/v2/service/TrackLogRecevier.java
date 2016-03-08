package com.loyo.oa.v2.service;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.TrackRule;
import com.loyo.oa.v2.tool.LogUtil;

public class TrackLogRecevier extends BroadcastReceiver {
    TrackRule trackRule;
    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            mContext = context;
            if (intent.hasExtra("track")) {
                LogUtil.d("收到, hasExtra");
                trackRule = (TrackRule) intent.getSerializableExtra("track");
                startAMapService(trackRule);
                TrackRule.StartTrackRule(trackRule, MainApp.getMainApp());
            }
        }
    }


    /**
     * 开启定位服务startTrackRule();
     *
     * @param trackRule
     */
    void startAMapService(TrackRule trackRule) {
        Intent serIntent = new Intent(mContext, AMapService.class);
        serIntent.putExtra("track", trackRule);
        mContext.startService(serIntent);
    }
}
