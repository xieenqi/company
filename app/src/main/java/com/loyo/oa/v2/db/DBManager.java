package com.loyo.oa.v2.db;

import android.content.Context;

public class DBManager {

    /**
     * 全局对象
     */
    private static DBHelper mDatabaseHelper;
    private static Context context;

    private DBManager() {
    }

    /**
     * @param context application context
     */
    public static void init(Context context) {
        DBManager.context = context;
        mDatabaseHelper = new DBHelper(context);
    }

    public static DBHelper Instance() {
        if (mDatabaseHelper == null) {
            mDatabaseHelper = new DBHelper(context);
        }
        return mDatabaseHelper;
    }

}
