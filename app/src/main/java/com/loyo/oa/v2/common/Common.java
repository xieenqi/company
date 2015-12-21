package com.loyo.oa.v2.common;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.SparseArray;

import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.ContactsGroup;
import com.loyo.oa.v2.beans.Department;
import com.loyo.oa.v2.beans.Project;
import com.loyo.oa.v2.beans.ProjectMember;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.beans.UserGroupData;
import com.loyo.oa.v2.beans.UserInfo;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.tool.ListUtil;
import com.loyo.oa.v2.tool.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Common {

    public static final int CUSTOMER_PAGE = 0;
    public static final int TASK_PAGE = 1;
    public static final int WORK_PAGE = 2;
    public static final int WFIN_PAGE = 3;


    public static ArrayList<User> getUsersByProject(Project project) {
        if (null == project) {
            return new ArrayList<>();
        }

        ArrayList<User> users = new ArrayList<>();
        ArrayList<ProjectMember> managers = project.managers;
        ArrayList<ProjectMember> members = project.members;

        if (null == managers) {
            managers = new ArrayList<>();
        }
        if (null != members && !members.isEmpty()) {
            managers.addAll(members);
        }
//        if (null == managers) {
//            managers = new ArrayList<>();
//            if (null != members && !members.isEmpty()) {
//                managers.addAll(members);
//            }
//        } else {
//            if (managers.isEmpty()) {
//                if (null != members && !members.isEmpty()) {
//                    managers.addAll(members);
//                } else {
//                    for (int i = 0; i < members.size(); i++) {
//                        ProjectMember member = members.get(i);
//                        if (!managers.contains(member)) {
//                            managers.add(member);
//                        }
//                    }
//                }
//            }
//        }
        if (!managers.isEmpty()) {
            for (int i = 0; i < managers.size(); i++) {
                User member = managers.get(i).user;
                if (null != member) {
                    users.add(member);
                }
            }
        }
        return users;
    }

    public static ArrayList<UserGroupData> getLstUserGroupData() {
        if (MainApp.lstUserGroupData == null) {
            InitOrganizationFromDB();
        }

        if (MainApp.lstUserGroupData == null) {
            Global.ProcException(new Exception("人员为空!"));
        }

        return MainApp.lstUserGroupData == null ? new ArrayList<UserGroupData>() : MainApp.lstUserGroupData;
    }

    /**
     * 获取部门所有人员（包含部门下属所有部门里的人员）
     *
     * @param deptId
     * @param result
     * @return
     */
    public static ArrayList<User> getUsersByDeptId(String deptId, ArrayList<User> result) {
        ArrayList<Department> departments = getLstDepartment(deptId);
        ArrayList<User> users = getListUser(deptId);

        if (null != users && !users.isEmpty()) {
            for (int i = 0; i < users.size(); i++) {
                User u = users.get(i);
                if (!result.contains(u)) {
                    result.add(u);
                }
            }
        }

        if (null != departments && !departments.isEmpty()) {
            for (int i = 0; i < departments.size(); i++) {
//                getUsersByDeptId(departments.get(i).getId(), result);
            }
        }
        if (null != departments && !departments.isEmpty()) {
            for (int i = 0; i < departments.size(); i++) {
                ArrayList<User> mUsers = departments.get(i).getUsers();
                if (null != mUsers && !mUsers.isEmpty()) {
                    for (int j = 0; j < mUsers.size(); j++) {
                        User u = mUsers.get(j);
                        if (!result.contains(u)) {
                            result.add(u);
                        }
                    }
                }
            }
        }

        return result;
    }
    public static String companyId;
    /**
     * 获取 本部门 的人员信息  xnq
     *
     * @param deptId
     * @return
     */
    public static ArrayList<ContactsGroup> getContactsGroups(String deptId) {


        List<Department> departmentList = getLstDepartment(deptId);//全部 组织 架构
        if (departmentList == null || departmentList.isEmpty()) {
            return new ArrayList<>();
        }

        SparseArray<ArrayList<Department>> maps = new SparseArray<>();//相当于 map 全部字母表 下的部门列表
        ArrayList<ContactsGroup> contactsGroups = new ArrayList<>();
        for (char index = '#'; index <= 'Z'; index += (char) 1) {
            ArrayList<Department> departments = new ArrayList<>();//相同首字母 部门集合
            for (Department department : departmentList) {//遍历组织架构
                if (department == null) {
                    continue;
                }
                if (department.getId().equals(department.getSuperiorId())) {
                    companyId = department.getId();
                    continue;
                }

                if (companyId.equals(department.getSuperiorId())) {
                    String groupName_current = department.getGroupName();
                    if (!TextUtils.isEmpty(groupName_current) && groupName_current.charAt(0) == index) {
                        departments.add(department);
                    }else if(TextUtils.isEmpty(groupName_current)){
                        departments.add(0,department);
                    }
                }

            }
            if (!departments.isEmpty()) {
                maps.put(index, departments);
            }
        }

        if (maps.size() > 0) {
            for (int i = 0; i < maps.size(); i++) {
                ContactsGroup group = new ContactsGroup();
                group.setGroupName(((char) maps.keyAt(i)) + "");
                group.setDepartments(maps.valueAt(i));
                contactsGroups.add(group);
            }
        }

        return contactsGroups;
    }

    public static ArrayList<Department> getLstDepartment() {
        if (MainApp.lstDepartment == null) {
            InitOrganizationFromDB();
        }

        if (MainApp.lstDepartment == null) {
            Global.ProcException(new Exception("部门为空!"));
        }

        return MainApp.lstDepartment == null ? new ArrayList<Department>() : MainApp.lstDepartment;
    }

    /*public static ArrayList<User> getSubUsers() {
        if (MainApp.subUsers == null) {
            MainApp.subUsers = DBManager.Instance().getSubordinates();
        }

        if (MainApp.subUsers == null) {
            Global.ProcException(new Exception("下属为空!"));
        }

        return MainApp.subUsers == null ? new ArrayList<User>() : MainApp.subUsers;
    }*/


//    public static void setSubUsers(ArrayList<User> subUsers) {
//        MainApp.subUsers = subUsers;
//    }

    public static void InitOrganizationFromDB() {
        //根据当前 token到DB中获取
        String token = MainApp.getToken();
        if (StringUtil.isEmpty(token)) {
            return;
        }

        setLstDepartment(DBManager.Instance().getOrganization());
    }

    /**
     * @param departmentList
     */
    static void setOrganization(ArrayList<Department> departmentList) {
        if (departmentList == null) {
            return;
        }

        ArrayList<UserGroupData> lstUserGroupData_current = new ArrayList<>();

        for (Department department : departmentList) {
            if (department == null || ListUtil.IsEmpty(department.getUsers())) {
                continue;
            }

            for (User user : department.getUsers()) {
                if (TextUtils.isEmpty(user.departmentsName)) {

                    user.departmentsName=department.getName();
                }

                Department deptInUser = new Department();
                deptInUser.setId(department.getId());
                deptInUser.setSuperiorId(department.getSuperiorId());
                deptInUser.setName(department.getName());

                UserInfo userInfo = new UserInfo();
                userInfo.setShortDept(department);

                user.depts=new ArrayList<>(Arrays.asList(userInfo));

                String groupName_current = user.getGroupName();

                Boolean isContainsGroupName = false;
                UserGroupData userGroupData_current;

                for (int m = 0; m < lstUserGroupData_current.size(); m++) {
                    userGroupData_current = lstUserGroupData_current.get(m);
                    if (groupName_current != null && groupName_current.equals(userGroupData_current.getGroupName())) {
                        isContainsGroupName = true;
                        Boolean isContainsUser = false;
                        for (int n = 0; n < userGroupData_current.getLstUser().size(); n++) {
                            if (userGroupData_current.getLstUser().get(n).equals(user)) {
                                isContainsUser = true;
                                break;
                            }
                        }
                        if (!isContainsUser) {
                            userGroupData_current.getLstUser().add(user);
                        }

                        break;
                    }
                }

                if (!isContainsGroupName) {
                    userGroupData_current = new UserGroupData();
                    userGroupData_current.setGroupName(groupName_current);
                    userGroupData_current.getLstUser().add(user);
                    userGroupData_current.setDepartmentId(department.getId());

                    lstUserGroupData_current.add(userGroupData_current);
                }
            }
        }

        for (int i = 0; i < lstUserGroupData_current.size(); i++) {
            ComparatorUser comparator = new ComparatorUser();
            Collections.sort(lstUserGroupData_current.get(i).getLstUser(), comparator);
        }

        ComparatorUserGroupData comparatorUserGroupData = new ComparatorUserGroupData();
        Collections.sort(lstUserGroupData_current, comparatorUserGroupData);

        Common.setLstUserGroupData(lstUserGroupData_current);
    }

    /**
     * 缓存  组织 架构 信息 xnq
     *
     * @param _lstDepartment
     */
    public static void setLstDepartment(ArrayList<Department> _lstDepartment) {
        if (_lstDepartment == null) {
            return;
        }

        if (MainApp.lstDepartment == null) {
            MainApp.lstDepartment = _lstDepartment;
        } else {
            MainApp.lstDepartment.clear();
            MainApp.lstDepartment.addAll(_lstDepartment);
        }

        setOrganization(_lstDepartment);
    }

    static void setLstUserGroupData(ArrayList<UserGroupData> _lstUserGroupData) {
        if (_lstUserGroupData == null) {
            return;
        }

        if (MainApp.lstUserGroupData == null) {
            MainApp.lstUserGroupData = _lstUserGroupData;
        } else {
            MainApp.lstUserGroupData.clear();
            MainApp.lstUserGroupData.addAll(_lstUserGroupData);
        }
    }

    public static Department getDepartment(@NonNull String deptId) {
        for (Department d : getLstDepartment()) {
            if (TextUtils.equals(d.getId(), deptId)) {
                return d;
            }
        }

        return null;
    }

    public static int getDepartmentUsersCount(String deptId) {
        int count = 0;

        for (Department d : getLstDepartment()) {
            if (TextUtils.equals(d.getId(), deptId) && d.getUsers() != null) {
                count += d.getUsers().size();
            } else if (TextUtils.equals(d.getSuperiorId(), deptId)) {
                count += getDepartmentUsersCount(d.getId());
            }
        }

        return count;
    }

    public static ArrayList<Department> getLstDepartment(String superDeptId) {
        ArrayList<Department> deptList = new ArrayList<>();

        if (TextUtils.isEmpty(superDeptId)) {
            deptList = getLstDepartment();
        } else {
            for (Department d : getLstDepartment()) {
                if (TextUtils.equals(d.getSuperiorId(), superDeptId) && !TextUtils.equals(d.getId(), superDeptId)) {
                    deptList.add(d);
                }
            }
        }

        return deptList;
    }

    public static ArrayList<User> getListUser(String DeptId) {

        ArrayList<User> users = new ArrayList<>();

        for (UserGroupData groupData : getLstUserGroupData()) {
            for (User user : groupData.getLstUser()) {
                boolean isAdd = false;

                if (user.depts == null) {
                    continue;
                }

                for (UserInfo d : user.depts) {
                    //如果已经填加过人员，就不能重复添加。主要是因为一个人有多个部门的情况。
                    if (!isAdd && TextUtils.equals(d.getShortDept().getId(), DeptId)) {
                        users.add(user);
                        isAdd = true;
                    }
                }
            }
        }

        return users;
    }

    public static String getDeptsUsersName(String deptIds, String userIds) {
        StringBuilder sb = null;

        if (!StringUtil.isEmpty(deptIds)) {
            String[] detps = deptIds.split(",");

            for (Department d : getLstDepartment()) {
                for (String dept : detps) {
                    if (TextUtils.equals(dept, d.getId())) {
                        if (sb == null) {
                            sb = new StringBuilder();
                            sb.append(d.getName());
                        } else {
                            sb.append(",").append(d.getName());
                        }

                        break;
                    }
                }
            }
        }

        if (!StringUtil.isEmpty(userIds)) {
            String[] users = userIds.split(",");

            for (UserGroupData userGroup : getLstUserGroupData()) {
                for (User u : userGroup.getLstUser()) {
                    for (String userId : users) {
                        if (userId.equals(u.id)) {
                            if (sb == null) {
                                sb = new StringBuilder();
                                sb.append(u.getRealname());
                            } else {
                                sb.append(",").append(u.getRealname());
                            }

                            break;
                        }
                    }
                }
            }
        }

        return sb == null ? "" : sb.toString();
    }

    public static User getSuper() {
        User superior = new User();
        superior.id=MainApp.user.superiorId;

        for (UserGroupData userGroup : getLstUserGroupData()) {

            int index = userGroup.getLstUser().indexOf(superior);
            if (index != -1) {
                return userGroup.getLstUser().get(index);
            }
        }
        return new User();
    }
}
