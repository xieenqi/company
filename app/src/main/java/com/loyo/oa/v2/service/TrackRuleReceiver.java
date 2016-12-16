package com.loyo.oa.v2.service;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.v2.beans.TrackRule;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.tool.SharedUtil;
import com.loyo.oa.v2.tool.StringUtil;

import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.format.DateTimeFormat;


/**这个Recevier 主要是用于手机重启后，复活轨迹使用。
 */
public class TrackRuleReceiver extends BroadcastReceiver {

    final String format = "yyyy-MM-dd HH:mm:ss";

    @Override
    public void onReceive(Context context, Intent intent) {
        String strLastCheck = SharedUtil.get(context, FinalVariables.LAST_CHECK_TRACKLOG);

        boolean alarmUp = (PendingIntent.getBroadcast(context, TrackRule.RequestCode,
                new Intent(TrackRule.RequestAction),
                PendingIntent.FLAG_NO_CREATE) != null);

//        int hours = 0;
//
//        if (!StringUtil.isEmpty(strLastCheck)) {
////            hours = Hours.hoursBetween(DateTime.parse(strLastCheck, DateTimeFormat.forPattern(format)), DateTime.now()).getHours();
//
//            hours= (int) (System.currentTimeMillis() - DateTool.getSecondStamp(strLastCheck)/(3600000));//用时间差除以一个小时的时差
//        }

        //3个小时重新获取TrackRule
        if (!alarmUp || StringUtil.isEmpty(strLastCheck) ||
                (System.currentTimeMillis() - DateTool.getSecondStamp(strLastCheck)>(3*3600000))) {//时差大于3小时
            TrackRule.StartTrackRule(30*1000);
        }
    }
}