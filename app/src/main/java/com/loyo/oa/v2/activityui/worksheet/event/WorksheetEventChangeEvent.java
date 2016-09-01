package com.loyo.oa.v2.activityui.worksheet.event;

import com.loyo.oa.v2.activityui.worksheet.bean.Worksheet;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetEvent;
import com.loyo.oa.v2.beans.BaseBean;
import com.loyo.oa.v2.common.Event.BaseEvent;
import com.loyo.oa.v2.common.Event.CommonEvent;
import com.loyo.oa.v2.common.ExtraAndResult;

/**
 * Created by EthanGong on 16/9/1.
 */
public class WorksheetEventChangeEvent extends CommonEvent<WorksheetEvent>{

    public WorksheetEventChangeEvent() {
        eventCode = ExtraAndResult.WORKSHEET_EVENT_CHANGE;
    }
}
