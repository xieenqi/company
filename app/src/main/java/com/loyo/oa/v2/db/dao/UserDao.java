package com.loyo.oa.v2.db.dao;

/**
 * Created by EthanGong on 16/8/2.
 */

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import com.loyo.oa.v2.db.DatabaseHelper;
import com.loyo.oa.v2.db.bean.DBUser;

public class UserDao {

    private Context context;
    private Dao<DBUser, String> userDaoOpe;
    private DatabaseHelper helper;

    public UserDao(Context context)
    {
        this.context = context;
        try
        {
            helper = DatabaseHelper.getHelper(context);
            userDaoOpe = helper.getDao(DBUser.class);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void add(DBUser user)
    {
        try
        {
            userDaoOpe.create(user);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }

    }

    public DBUser get(String id)
    {
        DBUser user = null;
        try
        {
            user = userDaoOpe.queryForId(id);

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return user;
    }

    public DBUser findOrCreate(DBUser user)
    {
        DBUser find = null;
        try
        {
            find = userDaoOpe.queryForId(user.id);
            if (find == null) {
                //add(user);
                find = userDaoOpe.queryForId(user.id);
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return user;
    }

    public DBUser createOrUpdate(DBUser user)
    {
        try
        {
            userDaoOpe.createOrUpdate(user);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return user;
    }

    public List<DBUser> all(){
        List<DBUser> result = new ArrayList<DBUser>();
        try
        {
            List<DBUser> d = userDaoOpe.queryForAll();
            if (d !=null) {
                result = d;
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return result;
    }
}

