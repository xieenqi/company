package com.loyo.oa.v2.db.dao;

/**
 * Created by EthanGong on 16/8/2.
 */

import java.sql.SQLException;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import com.loyo.oa.v2.db.DatabaseHelper;
import com.loyo.oa.v2.db.bean.DBUserNode;

public class UserNodeDao {

    private Context context;
    private Dao<DBUserNode, String> userNodeDaoOpe;
    private DatabaseHelper helper;

    public UserNodeDao(Context context)
    {
        this.context = context;
        try
        {
            helper = DatabaseHelper.getHelper(context);
            userNodeDaoOpe = helper.getDao(DBUserNode.class);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void add(DBUserNode node)
    {
        try
        {
            userNodeDaoOpe.create(node);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }

    }

    public DBUserNode get(String id)
    {
        DBUserNode node = null;
        try
        {
            node = userNodeDaoOpe.queryForId(id);

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return node;
    }

    public DBUserNode findOrCreate(DBUserNode node)
    {
        DBUserNode find = null;
        try
        {
            find = userNodeDaoOpe.queryForId(node.id);
            if (find == null) {
                add(node);
                //find = userNodeDaoOpe.queryForId(node.id);
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return node;
    }

    public DBUserNode createorUpdate(DBUserNode node)
    {
        try
        {
            userNodeDaoOpe.createOrUpdate(node);

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return node;
    }
}

