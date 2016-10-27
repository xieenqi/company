package com.loyo.oa.v2.activityui.worksheet.event;

import com.loyo.oa.v2.activityui.worksheet.bean.Worksheet;
import com.loyo.oa.v2.common.event.CommonEvent;
import com.loyo.oa.v2.common.ExtraAndResult;

/**
 * Created by EthanGong on 16/9/1.
 */
public class WorksheetChangeEvent extends CommonEvent<Worksheet> {

    public WorksheetChangeEvent() {
        eventCode = ExtraAndResult.WORKSHEET_CHANGE;
    }
}
