package com.loyo.oa.v2.beans.Config;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by pj on 15/6/5.
 */

@DatabaseTable(tableName = "SomeConfig")
public class SomeConfig {

    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField(defaultValue = "0")
    public String sid;

    @DatabaseField
    public String token;

    @DatabaseField
    public String t;

    @DatabaseField
    public String json;

}
