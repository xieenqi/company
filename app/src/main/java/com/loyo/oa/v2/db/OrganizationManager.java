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
import com.loyo.oa.v2.common.ExtraAndResult;
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

import com.loyo.oa.v2.beans.Position;
import com.loyo.oa.v2.activityui.customer.bean.Role;
import com.loyo.oa.v2.beans.UserInfo;
import com.loyo.oa.v2.activityui.customer.bean.Department;
import com.loyo.oa.v2.activityui.other.bean.User;


import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.tool.SharedUtil;

public class OrganizationManager {

    /* 常量 */
    private static final String kCurrentUserSameDeptsUsers = "loyo.oa.v2.kCurrentUserSameDeptsUsers";

    private static DatabaseHelper mDatabaseHelper;
    private static Context context;

    /* 缓存数据 */
    private static DBUser             sLoginUser;
    private static DBDepartment       sComany;
    private static List<DBDepartment> departmentsCache = new ArrayList<DBDepartment>();
    private static List<DBPosition>   positionsCache   = new ArrayList<DBPosition>();
    private static List<DBRole>       rolesCache = new ArrayList<DBRole>();
    private static List<DBUser>       usersCache = new ArrayList<DBUser>();
    private static List<DBUserNode>   nodesCache = new ArrayList<DBUserNode>();
    private static HashMap<String, Object> caches = new HashMap<String, Object>();

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
        d.isRoot = d.id.equals(d.superiorId) || d.superiorId == null;

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

    /**
     *  DBUser       <---> User
     *  DBDepartment <---  Department
     *  DBRole       <---  Role
     *  DBPosition   <---  Position
     *  DBUserNode   <---  UserInfo
     */

    public static void updateDBUserWithUser(DBUser result, User user)
    {
        result.name = user.name;
        result.gender = user.gender;
        result.mobile = user.mobile;
        result.avatar = user.avatar;
        //result.activated = user
        result.simplePinyin = user.simplePinyin;
        result.fullPinyin = user.fullPinyin;
        result.weixinId = user.weixinId;
        result.birthDay = user.birthDay;
        //result.bqqDeletable = user.bqq
        result.isSuperUser = user.isSuperUser;
        result.isBQQ = user.isBQQ;

        ArrayList<UserInfo> deptsArray = user.depts;
        if (deptsArray == null){
            return ;
        }

        StringBuilder namesBuilder = new StringBuilder();

        for (int k = 0; k < deptsArray.size();k++) {
            UserInfo dep = deptsArray.get(k);
            if (dep.getShortDept() != null){

                String name = dep.getShortDept().getName();
                if (name != null && name.length() > 0) {
                    namesBuilder.append(name);
                }
            }
        }
        result.shortDeptNames = namesBuilder.toString();

        return ;
    }

    public static Boolean isOrganizationCached() {
        return SharedUtil.getBoolean(MainApp.getMainApp(), ExtraAndResult.IS_ORGANIZATION_CACHED);
    }

    public static void setOrganizationCached(Boolean cached) {
        SharedUtil.putBoolean(MainApp.getMainApp(), ExtraAndResult.IS_ORGANIZATION_CACHED, cached);
    }

    public static void clearOrganizationData() {
        mDatabaseHelper.clearOrganizationData();
        sLoginUser = null;
        sComany =null;
        departmentsCache = new ArrayList<DBDepartment>();
        positionsCache   = new ArrayList<DBPosition>();
        rolesCache = new ArrayList<DBRole>();
        usersCache = new ArrayList<DBUser>();
        nodesCache = new ArrayList<DBUserNode>();
        caches = new HashMap<String, Object>();
        setOrganizationCached(false);
    }

