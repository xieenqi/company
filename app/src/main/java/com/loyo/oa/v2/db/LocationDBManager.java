package com.loyo.oa.v2.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.DateTool;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by EthanGong on 16/9/28.
 */

public class LocationDBManager {

    private MainApp mApp;
    private LocationDBHelper mDBHealper;

    private static LocationDBManager ourInstance = new LocationDBManager();

    public static LocationDBManager getInstance() {
        return ourInstance;
    }

    private LocationDBManager() {
        mApp = MainApp.getMainApp();
        mDBHealper = new LocationDBHelper();
    }


    public void addLocation(LocationEntity entity) {
        SQLiteDatabase database = mDBHealper.getWritableDatabase();

        //
        try {
            ContentValues values = new ContentValues();
            values.put(LocationEntity.COLUMN_TIMESTAMP, entity.timestamp);
            values.put(LocationEntity.COLUMN__ACCURACY, entity.accuracy);
            values.put(LocationEntity.COLUMN_PROVIDER,  entity.provider);
            values.put(LocationEntity.COLUMN_LATITUDE,  entity.latitude);
            values.put(LocationEntity.COLUMN_LONGITUDE, entity.longitude);
            values.put(LocationEntity.COLUMN_ADDRESS,   entity.address);
            values.put(LocationEntity.COLUMN_UPLOAD_ID, entity.uploadID);
            long rowId = database.insert(LocationEntity.TABLE_NAME, null, values);
        } catch (Exception e) {
            Global.ProcException(e);
        }

        database.close();
    }

    public synchronized List<LocationEntity> getLocations() {
        List<LocationEntity> locateDatas = new ArrayList<>();
        try {
            String orderby = LocationEntity.COLUMN_TIMESTAMP + " asc ";
            Cursor cursor = mDBHealper.getReadableDatabase()
                    .query(LocationEntity.TABLE_NAME, null, null, null, null, null, orderby);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    long t = cursor.getLong(cursor.getColumnIndex(LocationEntity.COLUMN_TIMESTAMP));
                    double a = cursor.getDouble(cursor.getColumnIndex(LocationEntity.COLUMN__ACCURACY));
                    String p = cursor.getString(cursor.getColumnIndex(LocationEntity.COLUMN_PROVIDER));
                    double la = cursor.getDouble(cursor.getColumnIndex(LocationEntity.COLUMN_LATITUDE));
                    double lo = cursor.getDouble(cursor.getColumnIndex(LocationEntity.COLUMN_LONGITUDE));
                    String add = cursor.getString(cursor.getColumnIndex(LocationEntity.COLUMN_ADDRESS));
                    String id = cursor.getString(cursor.getColumnIndex(LocationEntity.COLUMN_UPLOAD_ID));

                    Log.v("debug", DateTool.getDiffTime(t) + "`         " + t
                            + "  " +a
                            + "  " +p
                            + "  " +la
                            + "  " +lo
                            + "  " +add
                            + "id=  " +id);

                    LocationEntity entity = new LocationEntity(t, la, lo, a, p, add);
                    locateDatas.add(entity);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Global.ProcException(e);
        }
        return locateDatas;
    }
    public synchronized List<LocationEntity> getFiveOldestLocations() {
        List<LocationEntity> locateDatas = new ArrayList<>();
        try {
            String orderby = " ORDER BY " + LocationEntity.COLUMN_TIMESTAMP + " asc ";
            String limit = "LIMIT 5";

            String query = "select * from "
                    +LocationEntity.TABLE_NAME
                    + " where "
                    + LocationEntity.COLUMN_UPLOAD_ID
                    +"=? "
                    + orderby
                    + limit;

            Cursor cursor = mDBHealper.getReadableDatabase()
                    .rawQuery(query, new String[]{""});

            Log.v("debug", "--------------------------------------");
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    long t = cursor.getLong(cursor.getColumnIndex(LocationEntity.COLUMN_TIMESTAMP));
                    double a = cursor.getDouble(cursor.getColumnIndex(LocationEntity.COLUMN__ACCURACY));
                    String p = cursor.getString(cursor.getColumnIndex(LocationEntity.COLUMN_PROVIDER));
                    double la = cursor.getDouble(cursor.getColumnIndex(LocationEntity.COLUMN_LATITUDE));
                    double lo = cursor.getDouble(cursor.getColumnIndex(LocationEntity.COLUMN_LONGITUDE));
                    String add = cursor.getString(cursor.getColumnIndex(LocationEntity.COLUMN_ADDRESS));
                    String id = cursor.getString(cursor.getColumnIndex(LocationEntity.COLUMN_UPLOAD_ID));

                    Log.v("debug", t
                            + "  " +a
                            + "  " +p
                            + "  " +la
                            + "  " +lo
                            + "  " +add
                            + " id = " +id);

                    LocationEntity entity = new LocationEntity(t, la, lo, a, p, add);
                    locateDatas.add(entity);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Global.ProcException(e);
        }
        return locateDatas;
    }

