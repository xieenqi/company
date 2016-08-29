package com.loyo.oa.v2.activityui.worksheet.common;

/**
 * Created by EthanGong on 16/8/27.
 */
public interface GroupKey {

    /** 获取排序权值 */
    public int compareWeight();

    /** 获取显示内容 */
    public String getName();

    /** 获取显示颜色*/
    public int getColor();

    /** 获取显示图标*/
    public int getIcon();
}
