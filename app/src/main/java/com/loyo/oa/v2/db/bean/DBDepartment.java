package com.loyo.oa.v2.db.bean;

/**
 * Created by EthanGong on 16/8/2.
 */

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

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
}
