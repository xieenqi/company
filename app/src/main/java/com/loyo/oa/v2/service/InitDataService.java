package com.loyo.oa.v2.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Department;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.point.IUser;
import com.loyo.oa.v2.tool.ListUtil;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import org.androidannotations.annotations.EIntentService;

import java.util.ArrayList;

import retrofit.client.Response;

/**App  一启动 就开启服务 调用户的数据
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

        try {
            getOrganization();
        } catch (Exception ex) {
            Global.ProcException(ex);
        }
        RestAdapterFactory.getInstance().build(FinalVariables.GET_PROFILE).create(IUser.class).getProfile(new RCallback<User>() {
            @Override
            public void success(User user, Response response) {
                String json = MainApp.gson.toJson(user);
                MainApp.user = user;
                DBManager.Instance().putUser(json);
                sendDataChangeBroad(user);
            }
        });
    }

    /** 后台 更新 组织架构
     *
     */
    void getOrganization() {
        ArrayList<Department> lstDepartment_current = RestAdapterFactory.getInstance().build(FinalVariables.GET_ORGANIZATION)
                .create(IUser.class).getOrganization();

        if (!ListUtil.IsEmpty(lstDepartment_current)) {
            LogUtil.d("更新 组织 架构 json：" + MainApp.gson.toJson(lstDepartment_current));
            //写DB
            DBManager.Instance().putOrganization(MainApp.gson.toJson(lstDepartment_current));
            //设置缓存
            Common.setLstDepartment(lstDepartment_current);

            for(int i = 0;i<lstDepartment_current.size();i++){
                for(User user : lstDepartment_current.get(i).getUsers()){
                    LogUtil.dll("部门大小aaaaa:"+user.getDepts().size());
                }
            }
        }
    }

    /**
     * 发送数据变化的广播
     * @param user
     */
    private void sendDataChangeBroad(User user){
        Intent intent=new Intent();
        intent.setAction(FinalVariables.ACTION_DATA_CHANGE);
        intent.putExtra("user",user);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
