package com.loyo.oa.v2.db.bean;

/**
 * Created by EthanGong on 16/8/2.
 */

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

@DatabaseTable(tableName = "departments")
public class DBDepartment {
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
    public int userNum;

    @DatabaseField
    public boolean isRoot;

    @DatabaseField(canBeNull = true, foreign = true, columnName = "parent_id",  foreignAutoRefresh = true)
    public DBDepartment parentDepartment;

    @ForeignCollectionField
    public ForeignCollection<DBDepartment> childDepartments;

    @ForeignCollectionField
    public ForeignCollection<DBUserNode> userNodes;

    public List<DBUserNode> allNodes() {

        List<DBUserNode> result = new ArrayList<DBUserNode>();
        ForeignCollection<DBUserNode> nodes = this.userNodes;
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
        Iterator<DBUserNode> iterator = nodes.iterator();
        while (iterator.hasNext()){
            DBUserNode node = iterator.next();
            if (node.user != null){
                result.add(node.user);
            }
        }

        //  子部门的用户
        List<DBDepartment> childrenList = new ArrayList<DBDepartment>();
        ForeignCollection<DBDepartment> children = this.childDepartments;
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

}
