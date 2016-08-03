package com.loyo.oa.v2.db;

/**
 * Created by EthanGong on 16/8/2.
 */
import android.content.Context;
import android.util.Log;
import java.util.List;

import com.j256.ormlite.misc.TransactionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.Callable;

import com.loyo.oa.v2.db.bean.DBDepartment;
import com.loyo.oa.v2.db.bean.DBPosition;
import com.loyo.oa.v2.db.bean.DBRole;
import com.loyo.oa.v2.db.bean.DBUser;
import com.loyo.oa.v2.db.bean.DBUserNode;
import com.loyo.oa.v2.db.dao.DepartmentDao;
import com.loyo.oa.v2.db.dao.PositionDao;
import com.loyo.oa.v2.db.dao.RoleDao;
import com.loyo.oa.v2.db.dao.UserDao;
import com.loyo.oa.v2.db.dao.UserNodeDao;

public class OrganizationManager {

    private static DatabaseHelper mDatabaseHelper;
    private static Context context;

    private OrganizationManager(Context context) {
        this.context = context;
        this.mDatabaseHelper = DatabaseHelper.getHelper(context);
    }

    public Context getContext() {
        return context;
    }

    private static OrganizationManager instance;
    /**
     * 单例获取该Helper
     *
     * @param context
     * @return
     */
    public static synchronized OrganizationManager shareManager(Context context)
    {
        context = context.getApplicationContext();
        if (instance == null)
        {
            synchronized (OrganizationManager.class)
            {
                if (instance == null)
                    instance = new OrganizationManager(context);
            }
        }

        return instance;
    }


    public static DBDepartment departmentFromJSON(JSONObject JSON, DepartmentDao dao) {

        DBDepartment d = new DBDepartment();
        d.id = JSON.optString("id");
        d.xpath = JSON.optString("xpath");
        d.superiorId = JSON.optString("superiorId");
        d.name = JSON.optString("name");
        d.simplePinyin = JSON.optString("simplePinyin");
        d.userNum = JSON.optInt("userNum");
        d.isRoot = d.id.equals(d.superiorId);
        if (d.superiorId != null) {
            DBDepartment parent = dao.find(d.superiorId);
            if (parent != null) {
                d.parentDepartment = parent;
            }
        }

        return d;
    }

    public static DBUser userFormJSON(JSONObject JSON, String depId) {
        DBUser user = new DBUser();
        user.id = JSON.optString("id");
        user.name = JSON.optString("name");
        user.gender = JSON.optInt("gender");
        user.mobile = JSON.optString("mobile");
        user.avatar = JSON.optString("avatar");
        user.activated = JSON.optBoolean("activated");
        user.simplePinyin = JSON.optString("simplePinyin");
        user.fullPinyin = JSON.optString("fullPinyin");
        user.weixinId = JSON.optString("weixinId");
        user.avatar = JSON.optString("avatar");
        user.bqqDeletable = JSON.optBoolean("bqqDeletable");
        user.isSuperUser = JSON.optBoolean("isSuperUser");
        user.isBQQ = JSON.optBoolean("isBQQ");
        user.deletedAt = JSON.optLong("deletedAt");

        return user;
    }

    public static DBPosition positionFromJSON(JSONObject JSON, String depId)
    {
        DBPosition pos = null;
        JSONArray deptsArray = JSON.optJSONArray("depts");
        if (deptsArray == null){
            return pos;
        }

        for (int k = 0; k < deptsArray.length();k++) {
            JSONObject dep = deptsArray.optJSONObject(k);
            if (dep.optJSONObject("shortDept") != null
                    && dep.optJSONObject("shortDept").optString("id")!=null
                    && dep.optJSONObject("shortDept").optString("id").equals(depId)){


                String positionId = dep.optJSONObject("shortPosition")!=null ?
                        dep.optJSONObject("shortPosition").optString("id") : null;
                if (positionId != null){
                    pos = new DBPosition();
                    pos.id = positionId;
                    pos.name = dep.optJSONObject("shortPosition").optString("name");;
                    pos.sequence = dep.optJSONObject("shortPosition").optInt("sequence");
                }
                break;

            }
        }

        return pos;
    }

