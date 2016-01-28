package com.loyo.oa.v2.tool;

import android.util.Log;

import java.util.Hashtable;

public class LogUtil {
    public static boolean isLogFlag() {
        return Config_project.is_developer_mode;
    }

    public static String tag = "LogoServerV2";
    public static boolean LogoStatus = Config_project.is_developer_mode;

    private static int logLevel = Log.DEBUG;//Log.ERROR
    private static Hashtable<String, LogUtil> sLoggerTable = new Hashtable<String, LogUtil>();
    private String mClassName;

    private static LogUtil llog;

    private LogUtil(String name) {
        mClassName = name;
        if (!Config_project.is_developer_mode) {
            logLevel = Log.ERROR;
        }
    }

    /** 用于 输出 日志 方便
     *xnq
     * @param text
     */
    public static void d(String text) {
        if (LogoStatus) {
            Log.d("LogoServerV2", text);
        }
    }

    public static void d(String TAG,String text) {
        if (LogoStatus) {
            Log.d(TAG, text);
        }
    }

    /**
     * @param className
     * @return
     */
    @SuppressWarnings("unused")
    private static LogUtil getLogger(String className) {
        LogUtil classLogger = (LogUtil) sLoggerTable.get(className);
        if (classLogger == null) {
            classLogger = new LogUtil(className);
            sLoggerTable.put(className, classLogger);
        }
        return classLogger;
    }


    /**
     * Purpose:Mark user two
     *
     * @return
     */
    public static LogUtil lLog() {
        if (llog == null) {
            llog = new LogUtil("@x n q@ ");
        }
        return llog;
    }

    /**
     * Get The Current Function Name
     *
     * @return
     */
    private String getFunctionName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts == null) {
            return null;
        }
        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) {
                continue;
            }
            if (st.getClassName().equals(Thread.class.getName())) {
                continue;
            }
            if (st.getClassName().equals(this.getClass().getName())) {
                continue;
            }
            tag = st.getFileName();
            return mClassName + "[ " + Thread.currentThread().getName() + ": "
                    + st.getFileName() + ":" + st.getLineNumber() + " "
                    + st.getMethodName() + " ]";
        }
        return null;
    }

    /**
     * The Log Level:i
     *
     * @param str
     */
    public void i(Object str) {
        if (isLogFlag()) {
            if (logLevel <= Log.INFO) {
                String name = getFunctionName();
                if (name != null) {
                    Log.i(tag + "", name + " - " + str);
                } else {
                    Log.i(tag, str.toString());
                }
            }
        }

    }

    /**
     * The Log Level:d
     *
     * @param str
     */
    public void d(Object str) {
        if (isLogFlag()) {
            if (logLevel <= Log.DEBUG) {
                String name = getFunctionName();
                if (name != null) {
                    Log.d(tag, name + " - " + str);
                } else {
                    Log.d(tag, str.toString());
                }
            }
        }
    }

    /**
     * The Log Level:V
     *
     * @param str
     */
    public void v(Object str) {
        if (isLogFlag()) {
            if (logLevel <= Log.VERBOSE) {
                String name = getFunctionName();
                if (name != null) {
                    Log.v(tag, name + " - " + str);
                } else {
                    Log.v(tag, str.toString());
                }
            }
        }
    }

    /**
     * The Log Level:w
     *
     * @param str
     */
    public void w(Object str) {
        if (isLogFlag()) {
            if (logLevel <= Log.WARN) {
                String name = getFunctionName();
                if (name != null) {
                    Log.w(tag, name + " - " + str);
                } else {
                    Log.w(tag, str.toString());
                }
            }
        }
    }

    /**
     * The Log Level:e
     *
     * @param str
     */
    public void e(Object str) {
        if (isLogFlag()) {
            if (logLevel <= Log.ERROR) {
                String name = getFunctionName();
                if (name != null) {
                    Log.e(tag, name + " - " + str);
                } else {
                    Log.e(tag, str.toString());
                }
            }
        }
    }

    /**
     * The Log Level:e
     *
     * @param ex
     */
    public void e(Exception ex) {
        if (isLogFlag()) {
            if (logLevel <= Log.ERROR) {
                Log.e(tag, "error", ex);
            }
        }
    }

    /**
     * The Log Level:e
     *
     * @param log
     * @param tr
     */
    public void e(String log, Throwable tr) {
        if (isLogFlag()) {
            String line = getFunctionName();
            Log.e(tag, "{Thread:" + Thread.currentThread().getName() + "}"
                    + "[" + mClassName + line + ":] " + log + "\n", tr);
        }
    }

    public static void dll(String str) {
        Log.d("LOG", str);
    }

    public static void dee(String str) { Log.d("TOP", str); }
}