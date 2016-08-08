package com.loyo.oa.v2.db;

/**
 * Created by EthanGong on 16/8/2.
 */

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import com.loyo.oa.v2.db.bean.*;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper{

    private static final String TABLE_NAME = "sqlite-test.db";
    private static final int DATABASE_VERSION = 1;
    private Map<String, Dao> daos = new HashMap<String, Dao>();

    private DatabaseHelper(Context context)
    {
        super(context, TABLE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database,
                         ConnectionSource connectionSource) {
        try
        {
            TableUtils.createTable(connectionSource, DBDepartment.class);
            TableUtils.createTable(connectionSource, DBPosition.class);
            TableUtils.createTable(connectionSource, DBRole.class);
            TableUtils.createTable(connectionSource, DBUser.class);
            TableUtils.createTable(connectionSource, DBUserNode.class);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase database,
                          ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try
        {
            TableUtils.dropTable(connectionSource, DBDepartment.class, true);
            TableUtils.dropTable(connectionSource, DBPosition.class, true);
            TableUtils.dropTable(connectionSource, DBRole.class, true);
            TableUtils.dropTable(connectionSource, DBUser.class, true);
            TableUtils.dropTable(connectionSource, DBUserNode.class, true);

            onCreate(database, connectionSource);

        } catch (SQLException e)
        {
            e.printStackTrace();
        }

    }

    private static DatabaseHelper instance;

    /**
     * 单例获取该Helper
     *
     * @param context
     * @return
     */
    public static synchronized DatabaseHelper getHelper(Context context)
    {
        context = context.getApplicationContext();
        if (instance == null)
        {
            synchronized (DatabaseHelper.class)
            {
                if (instance == null)
                    instance = new DatabaseHelper(context);
            }
        }

        return instance;
    }

    public synchronized Dao getDao(Class clazz) throws SQLException
    {
        Dao dao = null;
        String className = clazz.getSimpleName();

        if (daos.containsKey(className))
        {
            dao = daos.get(className);
        }
        if (dao == null)
        {
            dao = super.getDao(clazz);
            daos.put(className, dao);
        }
        return dao;
    }

    /**
     * 释放资源
     */
    @Override
    public void close()
    {
        super.close();

        for (String key : daos.keySet())
        {
            Dao dao = daos.get(key);
            dao.clearObjectCache();
            dao = null;
        }
    }

    public void dropAndCreateTable(){
        try {
            TableUtils.dropTable(getConnectionSource(), DBDepartment.class, true);
            TableUtils.dropTable(getConnectionSource(), DBPosition.class, true);
            TableUtils.dropTable(getConnectionSource(), DBRole.class, true);
            TableUtils.dropTable(getConnectionSource(), DBUser.class, true);
            TableUtils.dropTable(getConnectionSource(), DBUserNode.class, true);

            TableUtils.createTable(getConnectionSource(), DBDepartment.class);
            TableUtils.createTable(getConnectionSource(), DBPosition.class);
            TableUtils.createTable(getConnectionSource(), DBRole.class);
            TableUtils.createTable(getConnectionSource(), DBUser.class);
            TableUtils.createTable(getConnectionSource(), DBUserNode.class);

            daos = new HashMap<String, Dao>();
        }
        catch (Exception e){}

    }

    public void clearOrganizationData(){
        try {

            daos = new HashMap<String, Dao>();

            TableUtils.dropTable(getConnectionSource(), DBDepartment.class, true);
            TableUtils.dropTable(getConnectionSource(), DBPosition.class, true);
            TableUtils.dropTable(getConnectionSource(), DBRole.class, true);
            TableUtils.dropTable(getConnectionSource(), DBUser.class, true);
            TableUtils.dropTable(getConnectionSource(), DBUserNode.class, true);
        }
        catch (Exception e){}

    }
}