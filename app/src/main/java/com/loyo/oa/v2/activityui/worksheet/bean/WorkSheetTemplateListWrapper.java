package com.loyo.oa.v2.activityui.worksheet.bean;

import com.loyo.oa.v2.beans.BaseBean;

import java.util.ArrayList;

/**
 * Created by EthanGong on 16/8/30.
 */
public class WorkSheetTemplateListWrapper extends BaseBean{
    public ArrayList<WorkSheetTemplate> data;

    public ArrayList<WorkSheetTemplate> getList() {
        return data;
    }
}