    public synchronized List<LocationEntity> getOldestLocationsByLimit(int limitCount) {
        List<LocationEntity> locateDatas = new ArrayList<>();
        try {
            String orderby = " ORDER BY " + LocationEntity.COLUMN_TIMESTAMP + " asc ";
            String limit = "LIMIT " + limitCount;

            String query = "select * from "
                    +LocationEntity.TABLE_NAME
                    + " where "
                    + LocationEntity.COLUMN_UPLOAD_ID
                    +"=? "
                    + orderby
                    + limit;

            Cursor cursor = mDBHealper.getReadableDatabase()
                    .rawQuery(query, new String[]{""});

            Log.v("debug", "--------------------------------------");
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    long t = cursor.getLong(cursor.getColumnIndex(LocationEntity.COLUMN_TIMESTAMP));
                    double a = cursor.getDouble(cursor.getColumnIndex(LocationEntity.COLUMN__ACCURACY));
                    String p = cursor.getString(cursor.getColumnIndex(LocationEntity.COLUMN_PROVIDER));
                    double la = cursor.getDouble(cursor.getColumnIndex(LocationEntity.COLUMN_LATITUDE));
                    double lo = cursor.getDouble(cursor.getColumnIndex(LocationEntity.COLUMN_LONGITUDE));
                    String add = cursor.getString(cursor.getColumnIndex(LocationEntity.COLUMN_ADDRESS));
                    String id = cursor.getString(cursor.getColumnIndex(LocationEntity.COLUMN_UPLOAD_ID));

                    Log.v("debug", t
                            + "  " +a
                            + "  " +p
                            + "  " +la
                            + "  " +lo
                            + "  " +add
                            + " id = " +id);

                    LocationEntity entity = new LocationEntity(t, la, lo, a, p, add);
                    locateDatas.add(entity);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Global.ProcException(e);
        }
        return locateDatas;
    }

    public synchronized boolean markAsUploadingWithID(List<LocationEntity> list, String identifier) {

        try {
            SQLiteDatabase db = mDBHealper.getWritableDatabase();
            db.beginTransaction();

            for (int i = 0; i <list.size(); i++) {
                LocationEntity entity = list.get(i);
                ContentValues values = new ContentValues();
                values.put(LocationEntity.COLUMN_UPLOAD_ID, identifier);
                int row= db.update(LocationEntity.TABLE_NAME, values, LocationEntity.COLUMN_TIMESTAMP+ "=?", new String[]{String.valueOf(entity.timestamp)});

                Log.v("debug", "--- "+ row);
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        }
        catch (Exception e) {
            return false;
        }

        return true;

    }

    public synchronized List<LocationEntity> getUploadingLocationsByID(String identifier) {
        List<LocationEntity> locateDatas = new ArrayList<>();
        try {
            String orderby = " ORDER BY " + LocationEntity.COLUMN_TIMESTAMP + " asc ";
            String limit = "LIMIT 5";

            String query = "select * from "
                    +LocationEntity.TABLE_NAME
                    + " where "
                    + LocationEntity.COLUMN_UPLOAD_ID
                    +"=? "
                    + orderby;

            Cursor cursor = mDBHealper.getReadableDatabase()
                    .rawQuery(query, new String[]{identifier});

            Log.v("debug", "--------------------------------------");
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    long t = cursor.getLong(cursor.getColumnIndex(LocationEntity.COLUMN_TIMESTAMP));
                    double a = cursor.getDouble(cursor.getColumnIndex(LocationEntity.COLUMN__ACCURACY));
                    String p = cursor.getString(cursor.getColumnIndex(LocationEntity.COLUMN_PROVIDER));
                    double la = cursor.getDouble(cursor.getColumnIndex(LocationEntity.COLUMN_LATITUDE));
                    double lo = cursor.getDouble(cursor.getColumnIndex(LocationEntity.COLUMN_LONGITUDE));
                    String add = cursor.getString(cursor.getColumnIndex(LocationEntity.COLUMN_ADDRESS));
                    String id = cursor.getString(cursor.getColumnIndex(LocationEntity.COLUMN_UPLOAD_ID));

                    Log.v("debug", t
                            + "  " +a
                            + "  " +p
                            + "  " +la
                            + "  " +lo
                            + "  " +add
                            + " id = " +id);

                    LocationEntity entity = new LocationEntity(t, la, lo, a, p, add);
                    locateDatas.add(entity);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Global.ProcException(e);
        }
        return locateDatas;
    }

    public synchronized void count() {
        SQLiteDatabase db = mDBHealper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*)from "+LocationEntity.TABLE_NAME,null);
        cursor.moveToFirst();
        Long count = cursor.getLong(0);
        Log.v("debug", "count = " + count);
    }

