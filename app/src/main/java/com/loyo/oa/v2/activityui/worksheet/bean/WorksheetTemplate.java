package com.loyo.oa.v2.activityui.worksheet.bean;

public class WorksheetTemplate {

    public static WorksheetTemplate Null;
    static {
        Null = new WorksheetTemplate();
        Null.name = "全部类型";
        Null.enabled = true;
    }

    public String id;
    public boolean enabled;
    public String name;
    public boolean isSystem;
    public boolean hasItems;

}