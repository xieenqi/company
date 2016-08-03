package com.loyo.oa.v2.db.bean;

/**
 * Created by EthanGong on 16/8/2.
 */

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "users")
public class DBUser {

    @DatabaseField(id = true)
    public String id;

    @DatabaseField
    public String name;

    @DatabaseField
    public int gender;

    @DatabaseField
    public String mobile;

    @DatabaseField
    public String avatar;

    @DatabaseField
    public boolean activated;

    @DatabaseField
    public String simplePinyin;

    @DatabaseField
    public String fullPinyin;

    @DatabaseField
    public String weixinId;

    @DatabaseField
    public boolean bqqDeletable;

    @DatabaseField
    public boolean isSuperUser;

    @DatabaseField
    public Long deletedAt;

    @DatabaseField
    public boolean isBQQ;

    @ForeignCollectionField
    public ForeignCollection<DBUserNode> userNodes;
}

