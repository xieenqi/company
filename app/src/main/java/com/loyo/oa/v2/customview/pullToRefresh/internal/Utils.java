package com.loyo.oa.v2.customview.pullToRefresh.internal;

import android.util.Log;

public class Utils {
    protected Utils() {
        throw new UnsupportedOperationException();
    }

    static final String LOG_TAG = "PullToRefresh";

    public static void warnDeprecation(String depreacted, String replacement) {
        Log.w(LOG_TAG, "You're using the deprecated " + depreacted + " attr, please switch over to " + replacement);
    }

}