    public static String titleFromJSON(JSONObject JSON, String depId) {
        String title = null;
        JSONArray deptsArray = JSON.optJSONArray("depts");
        if (deptsArray == null){
            return title;
        }

        for (int k = 0; k < deptsArray.length();k++) {
            JSONObject dep = deptsArray.optJSONObject(k);
            if (dep.optJSONObject("shortDept") != null
                    && dep.optJSONObject("shortDept").optString("id")!=null
                    && dep.optJSONObject("shortDept").optString("id").equals(depId)){

                title = dep.optString("title");

                break;

            }
        }

        return title;
    }

    public static DBRole roleFromJSON(JSONObject JSON){
        DBRole role = null;
        JSONObject roleObj = JSON.optJSONObject("role");
        String roleId = roleObj!=null?roleObj.optString("id"):null;
        if (roleId!=null) {
            role = new DBRole();
            role.id = roleId;
            role.name = roleObj.optString("name");
            role.dataRange = roleObj.optInt("dataRange");
        }
        return role;
    }

    public void saveOrgnizitionToDB(final String json) {

        try {
            TransactionManager.callInTransaction(mDatabaseHelper.getConnectionSource(),
                    new Callable<Void>()
                    {
                        @Override
                        public Void call() throws Exception
                        {
                            String s = json;
                            long start = System.currentTimeMillis();

                            DepartmentDao departmentDao  = new DepartmentDao(context);
                            PositionDao positionDao = new PositionDao(context);
                            RoleDao roleDao  = new RoleDao(context);
                            UserDao userDao  = new UserDao(context);
                            UserNodeDao nodeDao  = new UserNodeDao(context);
                            if (s == null) {
                                long end = System.currentTimeMillis();
                                Log.i("time------------", "" + (end- start) + "ms");
                                return null;
                            }

                            JSONArray jsonarray = new JSONArray(s);
                            for(int i = 0; i < jsonarray.length(); i++) {
                                JSONObject departmentObj = jsonarray.getJSONObject(i);

                                // 部门
                                DBDepartment d = OrganizationManager.departmentFromJSON(departmentObj, departmentDao);

                                // Node
                                JSONArray userArray = departmentObj.optJSONArray("users");
                                if (userArray == null) {
                                    departmentDao.createOrUpdate(d);
                                    continue;
                                }
                                for(int j = 0; j < userArray.length(); j++) {
                                    JSONObject userObj = userArray.optJSONObject(j);
                                    DBUser user = OrganizationManager.userFormJSON(userObj, d.id);
                                    DBPosition position = OrganizationManager.positionFromJSON(userObj, d.id);
                                    String title = OrganizationManager.titleFromJSON(userObj, d.id);

                                    DBRole role = OrganizationManager.roleFromJSON(userObj);
                                    DBUserNode node = new DBUserNode();
                                    node.id = user.id + "@" + d.id;
                                    node.title = title;
                                    node.position = position;
                                    node.role = role;
                                    node.user = user;
                                    node.department = d;
                                    departmentDao.createOrUpdate(d);
                                    userDao.createOrUpdate(user);
                                    roleDao.createOrUpdate(role);
                                    positionDao.createOrUpdate(position);
                                    nodeDao.createorUpdate(node);
                                }
                            }

                            long end = System.currentTimeMillis();
                            Log.i("time------------", "" + (end- start) + "ms");
                            return null;
                        }
                    });

        }
        catch (Exception e) {
            Log.v("debug", "error");
        }
    }

    public List<DBDepartment> allDepartment() {
        DepartmentDao dao = new DepartmentDao(getContext());
        List<DBDepartment> list = null;

        try {
            list = dao.getDao().queryForAll();
        }
        catch (Exception e) {

        }
        return list;
    }

}
