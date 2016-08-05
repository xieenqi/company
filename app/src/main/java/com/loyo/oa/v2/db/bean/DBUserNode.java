package com.loyo.oa.v2.db.bean;

/**
 * Created by EthanGong on 16/8/2.
 */

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.io.Serializable;

@DatabaseTable(tableName = "user_nodes")
public class DBUserNode implements Serializable{

    @DatabaseField(id = true)
    public String id;

    @DatabaseField
    public String title;

    @DatabaseField(canBeNull = true, foreign = true, columnName = "department_id",  foreignAutoRefresh = true)
    public DBDepartment department;

    @DatabaseField(canBeNull = true, foreign = true, columnName = "user_id",  foreignAutoRefresh = true)
    public DBUser user;

    @DatabaseField(canBeNull = true, foreign = true, columnName = "role_id",  foreignAutoRefresh = true)
    public DBRole role;

    @DatabaseField(canBeNull = true, foreign = true, columnName = "position_id",  foreignAutoRefresh = true)
    public DBPosition position;

    @DatabaseField
    public long saveTransactionId;

}
