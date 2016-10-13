package com.loyo.oa.v2.db.bean;

/**
 * Created by EthanGong on 16/8/2.
 */

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.loyo.oa.v2.db.sort.DepartmentPinyinComparator;
import com.loyo.oa.v2.db.sort.UserPinyinComparator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

@DatabaseTable(tableName = "departments")
public class DBDepartment implements Serializable {
    @DatabaseField(id = true)
    public String id;

    @DatabaseField
    public String xpath;

    @DatabaseField
    public String superiorId;

    @DatabaseField
    public String name;

    @DatabaseField
    public String simplePinyin;

    @DatabaseField
    public String fullPinyin;

    @DatabaseField
    public int userNum;

    @DatabaseField
    public boolean isRoot;

    @DatabaseField(defaultValue = "1")
    public int depth;

    public HashSet<DBUser> directUsers = new HashSet<>();
    public HashSet<DBDepartment> childDepts = new HashSet<>();
    public DBDepartment parentDept;


    public List<DBUser> allUsers() {

        HashSet<DBUser> result = new HashSet<>();
        result.addAll(directUsers);

        Iterator<DBDepartment> iterator = childDepts.iterator();
        while (iterator.hasNext()) {
            result.addAll(iterator.next().allUsers());
        }

        return new ArrayList<DBUser>(result);
    }

    public List<DBDepartment> flatDepartments() {
        List<DBDepartment> result = new ArrayList<>();

        /** 深度遍历 */
        result.add(this);
        List<DBDepartment> children = new ArrayList<>(childDepts);
        Collections.sort(children, new DepartmentPinyinComparator());
        Iterator<DBDepartment> iterator = children.iterator();
        while (iterator.hasNext()) {
            result.addAll(iterator.next().flatDepartments());
        }

        return result;
    }

    public List<DBUser> allUsersSortedByPinyin() {
        List<DBUser> result = allUsers();
        Collections.sort(result, new UserPinyinComparator());
        return result;
    }

    public String getSortLetter() {

        String pinyin = this.simplePinyin != null && this.simplePinyin.length()>0 ? this.simplePinyin:this.fullPinyin;
        if (pinyin != null && pinyin.length() > 0) {
            String sortString = pinyin.substring(0, 1).toUpperCase();
            return sortString;
        }
        else {
            return "#";
        }
    }

    public String pinyin(){
        String pinyin = this.fullPinyin != null && this.fullPinyin.length()>0 ? this.fullPinyin:this.simplePinyin;
        if (pinyin != null && pinyin.length() > 0) {
            return pinyin;
        }
        else {
            return "#";
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != DBDepartment.class) {
            return false;
        }

        DBDepartment d =( DBDepartment)obj;
        return id.equals(d.id);
    }
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