    public static void updateDBUserWithDBUser(DBUser result, DBUser user)
    {
        result.name = user.name;
        result.gender = user.gender;
        result.mobile = user.mobile;
        result.avatar = user.avatar;
        //result.activated = user
        result.simplePinyin = user.simplePinyin;
        result.fullPinyin = user.fullPinyin;
        result.weixinId = user.weixinId;
        result.birthDay = user.birthDay;
        //result.bqqDeletable = user.bqq
        result.isSuperUser = user.isSuperUser;
        result.isBQQ = user.isBQQ;
        result.shortDeptNames = user.shortDeptNames;

        return ;
    }

    public static DBUser convertDBUserFormUser(User user) {
        if (user.id == null)
            return null;

        DBUser result = new DBUser();
        result.id = user.id;
        result.name = user.name;
        result.gender = user.gender;
        result.mobile = user.mobile;
        result.avatar = user.avatar;
        //result.activated = user
        result.simplePinyin = user.simplePinyin;
        result.fullPinyin = user.fullPinyin;
        result.weixinId = user.weixinId;
        result.birthDay = user.birthDay;
        //result.bqqDeletable = user.bqq
        result.isSuperUser = user.isSuperUser;
        result.isBQQ = user.isBQQ;

        ArrayList<UserInfo> deptsArray = user.depts;
        if (deptsArray == null){
            return result;
        }

        StringBuilder namesBuilder = new StringBuilder();

        for (int k = 0; k < deptsArray.size();k++) {
            UserInfo dep = deptsArray.get(k);
            if (dep.getShortDept() != null){

                String name = dep.getShortDept().getName();
                if (name != null && name.length() > 0) {
                    namesBuilder.append(name);
                }
            }
        }
        result.shortDeptNames = namesBuilder.toString();

        return result;
    }

    public static DBDepartment convertDBDepartmentFromDepartment(Department dept) {
        if (dept.id == null) {
            return null;
        }
        DBDepartment d = new DBDepartment();
        d.id = dept.id;
        d.xpath = dept.xpath;
        d.superiorId = dept.superiorId;
        d.name = dept.name;
        d.simplePinyin = dept.simplePinyin;
        d.userNum = Integer.valueOf(dept.userNum);
        d.isRoot = d.id.equals(d.superiorId);

        return d;
    }

    public static DBRole convertDBRoleFromUser(User user) {
        if (user.id == null) {
            return null;
        }

        DBRole role = null;
        Role roleObj = user.role;
        String roleId = roleObj!=null?roleObj.id:null;
        if (roleId!=null) {
            role = new DBRole();
            role.id = roleId;
            role.name = roleObj.name;
            role.dataRange = roleObj.dataRange;
        }
        return role;
    }

    public static DBPosition convertDBPositionFromUser(User user, String depId) {
        if (user.getId() == null) {
            return null;
        }

        ArrayList<UserInfo> deptsArray = user.depts;
        if (deptsArray == null){
            return null;
        }

        DBPosition pos = null;

        for (int k = 0; k < deptsArray.size();k++) {
            UserInfo dep = deptsArray.get(k);
            if (dep.getShortDept() != null
                    && dep.getShortDept().id!=null
                    && dep.getShortDept().id.equals(depId)){


                String positionId = dep.getShortPosition()!=null ?
                        dep.getShortPosition().getId() : null;
                if (positionId != null){
                    pos = new DBPosition();
                    pos.id = positionId;
                    pos.name = dep.getShortPosition().getName();
                    pos.sequence = dep.getShortPosition().getSequence();
                }
                break;

            }
        }
        return pos;
    }

    public static String titleFromUser(User user, String depId) {
        if (user.getId() == null) {
            return null;
        }

        ArrayList<UserInfo> deptsArray = user.depts;
        if (deptsArray == null){
            return null;
        }

        String title = null;
        for (int k = 0; k < deptsArray.size();k++) {
            UserInfo dep = deptsArray.get(k);
            if (dep.getShortDept() != null
                    && dep.getShortDept().id!=null
                    && dep.getShortDept().id.equals(depId)){
                title = dep.getTitle();
            }
        }
        return title;
    }

