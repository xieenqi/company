package com.loyo.oa.v2.activityui.worksheet.common;

import com.google.gson.reflect.TypeToken;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetTemplate;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.tool.SharedUtil;
import com.loyo.oa.v2.worksheet.api.WorksheetService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EthanGong on 16/8/30.
 */
public class WorksheetConfig {

    /* 从网络获取 */
    public static void fetchWorksheetTypes() {
        final List<WorksheetTemplate> data = new ArrayList<>();
        WorksheetService.getWorksheetTypesList()
                .subscribe(new DefaultLoyoSubscriber<ArrayList<WorksheetTemplate>>(LoyoErrorChecker.SILENCE) {
                    @Override
                    public void onNext(ArrayList<WorksheetTemplate> templates) {
                        if (templates == null) {
                            return;
                        }
                        for (WorksheetTemplate template : templates) {
                            data.add(template);
                        }
                        String json = MainApp.gson.toJson(data);
                        SharedUtil.remove(MainApp.getMainApp(), ExtraAndResult.WORKSHEET_TYPES);
                        SharedUtil.put(MainApp.getMainApp(), ExtraAndResult.WORKSHEET_TYPES, json);
                    }
                });
    }

    /* 读取缓存 */
    public static ArrayList<WorksheetTemplate> getWorksheetTypes() {
        ArrayList<WorksheetTemplate> raw =
                MainApp.getMainApp().gson.fromJson(
                        SharedUtil.get(MainApp.getMainApp(), ExtraAndResult.WORKSHEET_TYPES),
                        new TypeToken<ArrayList<WorksheetTemplate>>() {
                        }.getType());


        ArrayList<WorksheetTemplate> result = new ArrayList<WorksheetTemplate>();
        if (raw != null) {
            for (int i = 0; i < raw.size(); i++) {
                if (raw.get(i).enabled) {
                    result.add(raw.get(i));
                }
            }
        }

        return result;
    }

    // TODO:  增加网络获取的回调
    /* 读取缓存，如果没有缓冲，从网络获取 */
    public static ArrayList<WorksheetTemplate> getWorksheetTypes(boolean fetchIfEmpty) {
        ArrayList<WorksheetTemplate> raw = null;
        String json = SharedUtil.get(MainApp.getMainApp(), ExtraAndResult.WORKSHEET_TYPES);
        raw = MainApp.getMainApp().gson.fromJson(json, new TypeToken<ArrayList<WorksheetTemplate>>() {
        }.getType());

        if (fetchIfEmpty && raw == null) {
            fetchWorksheetTypes();
        }

        ArrayList<WorksheetTemplate> result = new ArrayList<WorksheetTemplate>();
        if (raw != null) {
            for (int i = 0; i < raw.size(); i++) {
                if (raw.get(i).enabled) {
                    result.add(raw.get(i));
                }
            }
        }

        return result;
    }

    /* 清除缓存 */
    private static void clearWorksheetTypes() {
        SharedUtil.remove(MainApp.getMainApp(), ExtraAndResult.WORKSHEET_TYPES);
    }
}
