package com.loyo.oa.v2.activityui.wfinstance.common;

import com.google.gson.reflect.TypeToken;
import com.loyo.oa.v2.activityui.wfinstance.api.WfinstanceService;
import com.loyo.oa.v2.activityui.wfinstance.bean.BizForm;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.tool.SharedUtil;
import java.util.ArrayList;
import java.util.HashMap;

public class WfinstanceBizformConfig {

    /* 从网络获取 */
    public static void getBizform() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageIndex", 1);
        params.put("pageSize", 2000);
        WfinstanceService.getWfBizForms(params)
                .subscribe(new DefaultLoyoSubscriber<PaginationX<BizForm>>(LoyoErrorChecker.SILENCE) {
            @Override
            public void onNext(PaginationX<BizForm> bizFormPaginationX) {
                if (null != bizFormPaginationX) {
                    String json = MainApp.gson.toJson(bizFormPaginationX.getRecords());
                    SharedUtil.remove(MainApp.getMainApp(), ExtraAndResult.WFINSTANCE_BIZFORM);
                    SharedUtil.put(MainApp.getMainApp(), ExtraAndResult.WFINSTANCE_BIZFORM, json);
                }
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
