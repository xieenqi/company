package com.loyo.oa.contactpicker.model;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.loyo.oa.indexablelist.widget.Indexable;
import com.loyo.oa.v2.db.bean.DBDepartment;
import com.loyo.oa.v2.db.bean.DBUser;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by EthanGong on 2016/10/14.
 */

public class PickDepartmentModel extends PickedModel implements Indexable {

    public final DBDepartment department;
    public boolean isLevel1;
    private static HashMap<String, WeakReference<PickDepartmentModel>> reuseCache = new HashMap<>();
    private boolean mIsSelected;

    public static void clearResueCache() {
        reuseCache.clear();
    }

    private PickDepartmentModel(@NonNull DBDepartment department) {
        this.isDepartment = true;
        this.department = department;
        isLevel1 = this.department.xpath.split("/").length <= 2;
    }

    private static PickDepartmentModel instance(@NonNull DBDepartment department) {
        return new PickDepartmentModel(department);
    }

    public static PickDepartmentModel getPickModel(DBDepartment dept) {
        if (dept==null || dept.id == null) {
            return null;
        }

        PickDepartmentModel result = null;
        WeakReference<PickDepartmentModel> reference = reuseCache.get(dept.id);

        if (reference == null || reference.get() == null) {
            result = instance(dept);
            reuseCache.put(dept.id, new WeakReference<PickDepartmentModel>(result));
        }
        else {
            result = reference.get();
        }

        return result;
    }

    @Override
    public String getDisplayName() {
        String result;
        String name = getName();
        if (TextUtils.isEmpty(name)) {
            result = "暂无";
        } else {
            if (name.length() <= 2) {
                result = name;
            } else {
                result = name.substring(0, 2);
            }
        }
        return result;
    }

    @Override
    public String getDisplayAvatar() {
        return null;
    }

    public String getName() {
        return department.name;
    }

    public boolean isLevel1() {
        return isLevel1;
    }

    public void setSelected(boolean selected) {
        mIsSelected = selected;
    }

    public void setSelectedRecursively(boolean selected) {
        mIsSelected = selected;
    }

    public boolean isSelected(){

        if (department.directUsers.size() <= 0
                &&
                department.childDepts.size() <= 0){
            return true;
        }

        return mIsSelected;
    }

    @Override
    public String getIndex() {
        return department.getSortLetter();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PickDepartmentModel)) return false;

        PickDepartmentModel model = (PickDepartmentModel) o;

        return department.equals(model.department);

    }

    @Override
    public int hashCode() {
        return department.hashCode();
    }

    public boolean isAllUsersSelected() {

        long start = System.currentTimeMillis();
        boolean result = true; /** 默认为true，当部门人数为0是，返回true */
        List<DBUser> allUsers = department.allUsers();
        long middle = System.currentTimeMillis();
        for (DBUser user : allUsers) {
            PickUserModel userModel = PickUserModel.getPickModel(user);
            if (userModel!=null &&  userModel.isSelected() == false) {
                result = false;
                break;
            }
        }
        long end = System.currentTimeMillis();
        Log.v("startend", "middle-start = " + (middle-start) + " ms");
        Log.v("startend", "end-start = " + (end-start) + " ms");
        return result;
    }

    public boolean isAllDerectUsersSelected() {

        long start = System.currentTimeMillis();
        boolean result = true; /** 默认为true，当部门人数为0是，返回true */
        List<DBUser> allUsers = department.allDirectUsers();
        long middle = System.currentTimeMillis();
        for (DBUser user : allUsers) {
            PickUserModel userModel = PickUserModel.getPickModel(user);
            if (userModel!=null &&  userModel.isSelected() == false) {
                result = false;
                break;
            }
        }
        long end = System.currentTimeMillis();
        Log.v("startend", "middle-start = " + (middle-start) + " ms");
        Log.v("startend", "end-start = " + (end-start) + " ms");
        return result;
    }

    public boolean isAllChildDeptSelected() {
        boolean result = true; /** 默认为true，当部门人数为0是，返回true */
        HashSet<DBDepartment> children = department.childDepts;
        for (DBDepartment dept:children) {
            PickDepartmentModel model = PickDepartmentModel.getPickModel(dept);
            if (!model.isSelected()) {
                result = false;
                break;
            }
        }
        return result;
    }

    public List<PickDepartmentModel> topSelected() {

        List<PickDepartmentModel> result = new ArrayList<>();
        if (isSelected()) {
            if (department.childDepts.size() <= 0 && department.directUsers.size() <= 0) {
                return result;
            }
            result.add(this);
        }
        else {
            for (DBDepartment dept:this.department.childDepts) {
                result.addAll(PickDepartmentModel.getPickModel(dept).topSelected());
            }
        }

        return result;
    }
}
