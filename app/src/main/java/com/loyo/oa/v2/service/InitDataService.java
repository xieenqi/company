package com.loyo.oa.v2.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.activityui.customer.bean.Department;
import com.loyo.oa.v2.beans.Permission;
import com.loyo.oa.v2.activityui.other.bean.User;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.db.bean.DBDepartment;
import com.loyo.oa.v2.point.IUser;
import com.loyo.oa.v2.tool.ListUtil;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SharedUtil;

// Add by ethan on 2016-08-03
import com.loyo.oa.v2.db.*;

import org.androidannotations.annotations.EIntentService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【组织架构】后台拉取服务
 * xnq
 */
@EIntentService
public class InitDataService extends IntentService {

    MainApp app;

    public InitDataService() {
        super("InitDataService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        app = (MainApp) getApplicationContext();
        if (null != DBManager.Instance().getUser()) {
            setRootMap(DBManager.Instance().getUser());//取缓存的用户对象
        }
        RestAdapterFactory.getInstance().build(FinalVariables.GET_PROFILE).create(IUser.class).getProfile(new RCallback<User>() {
            @Override
            public void success(User user, Response response) {
                try {
                    HttpErrorCheck.checkResponse("获取user", response);
                    String json = MainApp.gson.toJson(user);
                    MainApp.user = user;
                    setRootMap(user);
                    sendDataChangeBroad(user);
                    DBManager.Instance().putUser(json);//保存用户信息
                    HashMap<String, String> map = new HashMap<>();
                    map.put("name", user.name);
                    map.put("id", user.id);
                } catch (Exception E) {
                    E.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });
        try {
            //all或者one
            String organizationUpdateInfo = SharedUtil.get(MainApp.getMainApp(), ExtraAndResult.IS_ORGANIZATION_UPDATE);
            //open或者run
            String appStatus = SharedUtil.get(MainApp.getMainApp(), ExtraAndResult.APP_START);
            if ("one".equals(organizationUpdateInfo) || "openOne".equals(appStatus)) {//个人信息变动
                getOrganization();
            } else if ("all".equals(organizationUpdateInfo) && "open".equals(appStatus)) {//启动app是 之前组织架构有变动
                getOrganization();
            } else if ("all".equals(organizationUpdateInfo) && "run".equals(appStatus)) {//手动跟新数据
                getOrganization();
            }
        } catch (Exception ex) {
            Global.ProcException(ex);
        }
    }

    /**
     * 权限数据保存为Map
     */
    void setRootMap(User user) {
        HashMap<String, Object> map = new HashMap<>();
        if (null != user.newpermission) {
            for (Permission permission : user.newpermission) {
                map.put(permission.getCode(), permission);
            }
            MainApp.rootMap = map;
        }
    }

    /**
     * 后台 更新 组织架构
     */
    void getOrganization() {
        ArrayList<Department> lstDepartment_current = RestAdapterFactory.getInstance().build(FinalVariables.GET_ORGANIZATION)
                .create(IUser.class).getOrganization();

        if (!ListUtil.IsEmpty(lstDepartment_current)) {
            //写DB
            String jsonString = MainApp.gson.toJson(lstDepartment_current);
            DBManager.Instance().putOrganization(jsonString);

            Log.v("debug", jsonString);

            /*
             * 写入结构化数据到数据库
             * Add by ethan
             */
            OrganizationManager.shareManager(getBaseContext()).saveOrgnizitionToDB(jsonString);

            //设置缓存
            Common.setLstDepartment(lstDepartment_current);
            LogUtil.d("更新 组织《《《《《《《《《《《《《《《《》》》》》》》》》》》 架构 json：完成");
            SharedUtil.remove(MainApp.getMainApp(), ExtraAndResult.IS_ORGANIZATION_UPDATE);
            SharedUtil.remove(MainApp.getMainApp(), ExtraAndResult.APP_START);
            //清除之前缓存通讯录部门的数据
            SharedUtil.remove(MainApp.getMainApp(), ExtraAndResult.ORGANIZATION_DEPARTENT);
        } else {
            LogUtil.d("更新 组织 架构  失败");
        }
    }

    /**
     * 发送数据变化的广播
     *
     * @param user
     */
    private void sendDataChangeBroad(User user) {
        Intent intent = new Intent();
        intent.setAction(FinalVariables.ACTION_DATA_CHANGE);
        intent.putExtra("user", user);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
