package com.loyo.oa.v2.db;

/**
 * Created by EthanGong on 16/8/2.
 */
import android.content.Context;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.Callable;

import com.loyo.oa.v2.activityui.customer.bean.Department;
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

import com.loyo.oa.v2.application.MainApp;

public class OrganizationManager {

    private static DatabaseHelper mDatabaseHelper;
    private static Context context;

    /* 缓存数据 */
    private static DBUser sLoginUser;
    private static DBDepartment sComany;

    private OrganizationManager(Context context) {
        this.context = context;
        this.mDatabaseHelper = DatabaseHelper.getHelper(context);
    }

    public Context getContext() {
        return context;
    }

    private static OrganizationManager instance;
    public static void init(Context context) {
        OrganizationManager.context = context;
    }
    /**
     * 单例获取该Helper
     * @return
     */
    public static synchronized OrganizationManager shareManager()
    {
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

        JSONArray deptsArray = JSON.optJSONArray("depts");
        if (deptsArray == null){
            return user;
        }

        StringBuilder namesBuilder = new StringBuilder();

        for (int k = 0; k < deptsArray.length();k++) {
            JSONObject dep = deptsArray.optJSONObject(k);
            if (dep.optJSONObject("shortDept") != null){

                String name = dep.optJSONObject("shortDept").optString("name");
                if (name != null && name.length() > 0) {
                    namesBuilder.append(name);
                }
            }
        }
        user.shortDeptNames = namesBuilder.toString();

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


        final long saveTransactionId = (long)((Math.random() * 9 + 1) * 100000000);
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

                            List<DBDepartment> tmpDepts = new ArrayList<DBDepartment>();

                            for(int i = 0; i < jsonarray.length(); i++) {
                                JSONObject departmentObj = jsonarray.getJSONObject(i);

                                // 部门
                                DBDepartment d = OrganizationManager.departmentFromJSON(departmentObj, departmentDao);
                                tmpDepts.add(d);

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
                                    node.saveTransactionId = saveTransactionId;
                                    departmentDao.createOrUpdate(d);
                                    userDao.createOrUpdate(user);
                                    roleDao.createOrUpdate(role);
                                    positionDao.createOrUpdate(position);
                                    nodeDao.createorUpdate(node);
                                }
                            }

                            for (int i = 0; i < tmpDepts.size(); i++) {
                                DBDepartment dept = tmpDepts.get(i);
                                if (dept.superiorId != null && !(dept.superiorId.equals(dept.id))) {
                                    DBDepartment parent = departmentDao.get(dept.superiorId);
                                    dept.parentDepartment = parent;
                                    departmentDao.createOrUpdate(dept);
                                }
                                else {
                                    departmentDao.createOrUpdate(dept);
                                }
                            }

                            {
                                DBUser user = userDao.get("573576daebe07f03eee89096");
                                DBDepartment dept = departmentDao.get("56a622250c342e5408000002");
                                DBUserNode node = new DBUserNode();
                                node.user = user;
                                node.department = dept;
                                node.id = user.id + "@" + dept.id;
                                node.saveTransactionId = saveTransactionId;
                                nodeDao.createorUpdate(node);
                            }



                            // 删除过时数据
                            DeleteBuilder<DBUserNode, String> deleteBuilder = nodeDao.getDao().deleteBuilder();
                            //
                            deleteBuilder.where().ne("saveTransactionId", saveTransactionId);
                            // prepare our sql statement
                            PreparedDelete<DBUserNode> preparedDelete = deleteBuilder.prepare();
                            // query for all
                            nodeDao.getDao().delete(preparedDelete);

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

    public List<DBUser> getUsersAtSameDeptsOfUser(){

        return this.getUsersAtSameDeptsOfUser(false);
    }

