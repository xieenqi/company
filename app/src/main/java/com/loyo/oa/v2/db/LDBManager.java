package com.loyo.oa.v2.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.LocateData;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.DateTool;
import com.nostra13.universalimageloader.utils.L;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * com.loyo.oa.v2.db
 * 描述 :轨迹缓存数据库
 * 作者 : ykb
 * 时间 : 15/8/21.
 */
public class LDBManager {
    private static final int BASE_DB_VERSION = 1;
    private static final String BASE_DB_NAME = "loyo_locate_db";
    /**
     * 位置时间key
     */
    private static final String BASE_RECORD_TIME_KEY = "recordtime";
    /**
     * 位置日期时间key
     */
    private static final String BASE_RECORD_DATE_KEY = "recordtime_str";
    /**
     * 位置信息存放表
     */
    private static final String BASE_LOCATE_TABLE_NAME = "locate";
    /**
     * 纬度
     */
    private static final String BASE_LOCATE_LATITUDE_KEY = "latitude";
    /**
     * 经度
     */
    private static final String BASE_LOCATE_LONGITUDE_KEY = "longitude";
    /**
     * 精度
     */
    private static final String BASE_LOCATE_ACCURACY_KEY = "accuracy";
    /**
     * 提供模式(gps、lbs、network）
     */
    private static final String BASE_LOCATE_PROVIDER_KEY = "provider";
    /**
     * 地址
     */
    private static final String BASE_LOCATE_ADDRESS_KEY = "address";

    private MainApp app;
    private BaseDbHelper baseDbHealper;

    public LDBManager() {
        app = MainApp.getMainApp();
        baseDbHealper = new BaseDbHelper();
    }


    // ------------------Locate--------------------

    /**
     * 插入位置数据
     *
     * @param datas 位置数据集合
     */
    public void addLocateData(List<LocateData> datas, SQLiteDatabase database) {
        for (LocateData locateData : datas) {
            addLocateData(locateData, database);
        }
        database.close();
    }

    /**
     * 插入位置数据
     *
     * @param datas 位置数据集合
     */
    public void addLocateData(List<LocateData> datas) {
        SQLiteDatabase database = baseDbHealper.getWritableDatabase();
        for (LocateData locateData : datas) {
            addLocateData(locateData, database);
        }
        database.close();
    }

    /**
     * 插入位置数据
     *
     * @param data 位置数据
     */
    public void addLocateData(LocateData data) {
        SQLiteDatabase database = baseDbHealper.getWritableDatabase();
        addLocateData(data, database);
        database.close();
    }

    /**
     * 插入位置数据
     *
     * @param data     位置数据
     * @param database 数据库
     */
    private synchronized void addLocateData(LocateData data, SQLiteDatabase database) {
        try {
            ContentValues values = new ContentValues();
            values.put(BASE_RECORD_TIME_KEY, data.getRecordTime());
            values.put(BASE_RECORD_DATE_KEY, app.df_api_get2.format(new Date(data.getRecordTime())));
            values.put(BASE_LOCATE_ACCURACY_KEY, data.getAccuracy());
            values.put(BASE_LOCATE_PROVIDER_KEY, data.getProvider());
            values.put(BASE_LOCATE_LATITUDE_KEY, data.getLat());
            values.put(BASE_LOCATE_LONGITUDE_KEY, data.getLng());
            values.put(BASE_LOCATE_ADDRESS_KEY, data.getAddress());
            long rowId = database.insert(BASE_LOCATE_TABLE_NAME, null, values);
            app.logUtil.e("addLocateData rowId : " + rowId);
        } catch (Exception e) {
            Global.ProcException(e);
        }
    }

    /**
     * 清除超出中国地区地区内的位置数据
     */
    public synchronized void deleteInvalidLocateData() {
        String whereClause = BASE_LOCATE_LATITUDE_KEY + "<? or " + BASE_LOCATE_LONGITUDE_KEY + "<? or " + BASE_LOCATE_LATITUDE_KEY + ">? or " + BASE_LOCATE_LONGITUDE_KEY + ">? ";
        String whereArgs[] = new String[]{Double.toString(3), Double.toString(73), Double.toString(54), Double.toString(136)};
        SQLiteDatabase database = baseDbHealper.getWritableDatabase();
        long effectRows = database.delete(BASE_LOCATE_TABLE_NAME, whereClause, whereArgs);
        database.close();
        app.logUtil.e("deleteInvalidLocateData effectRows : " + effectRows);
    }


