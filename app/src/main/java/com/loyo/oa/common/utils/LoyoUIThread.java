package com.loyo.oa.common.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by EthanGong on 2016/12/28.
 */

public class LoyoUIThread {
    private static Handler mainHandler = new Handler(Looper.getMainLooper());

    public static void run(Runnable runnable) {
        mainHandler.post(runnable);
    }

    public static void runAfterDelay(Runnable runnable, int milli) {
        mainHandler.postDelayed(runnable, milli);
    }
}
