package com.loyo.oa.v2.db.bean;

/**
 * Created by EthanGong on 16/8/2.
 */
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.io.Serializable;

@DatabaseTable(tableName = "roles")
public class DBRole implements Serializable {

    @DatabaseField(id = true)
    public String id;

    @DatabaseField
    public String name;

    @DatabaseField
    public int dataRange;

    @Override
    public boolean equals(Object obj) {
        DBRole d =( DBRole)obj;
        return id.equals(d.id);
    }
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
