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

    @DatabaseField(index = true)
    public String departmentId;

    @DatabaseField(index = true)
    public String departmentXpath;

    @DatabaseField(index = true)
    public String userId;

    @DatabaseField(index = true)
    public String roleId;

    @DatabaseField(index = true)
    public String positionId;

    @DatabaseField(defaultValue = "1")
    public int depth;

    @DatabaseField
    public long saveTransactionId;

    @Override
    public boolean equals(Object obj) {
        DBUserNode d =( DBUserNode)obj;
        return id.equals(d.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
