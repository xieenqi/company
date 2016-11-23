package com.loyo.oa.common.patch;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yzxtcp.service.YzxIMCoreService;
import com.yzxtcp.tools.tcp.receiver.MsgBackReceiver;
import com.yzxtcp.tools.u;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }

    public void onReceive(Context var1, Intent var2) {
        u.b("SEND PING...");
        if(YzxIMCoreService.getInstance() != null && (var1 = YzxIMCoreService.getInstance().getApplicationContext()) != null) {
            if (var1 == null) {
                return ;
            }

            var2 = new Intent(var1, MsgBackReceiver.class);
            PendingIntent var3 = PendingIntent.getBroadcast(var1, 0, var2, PendingIntent.FLAG_UPDATE_CURRENT);
            ((AlarmManager)var1.getSystemService(Context.ALARM_SERVICE)).set(0, System.currentTimeMillis() + 20000L, var3);
        }
        (new PatchA()).onSendMessage();
    }
}
