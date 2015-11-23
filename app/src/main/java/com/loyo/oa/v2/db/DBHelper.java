package com.loyo.oa.v2.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableUtils;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Attachment;
import com.loyo.oa.v2.beans.Config.SomeConfig;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.Department;
import com.loyo.oa.v2.beans.SaleActivity;
import com.loyo.oa.v2.beans.Task;
import com.loyo.oa.v2.beans.TrackRule;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.beans.WfInstance;
import com.loyo.oa.v2.beans.WorkReport;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.StringUtil;

import java.io.File;
import java.lang.reflect.Type;
import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by pj on 15/4/2.
 */
public class DBHelper extends OrmLiteSqliteOpenHelper {

    // name of the database file for your application -- change to something
    // appropriate for your app
    private static final String DATABASE_NAME = "LeShare.db";
    // any time you make changes to your database objects, you may have to
    // increase the database version
    private static final int DATABASE_VERSION = 4;
    // the DAO object we use to access the tables
    private RuntimeExceptionDao<SomeConfig, Integer> stuffsRuntimeDao = null;
    private RuntimeExceptionDao<Attachment, Integer> attachmentsRuntimeDao = null;
    // 添加事物
    public DatabaseConnection conn;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is first created. Usually you should
     * call createTable statements here to create the tables that will store
     * your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.d(DBHelper.class.getName(), "onCreate");
            conn = connectionSource.getSpecialConnection();

