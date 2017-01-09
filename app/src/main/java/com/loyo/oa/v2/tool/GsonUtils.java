package com.loyo.oa.v2.tool;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loyo.oa.v2.activityui.setting.bean.SystemMessageItemType;
import com.loyo.oa.v2.activityui.wfinstance.common.SubmitStatus;
import com.loyo.oa.v2.activityui.worksheet.common.WSRole;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetEventAction;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetEventStatus;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetStatus;

/**
 * Created by EthanGong on 16/8/29.
 */
public class GsonUtils {

    public static Gson newInstance() {
        GsonBuilder builder = new GsonBuilder();

        /* 工单状态 */
        builder.registerTypeAdapter(WorksheetStatus.class,
                new WorksheetStatus.EnumSerializer());

        /* 审批状态 */
        builder.registerTypeAdapter(SubmitStatus.class,
                new SubmitStatus.EnumSerializer());

        /* 工单事件状态 */
        builder.registerTypeAdapter(WorksheetEventStatus.class,
                new WorksheetEventStatus.EnumSerializer());

        /* 工单事件操作 */
        builder.registerTypeAdapter(WorksheetEventAction.class,
                new WorksheetEventAction.EnumSerializer());

        /* 工单角色 */
        builder.registerTypeAdapter(WSRole.class,
                new WSRole.WSRoleSerializer());

        /*  */
        builder.registerTypeAdapter(SystemMessageItemType.class,
                new SystemMessageItemType.SystemMessageSerializer());
        return builder.create();
    }
}