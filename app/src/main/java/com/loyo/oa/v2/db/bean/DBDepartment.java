package com.loyo.oa.v2.db.bean;

/**
 * Created by EthanGong on 16/8/2.
 */

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
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

    @DatabaseField(defaultValue = "1")
    public int depth;


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
        DBDepartment d =( DBDepartment)obj;
        return id.equals(d.id);
    }
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
