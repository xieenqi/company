package com.loyo.oa.v2.activityui.wfinstance.common;

import com.google.gson.reflect.TypeToken;
import com.loyo.oa.v2.activityui.other.model.Tag;
import com.loyo.oa.v2.activityui.wfinstance.bean.BizForm;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IWfInstance;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SharedUtil;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class WfinstanceBizformConfig {

    /* 从网络获取 */
    public static void getBizform() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageIndex", 1);
        params.put("pageSize", 2000);
        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWfInstance.class).getWfBizForms(params, new RCallback<PaginationX<BizForm>>() {
            @Override
            public void success(PaginationX<BizForm> result, Response response) {
                HttpErrorCheck.checkResponse("审批 config 自定义字段", response);
                String json = MainApp.gson.toJson(result.getRecords());
                SharedUtil.remove(MainApp.getMainApp(), ExtraAndResult.WFINSTANCE_BIZFORM);
                SharedUtil.put(MainApp.getMainApp(), ExtraAndResult.WFINSTANCE_BIZFORM, json);
            }
        });
    }

    /* 读取缓存 */
    public static ArrayList<BizForm> getBizformCache() {
        ArrayList<BizForm> result =
                MainApp.getMainApp().gson.fromJson(
                        SharedUtil.get(MainApp.getMainApp(), ExtraAndResult.WFINSTANCE_BIZFORM),
                        new TypeToken<ArrayList<BizForm>>() {
                        }.getType());
        if (result == null) {
            Global.Toast("审批类别数据在准备中,请退出重试");
            return new ArrayList<BizForm>();
        }
        return result;
    }

    // TODO:  增加网络获取的回调
    /* 读取缓存，如果没有缓冲，从网络获取 */
    public static ArrayList<BizForm> getBizform(boolean fetchIfEmpty) {
        ArrayList<BizForm> result = getBizformCache();

        if (fetchIfEmpty && result == null) {
            getBizform();
        }

        return result;
    }

    /* 清除缓存 */
    private static void clearBizform() {
        SharedUtil.remove(MainApp.getMainApp(), ExtraAndResult.WFINSTANCE_BIZFORM);
    }
}