    public void saveOrganizitionToDB(final ArrayList<Department> list) {

        if (list == null || list.size() == 0) {
            return;
        }

        long saveTransactionId = (long)((Math.random() * 9 + 1) * 100000000);
        List<DBDepartment> deptListTmp = new ArrayList<DBDepartment>();
        List<DBUser> userListTmp = new ArrayList<DBUser>();
        List<DBUserNode> nodeListTmp = new ArrayList<DBUserNode>();

        List<DBPosition> positionListTmp = new ArrayList<DBPosition>();
        List<DBRole> roleListTmp = new ArrayList<DBRole>();

        for(int i = 0; i < list.size(); i++) {
            Department departmentObj = list.get(i);

            // 部门
            DBDepartment d = OrganizationManager.convertDBDepartmentFromDepartment(departmentObj);
            if (d!=null){
                deptListTmp.add(d);
            }
            // Node
            ArrayList<User> userArray = departmentObj.users;
            if (userArray == null) {
                continue;
            }
            for(int j = 0; j < userArray.size(); j++) {
                User userObj = userArray.get(j);
                DBUser user = OrganizationManager.convertDBUserFormUser(userObj);
                DBPosition position = OrganizationManager.convertDBPositionFromUser(userObj, d.id);
                String title = OrganizationManager.titleFromUser(userObj, d.id);
                DBRole role = OrganizationManager.convertDBRoleFromUser(userObj);
                if (user == null || d == null){
                    continue;
                }

                DBUserNode node = new DBUserNode();
                node.id = user.id + "@" + d.id;
                node.title = title;
                node.saveTransactionId = saveTransactionId;

                if (position != null){
                    node.positionId = position.id;
                    positionListTmp.add(position);
                }
                if (role != null) {
                    node.roleId = role.id;
                    roleListTmp.add(role);
                }
                if (user != null) {
                    node.userId = user.id;
                    userListTmp.add(user);
                }
                if (d != null) {
                    node.departmentId = d.id;
                    node.departmentXpath = d.xpath;
                    node.depth = d.depth;
                }
                nodeListTmp.add(node);
            }
        }

        final List<DBDepartment>  deptList = new ArrayList<DBDepartment>(new HashSet<DBDepartment>(deptListTmp));
        final List<DBUser> userList = new ArrayList<DBUser>(new HashSet<DBUser>(userListTmp));
        final List<DBUserNode> nodeList = new ArrayList<DBUserNode>(new HashSet<DBUserNode>(nodeListTmp));

        final List<DBRole> roleList = new ArrayList<DBRole>(new HashSet<DBRole>(roleListTmp));
        final List<DBPosition> positionList = new ArrayList<DBPosition>(new HashSet<DBPosition>(positionListTmp));

        mDatabaseHelper.dropAndCreateTable();

        try {
            TransactionManager.callInTransaction(mDatabaseHelper.getConnectionSource(),
                    new Callable<Void>()
                    {
                        @Override
                        public Void call() throws Exception
                        {
                            DepartmentDao departmentDao  = new DepartmentDao(context);
                            PositionDao positionDao = new PositionDao(context);
                            RoleDao roleDao  = new RoleDao(context);
                            UserDao userDao  = new UserDao(context);
                            UserNodeDao nodeDao  = new UserNodeDao(context);

                            for(DBDepartment d : deptList) {
                                departmentDao.add(d);
                            }
                            for(DBUser d : userList) {
                                userDao.add(d);
                            }
                            for(DBPosition d : positionList) {
                                positionDao.add(d);
                            }
                            for(DBRole d : roleList) {
                                roleDao.add(d);
                            }
                            for(DBUserNode node : nodeList) {
                                nodeDao.add(node);
                            }

                            //  设置已本地缓存组织架构
                            OrganizationManager.setOrganizationCached(true);

                            return null;
                        }
                    });

        }
        catch (Exception e) {
            Log.v("debug", "error");
        }
    }

