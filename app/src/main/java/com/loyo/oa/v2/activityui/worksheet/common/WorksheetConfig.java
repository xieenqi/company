package com.loyo.oa.v2.activityui.worksheet.common;

import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetTemplate;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetTemplateListWrapper;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IWorksheet;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SharedUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by EthanGong on 16/8/30.
 */
public class WorksheetConfig {

    /* 从网络获取 */
    public static void fetchWorksheetTypes() {
        final List<WorksheetTemplate> data = new ArrayList<>();
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(IWorksheet.class).getWorksheetTypesList(new Callback<WorksheetTemplateListWrapper>() {
            @Override
            public void success(WorksheetTemplateListWrapper listWrapper, Response response) {
                HttpErrorCheck.checkResponse("类型列表：", response);
                if(0 == listWrapper.errcode && listWrapper.data != null){
                    for (WorksheetTemplate template : listWrapper.data) {
                        data.add(template);
                    }
                    String json = MainApp.gson.toJson(data);
                    SharedUtil.remove(MainApp.getMainApp(), ExtraAndResult.WORKSHEET_TYPES);
                    SharedUtil.put(MainApp.getMainApp(), ExtraAndResult.WORKSHEET_TYPES, json);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
            }
        });
    }

    /* 读取缓存 */
    public static ArrayList<WorksheetTemplate> getWorksheetTypes() {
        ArrayList<WorksheetTemplate> result =
                MainApp.getMainApp().gson.fromJson(
                        SharedUtil.get(MainApp.getMainApp(), ExtraAndResult.WORKSHEET_TYPES),
                        new TypeToken<ArrayList<WorksheetTemplate>>(){}.getType());
        return result;
    }

    // TODO:  增加网络获取的回调
    /* 读取缓存，如果没有缓冲，从网络获取 */
    public static ArrayList<WorksheetTemplate> getWorksheetTypes(boolean fetchIfEmpty) {
        ArrayList<WorksheetTemplate> result = null;
        String json = SharedUtil.get(MainApp.getMainApp(), ExtraAndResult.WORKSHEET_TYPES);
        result = MainApp.getMainApp().gson.fromJson(json, new TypeToken<ArrayList<WorksheetTemplate>>(){}.getType());

        if (fetchIfEmpty && result == null) {
            fetchWorksheetTypes();
        }

        return result;
    }

    /* 清除缓存 */
    private  static void clearWorksheetTypes() {
        SharedUtil.remove(MainApp.getMainApp(), ExtraAndResult.WORKSHEET_TYPES);
    }
}