    /**
     * 获取所有位置信息
     *
     * @return
     */
    public List<LocateData> getAllLocateDatas() {
        return getLocateData(DateTool.getCurrentMoringMillis(), DateTool.getNextMoringMillis(), null);
    }

    /**
     * 清除所有缓存数据
     */
    public void clearAllLocateDatas() {
        try {
            SQLiteDatabase database = baseDbHealper.getWritableDatabase();
            long effectRows = database.delete(BASE_LOCATE_TABLE_NAME, null, null);
            database.close();
            app.logUtil.e("clearAllLocateDatas effectRows : " + effectRows);
        } catch (Exception e) {
            Global.ProcException(e);
        }
    }

    /**
     * 获取一段时间内的轨迹数据
     *
     * @param startTime 起始时间
     * @param endTime   截止时间
     * @return
     */
    public List<LocateData> getLocateData(long startTime, long endTime, String orderby) {
        SQLiteDatabase database = baseDbHealper.getReadableDatabase();
        List<LocateData> locateDatas = getLocateData(startTime, endTime, database, orderby);
        database.close();

        return locateDatas;
    }

    /**
     * 获取一段时间内的轨迹数据
     *
     * @param startTime 起始时间(对应某段时间段内能量数据的第一个的起始时间)
     * @param endTime   截止时间(对应某段时间段内能量数据的最后一个的起始时间)
     * @param orderby   排序
     * @return
     */
    private synchronized List<LocateData> getLocateData(long startTime, long endTime, SQLiteDatabase database, String orderby) {
        List<LocateData> locateDatas = new ArrayList<>();
        try {
            String selection = BASE_RECORD_TIME_KEY + ">=?" + " and " + BASE_RECORD_TIME_KEY + "<=?";
            String[] selectionArgs = new String[]{Long.toString(startTime), Long.toString(endTime)};
            if (TextUtils.isEmpty(orderby)) {
                orderby = BASE_RECORD_TIME_KEY + " desc ";
            }
            Cursor cursor = database.query(BASE_LOCATE_TABLE_NAME, null, selection, selectionArgs, null, null, orderby);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    long recordTime = cursor.getLong(cursor.getColumnIndex(BASE_RECORD_TIME_KEY));
                    String recordDate = cursor.getString(cursor.getColumnIndex(BASE_RECORD_DATE_KEY));
                    double accuracy = cursor.getDouble(cursor.getColumnIndex(BASE_LOCATE_ACCURACY_KEY));
                    String provider = cursor.getString(cursor.getColumnIndex(BASE_LOCATE_PROVIDER_KEY));
                    double lat = cursor.getDouble(cursor.getColumnIndex(BASE_LOCATE_LATITUDE_KEY));
                    double lng = cursor.getDouble(cursor.getColumnIndex(BASE_LOCATE_LONGITUDE_KEY));
                    String address = cursor.getString(cursor.getColumnIndex(BASE_LOCATE_ADDRESS_KEY));
                    LocateData locateData = new LocateData(recordTime, lat, lng);
                    locateData.setAddress(address);
                    locateData.setAccuracy(accuracy);
                    locateData.setRecordDate(recordDate);
                    locateData.setProvider(provider);
                    locateDatas.add(locateData);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Global.ProcException(e);
        }
        return locateDatas;
    }


    private static class BaseDbHelper extends SQLiteOpenHelper {

        public BaseDbHelper() {
            super(MainApp.getMainApp(), BASE_DB_NAME, null, BASE_DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String create = " create table if not exists ";
            String left = " ( ";
            String right = " ) ";
            // 位置表，自增id、每次位置时间、位置纬度、位置经度
            String locateSql = create + BASE_LOCATE_TABLE_NAME + left + " id INTEGER PRIMARY KEY AUTOINCREMENT , " + BASE_RECORD_TIME_KEY + " double , " + BASE_RECORD_DATE_KEY + " varchar(100) , " + BASE_LOCATE_ACCURACY_KEY + " double , " + BASE_LOCATE_PROVIDER_KEY + " varchar(50) , " + BASE_LOCATE_LATITUDE_KEY + " double , " + BASE_LOCATE_LONGITUDE_KEY + " double , " + BASE_LOCATE_ADDRESS_KEY + " varchar(100)" + right;

            db.execSQL(locateSql);
            L.e(locateSql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + BASE_LOCATE_TABLE_NAME);
            onCreate(db);
        }

    }
}
