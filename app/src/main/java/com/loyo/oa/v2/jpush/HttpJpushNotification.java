package com.loyo.oa.v2.jpush;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by pj on 15/12/28.
 */
public class HttpJpushNotification implements Serializable {
    public String buzzId;//工作报告【1】、任务【2】、日程【3】、项目【4】、通知公告【5】、客户【6】、轨迹规则【7】
    //组织架构【8】、个人信息【9】、产品【10】、客户拜访计划【11】快捷审批【12】通知公告【19】【订单详情】16
    // 拜访【22】、跟进【23】、拜访评论【24】、跟进评论【25】
    public int buzzType;
    public String operationType;//discuss/delete/finished/finished/update/expire/willexpire/redo/done/repeat/accept/reject/attachment/demand/share/remind
    public String uuid;//暂时没有用到
    public String pusherCognate = "";//调用接口回传给服务器跟新系统消息的红点状态
    public int silentType;
    public ArrayList<String> buzzIds;
    public int jumpType;//1.我负责的客户  2.我参与的客户 3.公海客户

    public enum JumpType {
        NUONE(0),
        MY_RESON(1),
        MY_MEMBER(2),
        COMMON_CUSTOMER(3);
        public int value;

        JumpType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public static final int JPUSH_TASK = 1;
    public static final int JPUSH_WORK_REPORT = 2;
    public static final int JPUSH_WFINSTANCE = 3;
    public static final int JPUSH_PROJECT = 4;
    public static final int JPUSH_BULLETIN = 5;
    public static final int JPUSH_CUSTOMER = 6;
    public static final int JPUSH_ORDER = 16;
    public static final int JPUSH_WORKSHEET = 18;
    public static final int JPUSH_WORKSHEET_EVENT = 19;
    public static final int JPUSH_SIGNIN = 22;
    public static final int JPUSH_SIGNIN_COMMENT = 24;
    public static final int JPUSH_FOLLOWUP = 23;
    public static final int JPUSH_FOLLOWUP_COMMENT = 25;
    public static final int JPUSH_EDIT_ORDER_RESPONSE = 28;//订单修改负责人
    /*线索修改负责人*/
    public static final int JPUSH_EDIT_CULE_RESPONSE = 29;


}
//{"operationType":"discuss","buzzId":"56a1c8a6526f152ed6000001","buzzType":1,"uuid":"e891057a-4b88-4b11-87dd-cfa99ed1a0f1"}
