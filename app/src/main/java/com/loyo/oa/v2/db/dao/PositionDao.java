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
import com.loyo.oa.v2.db.bean.DBPosition;

public class PositionDao {

    private Context context;
    private Dao<DBPosition, String> positionDaoOpe;
    private DatabaseHelper helper;

    public PositionDao(Context context)
    {
        this.context = context;
        try
        {
            helper = DatabaseHelper.getHelper(context);
            positionDaoOpe = helper.getDao(DBPosition.class);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void add(DBPosition pos)
    {
        try
        {
            positionDaoOpe.create(pos);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }

    }

    public DBPosition get(String id)
    {
        DBPosition pos = null;
        try
        {
            pos = positionDaoOpe.queryForId(id);

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return pos;
    }

    public DBPosition findOrCreate(DBPosition position)
    {
        DBPosition pos = null;
        try
        {
            pos = positionDaoOpe.queryForId(position.id);
            if (pos == null) {
                add(position);
                //pos = positionDaoOpe.queryForId(position.id);
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return position;
    }

    public DBPosition createOrUpdate(DBPosition pos)
    {
        try
        {
            positionDaoOpe.createOrUpdate(pos);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return pos;
    }

    public List<DBPosition> all(){
        List<DBPosition> result = new ArrayList<DBPosition>();
        try
        {
            List<DBPosition> d = positionDaoOpe.queryForAll();
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
