package com.loyo.oa.contactpicker.model;

import android.support.annotation.NonNull;

import com.loyo.oa.contactpicker.model.result.StaffMember;
import com.loyo.oa.indexablelist.widget.Indexable;
import com.loyo.oa.v2.db.bean.DBDepartment;
import com.loyo.oa.v2.db.bean.DBUser;

import java.util.HashMap;

/**
 * Created by EthanGong on 2016/10/14.
 */

public class PickUserModel extends PickedModel implements Indexable {

    private static HashMap<String, PickUserModel> reuseCache = new HashMap<>();
    public final DBUser user;
    private boolean mIsSelected;

    public static void clearResueCache() {
        reuseCache.clear();
    }

    private PickUserModel(@NonNull DBUser user) {
        this.isDepartment = false;
        this.user = user;
    }

    private static PickUserModel instance(@NonNull DBUser user) {
        return new PickUserModel(user);
    }

    public static PickUserModel getPickModel(DBUser user) {
        if (user == null || user.id == null) {
            return null;
        }

        PickUserModel result = reuseCache.get(user.id);

        if (result == null) {
            result = instance(user);
            reuseCache.put(user.id, result);
        }

        return result;
    }

    public void setSelected(boolean selected) {
        mIsSelected = selected;
    }

    public boolean isSelected(){
        return mIsSelected;
    }

    public String getName() {
        return user.name;
    }

    public String getAvatar() {
        return user.avatar;
    }

    public boolean isContainedBySelectedDept() {

        for (DBDepartment dept: user.depts) {
            PickDepartmentModel pickedDepartmentModel = PickDepartmentModel.getPickModel(dept);
            if (pickedDepartmentModel.isSelected()) {
                return true;
            }
        }

        return false;

    }

    @Override
    public String getDisplayName() {
        return getName();
    }

    @Override
    public String getDisplayAvatar() {
        return getAvatar();
    }

    @Override
    public StaffMember toStaffMember() {
        StaffMember member = new StaffMember();
        member.id = user.id;
        member.name = user.name;
        member.avatar = user.avatar;
        return member;
    }

    @Override
    public String getIndex() {
        return user.getSortLetter();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PickUserModel)) return false;

        PickUserModel model = (PickUserModel) o;

        return user.equals(model.user);

    }

    @Override
    public int hashCode() {
        return user.hashCode();
    }
}
