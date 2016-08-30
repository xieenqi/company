package com.loyo.oa.v2.tool;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetEventStatus;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetStatus;

/**
 * Created by EthanGong on 16/8/29.
 */
public class GsonUtils {

    public static Gson newInstance() {
        GsonBuilder builder = new GsonBuilder();

        builder.registerTypeAdapter(WorksheetStatus.class,
                new WorksheetStatus.EnumSerializer());
        builder.registerTypeAdapter(WorksheetEventStatus.class,
                new WorksheetEventStatus.EnumSerializer());

        return builder.create();
    }
}