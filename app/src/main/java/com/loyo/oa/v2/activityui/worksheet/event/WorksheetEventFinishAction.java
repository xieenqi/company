package com.loyo.oa.v2.activityui.worksheet.event;

import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetEvent;
import com.loyo.oa.v2.common.event.CommonEvent;

/**
 * Created by EthanGong on 16/9/6.
 */
public class WorksheetEventFinishAction extends CommonEvent<WorksheetEvent> {
    final public static int FROM_RESPONSABLE_LIST = 1;
    final public static int FROM_SEARCH_LIST = 2;

}
