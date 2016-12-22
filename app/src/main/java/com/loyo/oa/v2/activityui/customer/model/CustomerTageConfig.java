package com.loyo.oa.v2.activityui.customer.model;

import com.google.gson.reflect.TypeToken;
import com.loyo.oa.v2.activityui.other.model.Tag;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.tool.SharedUtil;

import java.util.ArrayList;

public class CustomerTageConfig {

    /* 从网络获取 */
    public static void getTage() {
        CustomerService.getCustomerTags()
                .subscribe(new DefaultLoyoSubscriber<ArrayList<Tag>>(LoyoErrorChecker.SILENCE) {
                    @Override
                    public void onNext(ArrayList<Tag> tagArrayList) {
                        String json = MainApp.gson.toJson(tagArrayList);
                        SharedUtil.remove(MainApp.getMainApp(), ExtraAndResult.CUSTOMER_TAGE);
                        SharedUtil.put(MainApp.getMainApp(), ExtraAndResult.CUSTOMER_TAGE, json);
                    }
                });
    }

    // TODO:  增加网络获取的回调
    /* 读取缓存，如果没有缓冲，从网络获取 */
    public static ArrayList<Tag> getTage(boolean fetchIfEmpty) {
        ArrayList<Tag> result = getTageCache();

        if (fetchIfEmpty && result == null) {
            getTage();
        }

        return result;
    }

    /* 读取缓存 */
    public static ArrayList<Tag> getTageCache() {
        ArrayList<Tag> result =
                MainApp.getMainApp().gson.fromJson(
                        SharedUtil.get(MainApp.getMainApp(), ExtraAndResult.CUSTOMER_TAGE),
                        new TypeToken<ArrayList<Tag>>() {
                        }.getType());

        return result;
    }


    /* 清除缓存 */
    private static void clearTage() {
        SharedUtil.remove(MainApp.getMainApp(), ExtraAndResult.CUSTOMER_TAGE);
    }
}
