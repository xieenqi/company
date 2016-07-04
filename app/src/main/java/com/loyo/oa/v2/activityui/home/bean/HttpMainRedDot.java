package com.loyo.oa.v2.activityui.home.bean;

import java.io.Serializable;

/**
 * CreatedXNQ16/1/11.
 */
public class HttpMainRedDot implements Serializable {
    public int bizType;//工作报告【1】、任务【2】、日程【3】、外勤【4】、项目【5】、客户【6】、客户联系人【7】
    // 、销售机会【8】、销售动态【9】、产品【10】、客户拜访计划【11】快捷审批【12】通知公告【19】
    public String id;
    public boolean viewed;
    public int bizNum;
}
