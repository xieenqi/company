package com.loyo.oa.v2.db.dao;

/**
 * Created by EthanGong on 16/8/2.
 */

import java.sql.SQLException;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import com.loyo.oa.v2.db.DatabaseHelper;
import com.loyo.oa.v2.db.bean.DBRole;

public class RoleDao {

    private Context context;
    private Dao<DBRole, String> roleDaoOpe;
    private DatabaseHelper helper;

    public RoleDao(Context context)
    {
        this.context = context;
        try
        {
            helper = DatabaseHelper.getHelper(context);
            roleDaoOpe = helper.getDao(DBRole.class);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void add(DBRole role)
    {
        try
        {
            roleDaoOpe.create(role);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }

    }

    public DBRole get(String id)
    {
        DBRole role = null;
        try
        {
            role = roleDaoOpe.queryForId(id);

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return role;
    }

    public DBRole findOrCreate(DBRole role)
    {
        DBRole find = null;
        try
        {
            find = roleDaoOpe.queryForId(role.id);
            if (find == null) {
                add(role);
                //find = roleDaoOpe.queryForId(role.id);
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return role;
    }

    public DBRole createOrUpdate(DBRole role)
    {
        try
        {
            roleDaoOpe.createOrUpdate(role);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return role;
    }
}

