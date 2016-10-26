package com.loyo.oa.v2.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.loyo.oa.v2.activityui.commonview.bean.NewUser;
import com.loyo.oa.v2.activityui.other.model.User;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Permission;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.event.AppBus;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.point.IUser;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import org.androidannotations.annotations.EIntentService;
import java.util.HashMap;
import retrofit.RetrofitError;
import retrofit.client.Response;

// Add by ethan on 2016-08-03

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
        RestAdapterFactory.getInstance().build(FinalVariables.GET_PROFILE).create(IUser.class).getProfile(new RCallback<NewUser>() {
            @Override
            public void success(NewUser user, Response response) {
                try {
                    if (Config_project.is_developer_mode)
                        HttpErrorCheck.checkResponse("获取user", response);
                    String json = MainApp.gson.toJson(user);
                    MainApp.user = user.data;
                    setRootMap(user.data);
                    DBManager.Instance().putUser(json);//保存用户信息
//                    HashMap<String, String> map = new HashMap<>();
//                    map.put("name", user.name);
//                    map.put("id", user.id);
                    sendDataChangeBroad(user.data);
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
//        try {
//            //all或者one
//            String organizationUpdateInfo = SharedUtil.get(MainApp.getMainApp(), ExtraAndResult.IS_ORGANIZATION_UPDATE);
//            //open或者run
//            String appStatus = SharedUtil.get(MainApp.getMainApp(), ExtraAndResult.APP_START);
//            if ("one".equals(organizationUpdateInfo) || "openOne".equals(appStatus)) {//个人信息变动
//                getOrganization();
//            } else if ("all".equals(organizationUpdateInfo) && "open".equals(appStatus)) {//启动app是 之前组织架构有变动
//                getOrganization();
//            } else if ("all".equals(organizationUpdateInfo) && "run".equals(appStatus)) {//手动跟新数据
//                getOrganization();
//            }
//        } catch (Exception ex) {
//            Global.ProcException(ex);
//        }
    }

    /**
     * 权限数据保存为Map
     */
    void setRootMap(User user) {
        HashMap<String, Permission> map = new HashMap<>();
        if (user != null && null != user.permissionGroup) {
            for (Permission permission : user.permissionGroup.topModules) {
                map.put(permission.getCode(), permission);
            }
            for (Permission permission : user.permissionGroup.subModules) {
                map.put(permission.getCode(), permission);
            }

            MainApp.rootMap = map;
            LogUtil.d("x相关权限:" + MainApp.gson.toJson(map));
        }
    }

//    /**
//     * <<<<<<< HEAD
//     * =======
//     * 后台 更新 组织架构
//     */
//    void getOrganization() {
//        ArrayList<Department> lstDepartment_current = RestAdapterFactory.getInstance().build(FinalVariables.GET_ORGANIZATION)
//                .create(IUser.class).getOrganization();
//
//        if (!ListUtil.IsEmpty(lstDepartment_current)) {
//            //写DB
//            DBManager.Instance().putOrganization(MainApp.gson.toJson(lstDepartment_current));
//            //设置缓存
//            Common.setLstDepartment(lstDepartment_current);
//            LogUtil.d("更新 组织《《《《《《《《《《《《《《《《》》》》》》》》》》》 架构 json：完成");
//            SharedUtil.remove(MainApp.getMainApp(), ExtraAndResult.IS_ORGANIZATION_UPDATE);
//            SharedUtil.remove(MainApp.getMainApp(), ExtraAndResult.APP_START);
//            //清除之前缓存通讯录部门的数据
//            SharedUtil.remove(MainApp.getMainApp(), ExtraAndResult.ORGANIZATION_DEPARTENT);
//        } else {
//            LogUtil.d("更新 组织 架构  失败");
//            Global.Toast("数据更新失败,稍后重试");
//        }
//    }

    /**
     * >>>>>>> develop-bug-fix2
     * 发送数据变化的广播
     *
     * @param user
     */
    private void sendDataChangeBroad(User user) {
        Intent intent = new Intent();
        intent.setAction(FinalVariables.ACTION_DATA_CHANGE);
        intent.putExtra("user", user);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        AppBus.getInstance().post(user);
    }
}