    public synchronized boolean clearAllUploadingFlag() {

        try {
            SQLiteDatabase db = mDBHealper.getWritableDatabase();
            db.beginTransaction();

            ContentValues values = new ContentValues();
            values.put(LocationEntity.COLUMN_UPLOAD_ID, "");
            int row= db.update(LocationEntity.TABLE_NAME, values, LocationEntity.COLUMN_UPLOAD_ID+ "!=?", new String[]{""});

            Log.v("debug", "clear --- "+ row);

            db.setTransactionSuccessful();
            db.endTransaction();
        }
        catch (Exception e) {
            return false;
        }

        return true;
    }

    public synchronized boolean deleteUploadingLocationsWithID(String identifer) {

        try {
            SQLiteDatabase db = mDBHealper.getWritableDatabase();
            db.beginTransaction();

            int row= db.delete(LocationEntity.TABLE_NAME, LocationEntity.COLUMN_UPLOAD_ID+ "=?", new String[]{identifer});

            Log.v("debug", "clear --- "+ row);

            db.setTransactionSuccessful();
            db.endTransaction();
        }
        catch (Exception e) {
            return false;
        }

        return true;
    }

    public static class LocationEntity  implements Serializable {
        public static final String
                TABLE_NAME        = "locations",  /* 表名 */
                COLUMN_TIMESTAMP  = "timestamp",  /* 定位时间 */
                COLUMN_LATITUDE   = "latitude",   /* 定位纬度 */
                COLUMN_LONGITUDE  = "longitude",  /* 定位经度 */
                COLUMN__ACCURACY  = "accuracy",   /* 精度 */
                COLUMN_PROVIDER   = "provider",   /* 提供模式(gps、lbs、network） */
                COLUMN_ADDRESS    = "address",    /* 地址 */
                COLUMN_UPLOAD_ID  = "uploadID";   /* uploadID, 标示正在上传中的位置 */

        public  long timestamp;
        private double latitude;
        private double longitude;
        private double accuracy;
        private String provider;
        private String address;
        public  String uploadID;

        public LocationEntity(long timestamp,
                              double latitude,
                              double longitude,
                              double accuracy,
                              String provider,
                              String address)
        {
            this.timestamp = timestamp;
            this.latitude = latitude;
            this.longitude = longitude;
            this.accuracy = accuracy;
            this.provider = provider;
            this.address = address;
            this.uploadID = "";
        }
    }


    public  class LocationDBHelper extends SQLiteOpenHelper {

        private static final int BASE_DB_VERSION = 1;
        private static final String BASE_DB_NAME = "track_location_cache";

        public LocationDBHelper() {
            super(MainApp.getMainApp(), BASE_DB_NAME, null, BASE_DB_VERSION);
        }

        public LocationDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        public LocationDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
            super(context, name, factory, version, errorHandler);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String create = " create table if not exists ";
            String left = " ( ";
            String right = " ) ";
            String locateSql = create + LocationEntity.TABLE_NAME
                    + left
                    + " id INTEGER PRIMARY KEY AUTOINCREMENT , "
                    + LocationEntity.COLUMN_TIMESTAMP + " INT8 , "
                    + LocationEntity.COLUMN__ACCURACY + " double , "
                    + LocationEntity.COLUMN_PROVIDER + " varchar(50) , "
                    + LocationEntity.COLUMN_LATITUDE + " double , "
                    + LocationEntity.COLUMN_LONGITUDE + " double , "
                    + LocationEntity.COLUMN_ADDRESS + " varchar(100), "
                    + LocationEntity.COLUMN_UPLOAD_ID + " varchar(100)"
                    + right;

            db.execSQL(locateSql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + LocationEntity.TABLE_NAME);
            onCreate(db);
        }
    }

}
