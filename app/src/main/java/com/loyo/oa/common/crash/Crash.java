package com.loyo.oa.common.crash;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by EthanGong on 2017/1/4.
 */

public class Crash {

    private static final int MAX_STACK_TRACE_SIZE = 131071; //128 KB - 1
    public String stackTrace;
    public boolean isBackground;

    private Crash(Throwable throwable, boolean isBackground) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        this.isBackground = isBackground;
        this.stackTrace = "";
        String stackTraceString = sw.toString();
        if (stackTraceString.length() > MAX_STACK_TRACE_SIZE) {
            String disclaimer = " [stack trace too large]";
            stackTraceString = stackTraceString.substring(0,
                    MAX_STACK_TRACE_SIZE - disclaimer.length()) + disclaimer;
            this.stackTrace = stackTraceString;
        }
        else {
            this.stackTrace = stackTraceString;
        }

    }

    private Crash(String stackTrace, boolean isBackground) {
        this.isBackground = isBackground;
        this.stackTrace = stackTrace;


    }

    public static Crash getLatestCrash(Context context) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        boolean hasCrash = preferences.getBoolean("CRASH_INDICATOR", false);
        boolean isBackground = preferences.getBoolean("IS_BACKGROUND", false);
        String stackTrace = preferences.getString("STACK_TRACE", "");
        if (hasCrash) {
            return new Crash(stackTrace, isBackground);
        }
        else {
            return null;
        }
    }

    public static void clearLatestCrash(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("CRASH_INDICATOR", false);
        editor.commit();
    }

    public static void saveCrash(Context context, Throwable e, boolean isBackground) {

        Crash crash = new Crash(e, isBackground);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("IS_BACKGROUND", isBackground);
        editor.putString("STACK_TRACE", crash.stackTrace);
        editor.putBoolean("CRASH_INDICATOR", true);
        editor.commit();
    }

}
