package com.loyo.oa.contactpicker.model;

import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.db.bean.DBDepartment;
import com.loyo.oa.v2.db.bean.DBUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by EthanGong on 2016/10/14.
 */

public class PickedContacts {
    List<PickUserModel> pickedUsers = new ArrayList<>();
    List<PickDepartmentModel> pickedDepartments = new ArrayList<>();

    public PickedContacts() {

    }

    public PickedContacts(String joinedIds) {
    }

    public void addUser(PickUserModel model) {
        model.setSelected(true);
        HashSet<DBDepartment> depts = model.user.depts;
        boolean hasSelectAll = false;
        for (DBDepartment dept: depts) {
            DBDepartment parent = dept;
            do {
                PickDepartmentModel pickDepartmentModel = PickDepartmentModel.getPickModel(parent);
                if (pickDepartmentModel.isAllChildDeptSelected() && pickDepartmentModel.isAllDerectUsersSelected()) {
                    hasSelectAll = true;
                    pickDepartmentModel.setSelected(true);
                }
                else {
                    pickDepartmentModel.setSelected(false);
                }

                parent = parent.parentDept;
            }
            while (parent != null);
        }
        if (!hasSelectAll) {
            pickedUsers.add(model);
        }
        else {
            resetPickedDepartments();
        }

    }

    public void deleteUser(PickUserModel model) {

        model.setSelected(false);
        HashSet<DBDepartment> depts = model.user.depts;
        boolean hasSelectAll = false;
        for (DBDepartment dept: depts) {
            DBDepartment parent = dept;
            do {
                PickDepartmentModel pickDepartmentModel = PickDepartmentModel.getPickModel(parent);
                if (pickDepartmentModel.isSelected()) {
                    hasSelectAll = true;
                }
                pickDepartmentModel.setSelected(false);
                parent = parent.parentDept;
            }
            while (parent != null);
        }
        if (!hasSelectAll) {
            pickedUsers.remove(model);
        }
        else {
            resetPickedDepartments();
        }
    }

    public void addAllUsersOfDepartment(PickDepartmentModel model) {

        model.setSelected(true);

        /** 全选子部门 */
        List<DBDepartment> children = model.department.flatDepartments();
        for (DBDepartment dept: children) {
            PickDepartmentModel deptModel = PickDepartmentModel.getPickModel(dept);

            if (deptModel != null) {
                deptModel.setSelected(true);
            }
        }

        /** 向上遍历父部门，并更新是否全选 */
        DBDepartment parent = model.department;
        while ((parent= parent.parentDept) != null) {
            PickDepartmentModel deptModel = PickDepartmentModel.getPickModel(parent);
            if (deptModel != null) {
                deptModel.setSelected(deptModel.isAllChildDeptSelected() && deptModel.isAllDerectUsersSelected());
            }
        }

        /** 全选所有用户 */
        List<DBUser> allUsers = model.department.allUsers();
        HashSet<DBUser> needAddUsers = new HashSet<>();
        for (DBUser user: allUsers) {
            PickUserModel pickModel = PickUserModel.getPickModel(user);
            if (pickModel != null) {
                pickModel.setSelected(true);
            }
            if (user.depts.size() > 1) {
                needAddUsers.add(user);
            }
        }

        HashSet<PickUserModel> needDeleteUsers = new HashSet<>();
        for (PickUserModel pickModel : pickedUsers) {
            HashSet<DBDepartment> depts = pickModel.user.depts;
            for (DBDepartment dept: depts) {
                if (dept.xpath.contains(model.department.xpath)) {
                    needDeleteUsers.add(pickModel);
                    break;
                }
            }
        }

        pickedUsers.removeAll(needDeleteUsers);

        resetPickedDepartments();

        for (DBUser user: needAddUsers) {
            PickUserModel pickModel = PickUserModel.getPickModel(user);
            addUser(pickModel);
        }

    }

    public void deleteAllUserOfDepartment(PickDepartmentModel model) {

        model.setSelected(false);

        /** 反选子部门 */
        List<DBDepartment> children = model.department.flatDepartments();
        for (DBDepartment dept: children) {
            PickDepartmentModel deptModel = PickDepartmentModel.getPickModel(dept);
            if (deptModel != null) {
                deptModel.setSelected(false);
            }
        }

        /** 向上遍历父部门，并更新是否全选 */
        DBDepartment parent = model.department;
        while ((parent= parent.parentDept) != null) {
            PickDepartmentModel deptModel = PickDepartmentModel.getPickModel(parent);
            if (deptModel != null) {
                deptModel.setSelected(false);
            }
        }

        /** 反选所有用户 */
        List<DBUser> allUsers = model.department.allUsers();
        HashSet<DBUser> needProcessUsers = new HashSet<>();
        for (DBUser user: allUsers) {
            PickUserModel pickModel = PickUserModel.getPickModel(user);
            if (pickModel != null) {
                pickModel.setSelected(false);
            }
            if (user.depts.size() > 1) {
                needProcessUsers.add(user);
            }
        }

        HashSet<PickUserModel> needDeleteUsers = new HashSet<>();
        for (PickUserModel pickModel : pickedUsers) {
            HashSet<DBDepartment> depts = pickModel.user.depts;
            for (DBDepartment dept: depts) {
                if (dept.xpath.contains(model.department.xpath)) {
                    needDeleteUsers.add(pickModel);
                    break;
                }
            }
        }

        pickedUsers.removeAll(needDeleteUsers);
        resetPickedDepartments();

        for (DBUser user: needProcessUsers) {
            PickUserModel pickModel = PickUserModel.getPickModel(user);
            deleteUser(pickModel);
        }
    }

    public void resetPickedDepartments() {
        DBDepartment company = OrganizationManager.shareManager().getsComany();
        pickedDepartments = PickDepartmentModel.getPickModel(company).topSelected();
    }
}
