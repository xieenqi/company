package com.loyo.oa.v2.db.bean;

/**
 * Created by EthanGong on 16/8/2.
 */

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.types.NativeUuidType;
import com.j256.ormlite.stmt.*;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.io.Serializable;

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

    @DatabaseField(canBeNull = true, foreign = true, columnName = "parent_id")
    public DBDepartment parentDepartment;

    @ForeignCollectionField
    public ForeignCollection<DBDepartment> childDepartments;

    @ForeignCollectionField
    public ForeignCollection<DBUserNode> userNodes;

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

    public List<DBUserNode> allNodes() {

        List<DBUserNode> result = new ArrayList<DBUserNode>();
        ForeignCollection<DBUserNode> nodes = this.userNodes;
        if (nodes == null) {
            return result;
        }
        CloseableIterator<DBUserNode> iterator = nodes.closeableIterator();

        DBUserNode node = null;
        try {
            while (iterator.hasNext()){
                node = iterator.next();
                result.add(node);
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

    public List<DBUser> allUsers() {

        List<DBUser> result = new ArrayList<DBUser>();

        // 本部门的用户
        List<DBUserNode> nodes = this.allNodes();
        if (nodes != null) {
            Iterator<DBUserNode> iterator = nodes.iterator();
            while (iterator.hasNext()){
                DBUserNode node = iterator.next();
                if (node.user != null){
                    result.add(node.user);
                }
            }
        }

        //  子部门的用户
        List<DBDepartment> childrenList = new ArrayList<DBDepartment>();
        ForeignCollection<DBDepartment> children = this.childDepartments;
        if (children == null) {
            return result;
        }

        CloseableIterator<DBDepartment> deptIterator = children.closeableIterator();

        try {
            while (deptIterator.hasNext()){
                DBDepartment sub = deptIterator.next();
                result.addAll(sub.allUsers());
            }
        }
        finally {
            // must always close our iterators otherwise connections to the database are held open
            try {
                deptIterator.close();
            }
            catch (Exception e){}
        }


        return result;
    }

    public List<DBDepartment> subDepartments(){
        //  子部门的用户
        List<DBDepartment> childrenList = new ArrayList<DBDepartment>();
        ForeignCollection<DBDepartment> children = this.childDepartments;
        if (children == null) {
            return childrenList;
        }

        CloseableIterator<DBDepartment> deptIterator = children.closeableIterator();

        try {
            while (deptIterator.hasNext()){
                DBDepartment sub = deptIterator.next();
                childrenList.add(sub);
            }
        }
        finally {
            // must always close our iterators otherwise connections to the database are held open
            try {
                deptIterator.close();
            }
            catch (Exception e){}
        }


        return childrenList;
    }

    public List<DBUser> allUsersWithoutSubDepartmentUsers() {

        List<DBUser> result = new ArrayList<DBUser>();

        // 本部门的用户
        List<DBUserNode> nodes = this.allNodes();
        if (nodes == null) {
            return result;
        }

        Iterator<DBUserNode> iterator = nodes.iterator();
        while (iterator.hasNext()){
            DBUserNode node = iterator.next();
            if (node.user != null){
                result.add(node.user);
            }
        }

        return result;
    }

    public DBDepartment subDepartmentWithXpath(String xpath)
    {
        DBDepartment result = null;
        if (this.xpath != null && this.xpath.equals(xpath)) {
            result = this;
            return result;
        }
        if (this.xpath != null && !(xpath.startsWith(this.xpath))) // 避免不必要的深度遍历
        {
            return result;
        }

        ForeignCollection<DBDepartment> children = this.childDepartments;
        if (children == null) {
            return result;
        }

        CloseableIterator<DBDepartment> deptIterator = children.closeableIterator();

        try {
            while (deptIterator.hasNext()){
                DBDepartment sub = deptIterator.next();
                DBDepartment theDept = sub.subDepartmentWithXpath(xpath);
                if (theDept != null) {
                    result = theDept;
                    return result;
                }
            }
        }
        finally {
            // must always close our iterators otherwise connections to the database are held open
            try {
                deptIterator.close();
            }
            catch (Exception e){}
        }

        return result;
    }

}
