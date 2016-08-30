package com.loyo.oa.v2.tool;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loyo.oa.v2.activityui.worksheet.common.WorkSheetEventStatus;
import com.loyo.oa.v2.activityui.worksheet.common.WorkSheetStatus;

/**
 * Created by EthanGong on 16/8/29.
 */
public class GsonUtils {

    public static Gson newInstance() {
        GsonBuilder builder = new GsonBuilder();

        builder.registerTypeAdapter(WorkSheetStatus.class,
                new WorkSheetStatus.EnumSerializer());
        builder.registerTypeAdapter(WorkSheetEventStatus.class,
                new WorkSheetEventStatus.EnumSerializer());

        return builder.create();
    }
}