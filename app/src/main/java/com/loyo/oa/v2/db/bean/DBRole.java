package com.loyo.oa.v2.db.bean;

/**
 * Created by EthanGong on 16/8/2.
 */
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "roles")
public class DBRole {

    @DatabaseField(id = true)
    public String id;

    @DatabaseField
    public String name;

    @DatabaseField
    public int dataRange;
}
