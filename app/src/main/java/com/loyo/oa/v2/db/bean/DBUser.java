package com.loyo.oa.v2.db.bean;

/**
 * Created by EthanGong on 16/8/2.
 */

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.Serializable;

@DatabaseTable(tableName = "users")
public class DBUser implements Serializable {

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
    public String birthDay;

    @DatabaseField
    public String shortDeptNames;

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

    public List<DBDepartment> allDepartment() {
        List<DBDepartment> result = new ArrayList<DBDepartment>();

        List<DBUserNode> nodes = this.allNodes();
        if (nodes == null) {
            return result;
        }

        Iterator<DBUserNode> iterator = nodes.iterator();
        while (iterator.hasNext()){
            DBUserNode node = iterator.next();
            if (node.department != null){
                result.add(node.department);
            }
        }

        return result;
    }
}

