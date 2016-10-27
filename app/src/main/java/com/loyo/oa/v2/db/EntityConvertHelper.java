package com.loyo.oa.v2.db;

import com.loyo.oa.v2.activityui.customer.model.Department;
import com.loyo.oa.v2.activityui.customer.model.Role;
import com.loyo.oa.v2.activityui.other.model.User;
import com.loyo.oa.v2.beans.UserInfo;
import com.loyo.oa.v2.db.bean.DBDepartment;
import com.loyo.oa.v2.db.bean.DBPosition;
import com.loyo.oa.v2.db.bean.DBRole;
import com.loyo.oa.v2.db.bean.DBUser;
import com.loyo.oa.v2.db.dao.DepartmentDao;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by EthanGong on 2016/10/12.
 */

public class EntityConvertHelper {

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

            if (k != 0) {
                namesBuilder.append(",");
            }

            UserInfo dep = deptsArray.get(k);
            if (dep.getShortDept() != null){

                String name = dep.getShortDept().getName();
                if (name != null && name.length() > 0) {
                    namesBuilder.append(name);
                }
            }
            if (dep.getShortPosition() != null){

                String name = dep.getShortPosition().getName();
                if (name != null && name.length() > 0) {
                    namesBuilder.append("|" + name);
                }
            }
        }
        result.shortDeptNames = namesBuilder.toString();

        return ;
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

            if (k != 0) {
                namesBuilder.append(",");
            }

            UserInfo dep = deptsArray.get(k);
            if (dep.getShortDept() != null){

                String name = dep.getShortDept().getName();
                if (name != null && name.length() > 0) {
                    namesBuilder.append(name);
                }
            }
            if (dep.getTitle() != null){
                String name = dep.getTitle();
                if (name != null && name.length() > 0) {
                    namesBuilder.append("|" + name);
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
}
