package com.loyo.oa.v2.activityui.followup.model;

import com.google.gson.reflect.TypeToken;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ISigninNeworFollowUp;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SharedUtil;

import java.util.ArrayList;

import retrofit.RetrofitError;
import retrofit.client.Response;


public class FolloUpConfig {

    /* 从网络获取 */
    public static void getFolloUpStage() {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISigninNeworFollowUp.class).
                getFollupFilters(new RCallback<ArrayList<FollowFilter>>() {
            @Override
            public void success(ArrayList<FollowFilter> result, Response response) {
                HttpErrorCheck.checkResponse("跟进 config 筛选 ：", response);
                    String json = MainApp.gson.toJson(result);
                    SharedUtil.remove(MainApp.getMainApp(), ExtraAndResult.FOLLOW_UP_STAGE);
                    SharedUtil.put(MainApp.getMainApp(), ExtraAndResult.FOLLOW_UP_STAGE, json);
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    /* 读取缓存 */
    public static ArrayList<FollowFilter>  getFolloUpStageCache() {
        ArrayList<FollowFilter>  result =
                MainApp.getMainApp().gson.fromJson(
                        SharedUtil.get(MainApp.getMainApp(), ExtraAndResult.FOLLOW_UP_STAGE),
                        new TypeToken<ArrayList<FollowFilter>>() {
                        }.getType());

        return result;
    }

    // TODO:  增加网络获取的回调
    /* 读取缓存，如果没有缓冲，从网络获取 */
    public static ArrayList<FollowFilter>  getFolloUpStage(boolean fetchIfEmpty) {
        ArrayList<FollowFilter>  result = null;
        String json = SharedUtil.get(MainApp.getMainApp(), ExtraAndResult.FOLLOW_UP_STAGE);
        result = MainApp.getMainApp().gson.fromJson(json, new TypeToken<ArrayList<FollowFilter> >() {
        }.getType());

        if (fetchIfEmpty && result == null) {
            getFolloUpStage();
        }

        return result;
    }

    /* 清除缓存 */
    private static void clearFolloUp() {
        SharedUtil.remove(MainApp.getMainApp(), ExtraAndResult.FOLLOW_UP_STAGE);
    }
}