            TableUtils.createTable(connectionSource, SomeConfig.class);
            TableUtils.createTable(connectionSource, Attachment.class);

        } catch (SQLException e) {
            Log.e(DBHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * This is called when your application is upgraded and it has a higher
     * version number. This allows you to adjust the various data to match the
     * new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion,
                          int newVersion) {

        // 先删除后重建
//        db.execSQL("DROP TABLE IF EXISTS organization;");

        try {
            Log.d(DBHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, SomeConfig.class, true);
            TableUtils.dropTable(connectionSource, Attachment.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(DBHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao
     * for our classes. It will create it or just give the cached value.
     * RuntimeExceptionDao only through RuntimeExceptions.
     */
    public RuntimeExceptionDao<SomeConfig, Integer> getSomeConfigDao() {
        if (stuffsRuntimeDao == null) {
            stuffsRuntimeDao = getRuntimeExceptionDao(SomeConfig.class);
        }
        return stuffsRuntimeDao;
    }

    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao
     * for our classes. It will create it or just give the cached value.
     * RuntimeExceptionDao only through RuntimeExceptions.
     */
    public RuntimeExceptionDao<Attachment, Integer> getAttachmentDao() {
        if (attachmentsRuntimeDao == null) {
            attachmentsRuntimeDao = getRuntimeExceptionDao(Attachment.class);
        }
        return attachmentsRuntimeDao;
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        stuffsRuntimeDao = null;
    }

    String getStr(String t) {
        return getStr(t, "");
    }

    String getStr(String t, String sid) {
        try {

            List<SomeConfig> list = DBManager
                    .Instance()
                    .getSomeConfigDao().queryBuilder()
                    .where().eq("token", MainApp.getToken()).and().eq("t", t).and().eq("sid", sid)
                    .query();

            if (list != null && list.size() > 0) {
                SomeConfig sc = list.get(0);

                if (sc != null) {
                    return sc.json;
                }
            }

        } catch (Exception ex) {
            Global.ProcException(ex);
        }

        return "";
    }

    void putStr(final String t, final String json) {
        putStr(t, "", json);
    }

    void putStr(final String t, final String sid, final String json) {

        final RuntimeExceptionDao<SomeConfig, Integer> cateRuntimeDao = DBManager
                .Instance()
                .getSomeConfigDao();
        try {
            TransactionManager.callInTransaction(DBManager.Instance()
                    .getConnectionSource(), new Callable<Void>() {

                @Override
                public Void call() throws Exception {

                    List<SomeConfig> list = DBManager
                            .Instance()
                            .getSomeConfigDao().queryBuilder()
                            .where().eq("token", MainApp.getToken()).and().eq("t", t).and().eq("sid", sid)
                            .query();

                    if (StringUtil.isEmpty(json)){
                        if (list != null && list.size() > 0) {
                            DBManager.Instance().getSomeConfigDao().delete(list.get(0));
                        }

                    } else if (list != null && list.size() > 0) {
                        list.get(0).json = json;
                        cateRuntimeDao.update(list.get(0));
                    } else {
                        SomeConfig sc = new SomeConfig();
                        sc.token = MainApp.getToken();
                        sc.t = t;
                        sc.json = json;
                        sc.sid = sid;
                        cateRuntimeDao.create(sc);
                    }

                    return null;
                }
            });
        } catch (Exception ex) {
            Global.ProcException(ex);
        }
    }

    void delete(String t, String sid) {
        putStr(t, sid, "");
    }

    public User getUser() {
        String str = getStr("user");

        if (!StringUtil.isEmpty(str)) {
            return MainApp.gson.fromJson(str, User.class);
        }

        return null;
    }

    public void putUser(String json) {
        putStr("user", json);
    }

    public ArrayList<User> getSubordinates() {
        Type type = new TypeToken<ArrayList<User>>() {
        }.getType();
        String str = getStr("Subordinates");

        if (!StringUtil.isEmpty(str)) {
            return MainApp.gson.fromJson(str, type);
        }

        return new ArrayList<>();
    }

    public void putSubordinates(String json) {
        putStr("Subordinates", json);
    }

    public ArrayList<Department> getOrganization() {
        Type type = new TypeToken<ArrayList<Department>>() {
        }.getType();
        String str = getStr("organization");

        if (!StringUtil.isEmpty(str)) {
            return MainApp.gson.fromJson(str, type);
        }

        return new ArrayList<>();
    }

    public void putOrganization(String json) {
        putStr("organization", json);
    }

    public void saveAttachment(final Attachment attachment) {
        final RuntimeExceptionDao<Attachment, Integer> cateRuntimeDao = DBManager
                .Instance()
                .getAttachmentDao();
        try {
            TransactionManager.callInTransaction(DBManager.Instance()
                    .getConnectionSource(), new Callable<Void>() {

                @Override
                public Void call() throws Exception {

                    if (attachment.getFile() == null) {
                        return null;
                    }

                    List<Attachment> list = DBManager
                            .Instance()
                            .getAttachmentDao().queryBuilder()
                            .where().eq("id", attachment.getId())
                            .query();

                    if (list != null && list.size() > 0) {

                        list.get(0).setLocalPath(attachment.getFile().getPath());
                        list.get(0).setOriginalName(attachment.getOriginalName());
                        list.get(0).setName(attachment.getName());
                        list.get(0).setUrl(attachment.getUrl());
                        cateRuntimeDao.update(list.get(0));

                    } else {
                        attachment.setLocalPath(attachment.getFile().getPath());
                        cateRuntimeDao.create(attachment);
                    }

                    return null;
                }
            });
        } catch (Exception ex) {
            Global.ProcException(ex);
        }
    }

    public Attachment getAttachment(Attachment attachment) {
        try {
            List<Attachment> list = DBManager
                    .Instance()
                    .getAttachmentDao().queryBuilder()
                    .where().eq("id", attachment.getId())
                    .query();

            if (list != null && list.size() > 0) {

                //取最后一个
                String localPath = list.get(list.size() - 1).getLocalPath();

                if (!StringUtil.isEmpty(localPath)) {
                    attachment.setLocalPath(localPath);
                    attachment.saveFile(new File(URI.create("file://" + localPath)));
                    return attachment;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public Task getTask() {
        String str = getStr("task");

        if (!StringUtil.isEmpty(str)) {
            return MainApp.gson.fromJson(str, Task.class);
        }

        return null;
    }

    public void putTask(String json) {
        putStr("task", json);
    }

    public void deleteTask() {
        delete("task", "0");
    }

    public Task getTaskScore(String sid) {
        String str = getStr("taskScore", sid);

        Task task = null;
        if (!StringUtil.isEmpty(str)) {
            task = MainApp.gson.fromJson(str, Task.class);
        }

        return task;
    }

    public void putTaskScore(String json, String sid) {
        putStr("taskScore", sid, json);
    }

    public void deleteTaskScore(String sid) {
        delete("taskScore", sid);
    }

    public WorkReport getWorkReport() {
        String str = getStr("WorkReport");

        if (!StringUtil.isEmpty(str)) {
            return MainApp.gson.fromJson(str, WorkReport.class);
        }

        return null;
    }

    public void putWorkReport(String json) {
        putStr("WorkReport", json);
    }

    public void deleteWorkReport() {
        delete("WorkReport", "0");
    }

    public WorkReport getWorkReportScore(String sid) {
        String str = getStr("WorkReportScore", sid);

        if (!StringUtil.isEmpty(str)) {
            return MainApp.gson.fromJson(str, WorkReport.class);
        }

        return null;
    }

    public void putWorkReportScore(String json, String sid) {
        putStr("WorkReportScore", sid, json);
    }

    public void deleteWorkReportScore(String sid) {
        delete("WorkReportScore", sid);
    }

    public WfInstance getWfInstance() {
        String str = getStr("WfInstance");

        if (!StringUtil.isEmpty(str)) {
            return MainApp.gson.fromJson(str, WfInstance.class);
        }

        return null;
    }

    public void putWfInstance(String json) {
        putStr("WfInstance", json);
    }

    public void deleteWfInstance() {
        delete("WfInstance", "");
    }

    public String getWfInstanceComment(String sid) {
        String str = getStr("WfInstanceComment", sid);

        if (!StringUtil.isEmpty(str)) {
            return str;//MainApp.gson.fromJson(getStr("WfInstance"), WfInstance.class);
        }

        return null;
    }

    public void putWfInstanceComment(String comment, String sid) {
        putStr("WfInstanceComment", sid, comment);
    }

    public void deleteWfInstanceComment(String sid) {
        delete("WfInstanceComment", sid);
    }

    public Customer getCustomer() {
        String str = getStr("Customer");

        if (!StringUtil.isEmpty(str)) {
            return MainApp.gson.fromJson(str, Customer.class);
        }

        return null;
    }

    public void putCustomer(String json) {
        putStr("Customer", json);
    }

    public void deleteCustomer() {
        delete("Customer", "");
    }

    public SaleActivity getSaleActivity(String sid) {
        String str = getStr("SaleActivity", sid);

        if (!StringUtil.isEmpty(str)) {
            return MainApp.gson.fromJson(str, SaleActivity.class);
        }

        return null;
    }

    public void putSaleActivity(String json, String sid) {
        putStr("SaleActivity", sid, json);
    }

    public void deleteSaleActivity(String sid) {
        delete("SaleActivity", sid);
    }

    public void putTrackRule(String json) {
        putStr("TrackRule", json);
    }

    public TrackRule getTrackRule() {
        String str = getStr("TrackRule");

        if (!StringUtil.isEmpty(str)) {
            return MainApp.gson.fromJson(str, TrackRule.class);
        }

        return null;
    }

    public void putLog(String sid, String json) {
        putStr("Log", sid, json);
    }
}