    public void loadOrganizitionDataToCache(){

        departmentsCache = (new DepartmentDao(getContext())).all();
        nodesCache = (new UserNodeDao(getContext())).all();
        usersCache = (new UserDao(getContext())).all();
        positionsCache = (new PositionDao(getContext())).all();
        rolesCache = (new RoleDao(getContext())).all();
    }

    /* 当前登录用户 */
    public DBUser getCurrentUser(){

        // 取缓存
        if (sLoginUser != null) {
            return sLoginUser;
        }

        DBUser result = null;
        String userId = MainApp.user.id;
        if (userId == null) {
            return result;
        }

        for(DBUser user : usersCache) {
            if (user.id.equals(userId)) {
                result = user;
                break;
            }
        }
        sLoginUser = result;

        return result;
    }

    /* 当前登录用户同部门的所有用户（包括子部门） */
    public List<DBUser> getCurrentUserSameDeptsUsers() {

//        if (caches.get(kCurrentUserSameDeptsUsers) != null) {
//            return (List<DBUser>)caches.get(kCurrentUserSameDeptsUsers);
//        }

        List<DBUser> result = new ArrayList<DBUser>();

        // 查找所在部门
        List<String> currentDeptXpath = _currentUserDeptXpaths();

        List<String> targetUserIds= new ArrayList<String>();
        for (DBUserNode node : nodesCache) {
            Iterator<String> xpathIterator = currentDeptXpath.iterator();
            while (xpathIterator.hasNext()) {
                String xpath = xpathIterator.next();
                if (node.userId != null
                        && node.departmentXpath != null
                        && node.departmentXpath.startsWith(xpath))
                {
                    targetUserIds.add(node.userId);
                }
            }
        }

        // 排重
        targetUserIds = new ArrayList<String>(new HashSet<String>(targetUserIds));

        // 按Id查询用户
        for(DBUser user : usersCache) {
            if (targetUserIds.contains(user.id)) {
                result.add(user);
            }
        }

//        caches.put(kCurrentUserSameDeptsUsers, result);

        return result;
    }

    // 当前登录用户所在所有部门的xpath列表
    public List<String> _currentUserDeptXpaths() {
        DBUser currentUser = getCurrentUser();
        List<String> currentDeptXpath = new ArrayList<String>();

        if (currentUser == null) {
            return currentDeptXpath;
        }

        for (DBUserNode node : nodesCache) {
            if (node.userId != null
                    && node.userId.equals(currentUser.id)
                    && node.departmentXpath != null)
            {
                currentDeptXpath.add(node.departmentXpath);
            }
        }

        return currentDeptXpath;
    }

    // 当前登录用户所在所有一级部门的xpath列表
    public List<String> _currentUserTopDeptXpaths() {
        List<String> currentTopDeptXpath = new ArrayList<String>();

        List<String> currentDeptXpath = _currentUserDeptXpaths();
        for(String xpath : currentDeptXpath) {
            String[] components = xpath.split("/");
            if (components.length < 2) {
                continue;
            }
            String target = String.format("%s/%s", components[0], components[1]);
            currentTopDeptXpath.add(target);
        }


        return currentTopDeptXpath;
    }

    // 当前登录用户所在所有一级部门的id列表
    public List<String> _currentUserTopDeptIds() {
        List<String> currentTopDeptId = new ArrayList<String>();

        List<String> currentDeptXpath = _currentUserDeptXpaths();
        for(String xpath : currentDeptXpath) {
            String[] components = xpath.split("/");
            if (components.length < 2) {
                continue;
            }
            String target = String.format("%s", components[1]);
            currentTopDeptId.add(target);
        }

        return currentTopDeptId;
    }

    // 当前登录用户所在所有一级部门列表
    public List<DBDepartment> currentUserTopDepartments() {

        List<DBDepartment> result = new ArrayList<DBDepartment>();

        List<String> currentTopDeptXpath = _currentUserTopDeptXpaths();
        List<String> currentTopDeptId = _currentUserTopDeptIds();
        for (DBDepartment dept : departmentsCache) {
            if (dept.xpath != null
                    && currentTopDeptXpath.contains(dept.xpath)) { // 按xpath查找
                result.add(dept);
            }
            else if (dept.xpath != null
                    && currentTopDeptId.contains(dept.id)) { // 按id查找
                result.add(dept);
            }
        }

        return result;
    }