    public List<DBUser> getUsersAtSameDeptsOfUser(Boolean excludeSelf){
        String userId = MainApp.user.id;
        UserDao dao = new UserDao(getContext());
        List<DBUser> list = new ArrayList<DBUser>();

        if (userId == null) {
            return list;
        }

        try {
            DBUser user = null;
            if ( OrganizationManager.sLoginUser != null) {
                user = OrganizationManager.sLoginUser;
            }
            else {
                user = new UserDao(getContext()).get(userId);
                if (user == null) {
                    return list;
                }
                OrganizationManager.sLoginUser = user;
            }

            List<DBUserNode> nodes = user.allNodes();
            Iterator<DBUserNode> iterator = nodes.iterator();
            while (iterator.hasNext()) {
                DBUserNode node = iterator.next();
                DBDepartment dept = node.department;
                if (dept == null) {
                    continue;
                }
                list.addAll(dept.allUsers());
            }
        }
        catch (Exception e) {}

        // 去重，去掉自己
        HashMap<String, DBUser> map = new HashMap<String, DBUser>();
        Iterator<DBUser> iterator = list.iterator();
        while (iterator.hasNext()) {
            DBUser user = iterator.next();
            if (excludeSelf && (userId.equals(user.id))) {
                continue;
            }
            else if (user.id != null){
                map.put(user.id, user);
            }
        }

        Collection<DBUser> collection = map.values();
        list = new ArrayList<DBUser>(collection);

        return list;
    }

    public DBUser currentUser(boolean refresh) {
        DBUser user = OrganizationManager.sLoginUser;
        String userId = MainApp.user.id;
        if (user == null || refresh) {
            user = new UserDao(getContext()).get(userId);
        }
        return user;
    }

    public DBDepartment company() {

        //  取缓存数据
        if (OrganizationManager.sComany != null) {
            return OrganizationManager.sComany;
        }

        // 按当前登录用户的部门向上查找公司
//        DBUser currentLoginUser = OrganizationManager.sLoginUser;
//        if (currentLoginUser != null) {
//            DBDepartment result = null;
//            List<DBDepartment> depts = currentLoginUser.allDepartment();
//            Iterator<DBDepartment> iterator = depts.iterator();
//            while (iterator.hasNext()) {
//                DBDepartment dept = iterator.next();
//                DBDepartment parent = dept.parentDepartment;
//                while (parent!=null && parent.parentDepartment != null) {
//                    parent = parent.parentDepartment;
//                }
//                if (parent.isRoot) {
//                    result = parent;
//                    break;
//                }
//            }
//
//            if (result != null) {
//                OrganizationManager.sComany = result;
//                return result;
//            }
//        }

        // 直接从数据库中读取
        DepartmentDao dao = new DepartmentDao(getContext());
        List<DBDepartment> list = null;
        DBDepartment company = null;
        try {
            QueryBuilder<DBDepartment, String> queryBuilder = dao.getDao().queryBuilder();
            queryBuilder.where().eq("isRoot", true);
            PreparedQuery<DBDepartment> preparedQuery = queryBuilder.prepare();
            list = dao.getDao().query(preparedQuery);
        }
        catch (Exception e) {

        }
        if (list.size() > 0) {
            company = list.get(0);
        }

        OrganizationManager.sComany = company;
        return company;
    }

    public List<DBDepartment> level1Departments() {
        List<DBDepartment> result = new ArrayList<DBDepartment>();

        DBDepartment company = this.company();
        if (company == null) {
            return result;
        }

        ForeignCollection<DBDepartment> depts = company.childDepartments;
        CloseableIterator<DBDepartment> iterator = depts.closeableIterator();

        DBDepartment dept = null;
        try {
            while (iterator.hasNext()){
                dept = iterator.next();
                result.add(dept);
            }
        }
        finally {
            // must always close our iterators otherwise connections to the database are held open
            try {
                iterator.close();
            }
            catch (Exception e){}
        }

        return result;
    }

    public DBDepartment getDepartment(String deptId, String xpath) {
        DBDepartment result = null;

        if (deptId == null) {
            return result;
        }
        if (xpath != null) { // 在缓存中查找
            DBDepartment company = OrganizationManager.sComany;
            if (company != null) {
                result = company.subDepartmentWithXpath(xpath);
                if (result != null) return result;
            }
        }

        // 从数据库读取
        result = (new DepartmentDao(getContext())).get(deptId);

        return result;
    }

    public List<List<Object>> getChildrenOf(String deptId, String xpath){

        List<Object> users = new ArrayList<Object>();
        List<Object> depts = new ArrayList<Object>();
        List<List<Object>> result = new ArrayList<List<Object>>();
        result.add(users);
        result.add(depts);

        DBDepartment department = this.getDepartment(deptId, xpath);
        if (department != null) {
            users.addAll(department.allUsersWithoutSubDepartmentUsers());
            depts.addAll(department.subDepartments());
        }

        return result;

    }
}
