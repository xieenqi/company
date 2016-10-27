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
import com.loyo.oa.v2.db.bean.DBDepartment;

public class DepartmentDao {

    private Context context;
    private Dao<DBDepartment, String> departmentDaoOpe;
    private DatabaseHelper helper;

    public DepartmentDao(Context context)
    {
        this.context = context;
        try
        {
            helper = DatabaseHelper.getHelper(context);
            departmentDaoOpe = helper.getDao(DBDepartment.class);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public Dao<DBDepartment, String> getDao(){
        return departmentDaoOpe;
    }

    /**
     * 增加一个部门
     * @param dep
     */
    public void add(DBDepartment dep)
    {
        try
        {
            departmentDaoOpe.create(dep);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }

    }

    public DBDepartment get(String id)
    {
        DBDepartment dep = null;
        try
        {
            dep = departmentDaoOpe.queryForId(id);

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return dep;
    }

    public DBDepartment find(String id)
    {
        DBDepartment dep = null;
        try
        {
            dep = departmentDaoOpe.queryForId(id);

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return dep;
    }

    public DBDepartment findOrCreate(DBDepartment department)
    {
        DBDepartment dep = null;
        try
        {
            dep = departmentDaoOpe.queryForId(department.id);
            if (dep == null) {
                add(department);
                //dep = departmentDaoOpe.queryForId(department.id);
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return department;
    }

    public DBDepartment createOrUpdate(DBDepartment department)
    {
        try
        {
            departmentDaoOpe.createOrUpdate(department);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return department;
    }

    public DBDepartment createIfNotExists(DBDepartment department)
    {
        DBDepartment d = null;
        try
        {
            d = departmentDaoOpe.createIfNotExists(department);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return d;
    }

    public List<DBDepartment> all(){
        List<DBDepartment> result = new ArrayList<DBDepartment>();
        try
        {
            List<DBDepartment> d = departmentDaoOpe.queryForAll();
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
