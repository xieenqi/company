package com.loyo.oa.v2.jpush;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by pj on 15/12/28.
 */
public class HttpJpushNotification implements Serializable {
    public String buzzId;//工作报告【1】、任务【2】、日程【3】、外勤【4】、项目【5】、客户【6】、客户联系人【7】
    // 、销售机会【8】、销售动态【9】、产品【10】、客户拜访计划【11】快捷审批【12】通知公告【19】
    public int buzzType;
    public String operationType;//discuss/delete/finished/finished/update/expire/willexpire/redo/done/repeat/accept/reject/attachment/demand/share/remind
    public String uuid;//暂时没有用到
    public int silentType;
    public ArrayList<String> buzzIds;

}
//{"operationType":"discuss","buzzId":"56a1c8a6526f152ed6000001","buzzType":1,"uuid":"e891057a-4b88-4b11-87dd-cfa99ed1a0f1"}
