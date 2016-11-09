package com.loyo.oa.v2.activityui.customer.model;


import com.loyo.oa.v2.beans.UserInfo;

import java.util.ArrayList;

/**
 * Created by yyy on 15/12/9.
 */
public class CustomerRepeatList {

    public String id;

    public String name;

    public long createdAt;

    public boolean lock;

    public Owner owner;

    public Owner getmOwner() {
        return owner;
    }

    public void setmOwner(Owner owner) {
        this.owner = owner;
    }

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public class Owner{
        public String id;
        public String name;
        public int gender;
        public String avatar;
        public ArrayList<UserInfo> depts = new ArrayList<>();
    }

}