    // 部门的子部门列表
    public List<DBDepartment> subDepartmentsOfDepartment(DBDepartment parent) {
        List<DBDepartment> result = new ArrayList<DBDepartment>();
        if (parent == null) {
            return result;
        }

        for (DBDepartment dept : departmentsCache) {
            if (parent.id.equals(dept.superiorId)) {
                result.add(dept);
            }
        }

        return result;
    }

    public List<DBDepartment> subDepartmentsOfDepartment(String parentId) {
        List<DBDepartment> result = new ArrayList<DBDepartment>();
        if (parentId == null) {
            return result;
        }

        for (DBDepartment dept : departmentsCache) {
            if (parentId.equals(dept.superiorId)) {
                result.add(dept);
            }
        }

        return result;
    }

    // 部门直属用户列表(非只部门用户)
    public List<DBUser> directUsersOfDepartment(DBDepartment parent) {
        List<DBUser> result = new ArrayList<DBUser>();
        if (parent == null) {
            return result;
        }

        List<String> userIds = new ArrayList<String>();
        for (DBUserNode node : nodesCache) {
            if (node.departmentId != null
                    && node.departmentId.equals(parent.id)
                    && node.userId != null) {
                userIds.add(node.userId);
            }
        }

        for (DBUser user : usersCache) {
            if (userIds.contains(user.id)) {
                result.add(user);
            }
        }

        return result;
    }

    public List<DBUser> directUsersOfDepartment(String parentId) {
        List<DBUser> result = new ArrayList<DBUser>();
        if (parentId == null) {
            return result;
        }

        List<String> userIds = new ArrayList<String>();
        for (DBUserNode node : nodesCache) {
            if (node.departmentId != null
                    && node.departmentId.equals(parentId)
                    && node.userId != null) {
                userIds.add(node.userId);
            }
        }

        for (DBUser user : usersCache) {
            if (userIds.contains(user.id)) {
                result.add(user);
            }
        }

        return result;
    }

    // 公司
    public DBDepartment getsComany() {

        // 优先取缓存
        if (OrganizationManager.sComany != null) {
            return OrganizationManager.sComany;
        }

        List<DBDepartment> list = new ArrayList<DBDepartment>();
        DBDepartment result = null;

        for(DBDepartment dept : departmentsCache) {
            if (dept.isRoot) {
                list.add( dept );
            }
        }

        if (list.size() > 1) {
            //DBUser
            List<String> deptXpaths = _currentUserDeptXpaths();
            String anyXpath = deptXpaths.size()> 0? deptXpaths.get(0):null;
            if (anyXpath != null) {
                for (DBDepartment dept: list) {
                    if (anyXpath.startsWith(dept.id)) {
                        result = dept;
                        break;
                    }
                }
                if (result == null) {
                    result = list.get(0);
                }
            }

        }
        else if (list.size() == 1){
            result = list.get(0);
        }

        OrganizationManager.sComany = result;

        return result;

    }

    // 公司的所有一级部门列表
    public List<DBDepartment> topDepartments() {
        List<DBDepartment> result = subDepartmentsOfDepartment(getsComany());
        return result;
    }

    // 根据id获取用户
    public DBUser getUser(String userId) {
        if (userId == null) {
            return null;
        }
        for (DBUser user : usersCache) {
            if (user.id.equals(userId)) {
                return user;
            }
        }

        return null;
    }

    //
    public List<DBUser> allUsers() {
        return usersCache;
    }

    // 更新
    public void updateUser(DBUser user){
        DBUser target = getUser(user.id);
        if (target != null) {
            updateDBUserWithDBUser(target, user);
            (new UserDao(getContext())).createOrUpdate(target);
        }
    }
}
