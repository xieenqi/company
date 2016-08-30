package com.loyo.oa.v2.activityui.worksheet.bean;

import com.loyo.oa.v2.beans.BaseBean;

import java.util.ArrayList;

/**
 * Created by EthanGong on 16/8/30.
 */
public class WorksheetTemplateListWrapper extends BaseBean{
    public ArrayList<WorksheetTemplate> data;

    public ArrayList<WorksheetTemplate> getList() {
        return data;
    }
}
