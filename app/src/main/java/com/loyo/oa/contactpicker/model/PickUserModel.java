package com.loyo.oa.contactpicker.model;

import android.support.annotation.NonNull;

import com.loyo.oa.indexablelist.widget.Indexable;
import com.loyo.oa.v2.db.bean.DBUser;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Created by EthanGong on 2016/10/14.
 */

public class PickUserModel implements Indexable {


    private static HashMap<String, WeakReference<PickUserModel>> reuseCache = new HashMap<>();
    public final DBUser user;
    private boolean mIsSelected;

    public static void clearResueCache() {
        reuseCache.clear();
    }

    private PickUserModel(@NonNull DBUser user) {
        this.user = user;
    }

    private static PickUserModel instance(@NonNull DBUser user) {
        return new PickUserModel(user);
    }

    public static PickUserModel getPickModel(DBUser user) {
        if (user == null || user.id == null) {
            return null;
        }



        PickUserModel result = null;
        WeakReference<PickUserModel> reference = reuseCache.get(user.id);

        if (reference == null || reference.get() == null) {
            result = instance(user);
            reuseCache.put(user.id, new WeakReference<PickUserModel>(result));
        }
        else {
            result = reference.get();
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
